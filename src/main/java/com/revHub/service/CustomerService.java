package com.revHub.service;

import com.revHub.dto.request.CustomerModifyRequestDTO;
import com.revHub.dto.request.CustomerSaveRequestDTO;
import com.revHub.dto.request.CustomerSearchRequestDTO;
import com.revHub.dto.response.CustomerContactNumberEmailIdsResponseDto;
import com.revHub.dto.response.CustomerResponseProjection;
import com.revHub.dto.response.CustomerTableViewProjection;
import com.revHub.util.StandardResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.util.List;

public interface CustomerService {
    void saveCustomerDetails(CustomerSaveRequestDTO request);

    CustomerResponseProjection getCustomerByContactNumber(String contactNumber);

    void updateCustomer(CustomerModifyRequestDTO customerModifyRequestDTO);

    List<CustomerContactNumberEmailIdsResponseDto> getAllCustomerContactNumberEmailIdsList();

    Page<CustomerTableViewProjection> getAllCustomerSummariesPaginated(CustomerSearchRequestDTO request, Pageable pageable);

    CustomerResponseProjection getCustomerByCustomerId(Long customerId);
}
