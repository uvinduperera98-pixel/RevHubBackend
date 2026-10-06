package com.revHub.service.impl;

import com.revHub.dto.request.VehicleModifyRequestDTO;
import com.revHub.dto.request.VehicleSearchRequestDTO;
import com.revHub.dto.response.*;
import com.revHub.entity.Customer;
import com.revHub.entity.Vehicle;
import com.revHub.entity.VehicleHistory;
import com.revHub.exception.BadRequestException;
import com.revHub.exception.NotFoundException;
import com.revHub.integration.vehiclesdb.repository.VehicleMakeRepository;
import com.revHub.integration.vehiclesdb.repository.VehicleModelRepository;
import com.revHub.repository.CustomerRepository;
import com.revHub.repository.VehicleHistoryRepository;
import com.revHub.repository.VehicleRepository;
import com.revHub.service.VehicleService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehicleServiceImpl implements VehicleService {

    private final VehicleRepository vehicleRepository;
    private final CustomerRepository customerRepository;
    private final VehicleHistoryRepository vehicleHistoryRepository;
    private final VehicleMakeRepository vehicleMakeRepository;
    private final VehicleModelRepository vehicleModelRepository;


    public VehicleServiceImpl(VehicleRepository vehicleRepository, CustomerRepository customerRepository, VehicleHistoryRepository vehicleHistoryRepository, VehicleMakeRepository vehicleMakeRepository, VehicleModelRepository vehicleModelRepository) {
        this.vehicleRepository = vehicleRepository;
        this.customerRepository = customerRepository;
        this.vehicleHistoryRepository = vehicleHistoryRepository;
        this.vehicleMakeRepository = vehicleMakeRepository;
        this.vehicleModelRepository = vehicleModelRepository;
    }

    @Override
    public VehicleAndCustomerResponseDTO getVehicleAndCustomerByVehicleRegNumber(String vehicleRegNumber) {
        Vehicle vehicle = vehicleRepository.findVehiclesByVehicleRegNo(vehicleRegNumber);

        if (vehicle == null) {
            throw new NotFoundException("Vehicle with registration number " + vehicleRegNumber + " not found.");
        }

        VehicleAndCustomerResponseDTO response = new VehicleAndCustomerResponseDTO();
        response.setVehicleRegNo(vehicle.getVehicleRegNo());
        response.setVehicleMake(vehicle.getVehicleMake());
        response.setVehicleModel(vehicle.getVehicleModel());
        response.setVehicleYear(vehicle.getVehicleYear());
        response.setColour(vehicle.getColour());
        response.setOtherSpecs(vehicle.getOtherSpecs());

        Customer customer = vehicle.getCustomer();

        if (customer != null) {
            response.setCustomerName(customer.getCustomerName());
            response.setDrivingLicenseNumber(customer.getDrivingLicenseNumber());
            response.setContactNumbers(customer.getContactNumber());
            response.setEmail(customer.getEmail());
            response.setCustomerAddress(customer.getCustomerAddress());
        }

        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<VehicleTableViewResponseProjection> getAllVehiclesSummaryPaginated(VehicleSearchRequestDTO request, Pageable pageable) {

        if (pageable.getSort().stream().anyMatch(order -> order.getProperty().equalsIgnoreCase("string"))) {
            pageable = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(), Sort.by("createdDate").descending());
        }

        return vehicleRepository.findAllVehiclesPaginated(request.getVehicleRegNo(), request.getVehicleVinNo(), pageable);
    }

    @Override
    public VehicleResponseProjection getVehicleByVehicleId(Long vehicleId) {
        return vehicleRepository.findVehicleByVehicleId(vehicleId)
                .orElseThrow(() -> new NotFoundException("Vehicle not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateVehicle(VehicleModifyRequestDTO dto) {
        Vehicle vehicle = vehicleRepository.findVehiclesByVehicleRegNo(dto.getVehicleRegNo());

        if (vehicle == null) {
            throw new NotFoundException(
                    "Vehicle with registration number " + dto.getVehicleRegNo() + " not found."
            );
        }

        // Save previous state before modifying
        VehicleHistory history = new VehicleHistory();
        history.setVehicleId(vehicle.getVehicleId());
        history.setCustomerId(vehicle.getCustomer() != null ? vehicle.getCustomer().getCustomerId() : null);
        history.setVehicleRegNo(vehicle.getVehicleRegNo());
        history.setVehicleVinNo(vehicle.getVehicleVinNo());
        history.setVehicleMake(vehicle.getVehicleMake());
        history.setVehicleModel(vehicle.getVehicleModel());
        history.setVehicleYear(vehicle.getVehicleYear());
        history.setColour(vehicle.getColour());
        history.setOtherSpecs(vehicle.getOtherSpecs());
        history.setActionType("UPDATE");

        vehicleHistoryRepository.save(history);

        // Resolve customer
        Customer assignedCustomer;

        if (dto.getCustomerId() > 0) {
            assignedCustomer = customerRepository.findById(dto.getCustomerId())
                    .orElseThrow(() -> new NotFoundException(
                            "Customer not found with ID: " + dto.getCustomerId()
                    ));
        } else if (dto.getCustomer() != null) {
            Customer newCustomer = new Customer();
            newCustomer.setCustomerName(dto.getCustomer().getCustomerName());
            newCustomer.setCustomerAddress(dto.getCustomer().getCustomerAddress());
            newCustomer.setContactNumber(dto.getCustomer().getContactNumber());
            newCustomer.setActive(dto.getCustomer().getActive());
            newCustomer.setDrivingLicenseNumber(dto.getCustomer().getDrivingLicenseNumber());
            newCustomer.setEmail(dto.getCustomer().getEmail());

            assignedCustomer = customerRepository.save(newCustomer);
        } else {
            throw new BadRequestException("Customer data missing!");
        }

        // Update vehicle
        vehicle.setVehicleMake(dto.getVehicleMake());
        vehicle.setVehicleModel(dto.getVehicleModel());
        vehicle.setVehicleYear(dto.getVehicleYear());
        vehicle.setColour(dto.getColour());
        vehicle.setOtherSpecs(dto.getOtherSpecs());
        vehicle.setCustomer(assignedCustomer);

        vehicleRepository.save(vehicle);
    }

    @Override
    public VehicleAndCustomerResponseDTO getVehicleAndCustomerByVehicleVinNumber(String vehicleVinNumber) {
        Vehicle vehicle = vehicleRepository.findVehiclesByVehicleVinNo(vehicleVinNumber);

        if (vehicle == null) {
            throw new NotFoundException("Vehicle with VIN number " + vehicleVinNumber + " not found.");
        }

        VehicleAndCustomerResponseDTO response = new VehicleAndCustomerResponseDTO();
        response.setVehicleRegNo(vehicle.getVehicleRegNo());
        response.setVehicleVinNo(vehicle.getVehicleVinNo());
        response.setVehicleMake(vehicle.getVehicleMake());
        response.setVehicleModel(vehicle.getVehicleModel());
        response.setVehicleYear(vehicle.getVehicleYear());
        response.setColour(vehicle.getColour());
        response.setOtherSpecs(vehicle.getOtherSpecs());

        Customer customer = vehicle.getCustomer();

        if (customer != null) {
            response.setCustomerName(customer.getCustomerName());
            response.setDrivingLicenseNumber(customer.getDrivingLicenseNumber());
            response.setContactNumbers(customer.getContactNumber());
            response.setEmail(customer.getEmail());
            response.setCustomerAddress(customer.getCustomerAddress());
        }

        return response;
    }

    @Override
    public List<String> getVehicleRegNoList() {
        return vehicleRepository.findAllVehicleRegNos();
    }

    @Override
    public List<String> getVehicleVinNoList() {
        return vehicleRepository.findAllVehicleVinNos();
    }

    @Override
    public List<VehicleMakeResponseDTO> getAllVehicleMakes() {
        return vehicleMakeRepository.findAllVehicleMakeIdAndName();
    }

    @Override
    public List<VehicleModelResponseDTO> getAllVehicleModelsByMake(Long makeId) {
        return vehicleModelRepository.findAllVehicleModelIdAndNameByMakeId(makeId);
    }
}
