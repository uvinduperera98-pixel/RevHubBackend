package com.revHub.service.impl;

import com.revHub.dto.request.CustomerModifyRequestDTO;
import com.revHub.dto.request.CustomerSaveRequestDTO;
import com.revHub.dto.request.CustomerSearchRequestDTO;
import com.revHub.dto.response.CustomerContactNumberEmailIdsResponseDto;
import com.revHub.dto.response.CustomerResponseProjection;
import com.revHub.dto.response.CustomerTableViewProjection;
import com.revHub.entity.Customer;
import com.revHub.entity.CustomerHistory;
import com.revHub.exception.DuplicateException;
import com.revHub.exception.NotFoundException;
import com.revHub.repository.CustomerHistoryRepository;
import com.revHub.repository.CustomerRepository;
import com.revHub.service.CustomerService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerHistoryRepository customerHistoryRepository;

    public CustomerServiceImpl(CustomerRepository customerRepository, CustomerHistoryRepository customerHistoryRepository) {
        this.customerRepository = customerRepository;
        this.customerHistoryRepository = customerHistoryRepository;
    }

    @Override
    public void saveCustomerDetails(CustomerSaveRequestDTO request) {

        Customer customer = new Customer();

        customer.setCustomerName(request.getCustomerName());
        customer.setCustomerAddress(request.getCustomerAddress());
        customer.setEmail(request.getEmail());
        customer.setContactNumber(request.getContactNumber());
        customer.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        customer.setActive(true);

        customerRepository.save(customer);
    }

    @Override
    public CustomerResponseProjection getCustomerByContactNumber(String contactNumber) {
        return customerRepository.findByContactNumberAndActive(contactNumber, true)
                .orElseThrow(() ->
                        new NotFoundException("Customer not found"));
    }

    @Override
    public CustomerResponseProjection getCustomerByCustomerId(Long customerId) {
        return customerRepository.findByCustomerByCustomerId(customerId)
                .orElseThrow(() ->
                        new NotFoundException("Customer not found"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateCustomer(CustomerModifyRequestDTO request) {

        Customer customer = customerRepository
                .findByCustomerId(request.getCustomerId())
                .orElseThrow(() ->
                        new NotFoundException("Customer not found"));

        if (customerRepository.existsByEmailAndCustomerIdNot(
                request.getEmail(),
                request.getCustomerId())) {

            throw new DuplicateException("Email already exists");
        }

        // Save previous state
        CustomerHistory history = new CustomerHistory();

        history.setCustomerId(customer.getCustomerId());
        history.setCustomerName(customer.getCustomerName());
        history.setCustomerAddress(customer.getCustomerAddress());
        history.setEmail(customer.getEmail());
        history.setContactNumber(customer.getContactNumber());
        history.setDrivingLicenseNumber(customer.getDrivingLicenseNumber());
        history.setActive(customer.getActive());
        history.setActionType("UPDATE");

        customerHistoryRepository.save(history);

        // Update customer
        customer.setEmail(request.getEmail());
        customer.setCustomerName(request.getCustomerName());
        customer.setDrivingLicenseNumber(request.getDrivingLicenseNumber());
        customer.setCustomerAddress(request.getCustomerAddress());
        customer.setActive(request.getActive());

        customerRepository.save(customer);
    }

    @Override
    public List<CustomerContactNumberEmailIdsResponseDto> getAllCustomerContactNumberEmailIdsList() {
        return customerRepository.findAllCustomerContactNumberEmailIdsList();
    }

    @Override
    public Page<CustomerTableViewProjection> getAllCustomerSummariesPaginated(CustomerSearchRequestDTO request, Pageable pageable) {

        boolean hasInvalidSort = pageable.getSort()
                .stream()
                .anyMatch(order ->
                        order.getProperty().equals("string") ||
                                order.getProperty().equals("created_date")
                );

        if (hasInvalidSort || pageable.getSort().isUnsorted()) {

            pageable = PageRequest.of(
                    pageable.getPageNumber(),
                    pageable.getPageSize(),
                    Sort.by("createdDate").descending()
            );
        }

        return customerRepository.findAllCustomerSummaries(
                request.getContactNumber(),
                request.getEmail(),
                request.getActiveStatus(),
                pageable
        );
    }
}
