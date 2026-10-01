package com.revHub.controller;


import com.revHub.dto.request.CustomerModifyRequestDTO;
import com.revHub.dto.request.CustomerSaveRequestDTO;
import com.revHub.dto.request.CustomerSearchRequestDTO;
import com.revHub.dto.response.CustomerContactNumberEmailIdsResponseDto;
import com.revHub.dto.response.CustomerResponseProjection;
import com.revHub.dto.response.CustomerTableViewProjection;
import com.revHub.service.CustomerService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin
@RestController
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    CustomerService customerService;

    @PostMapping("/save")
    public ResponseEntity<StandardResponse> saveCustomer(@RequestBody CustomerSaveRequestDTO request) {
        customerService.saveCustomerDetails(request);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        new StandardResponse(
                                HttpStatus.CREATED.value(),
                                "Customer saved successfully",
                                null
                        )
                );
    }

    @PutMapping("/modify")
    public ResponseEntity<StandardResponse> updateCustomer(@RequestBody CustomerModifyRequestDTO request) {
        customerService.updateCustomer(request);
        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Customer updated successfully",
                        null
                )
        );
    }

    @GetMapping("/get-customer-by-contact-number/{contactNumber}")
    public ResponseEntity<StandardResponse> getCustomerByContactNumber(@PathVariable String contactNumber) {

        CustomerResponseProjection customer = customerService.getCustomerByContactNumber(contactNumber);
        return ResponseEntity.ok(new StandardResponse(HttpStatus.OK.value(),
                "Customer retrieved successfully", customer)
        );
    }

    @PostMapping("/get-all-customers-summary")
    public ResponseEntity<StandardResponse> getAllCustomerSummariesPaginated(
            @RequestBody CustomerSearchRequestDTO request,
            @PageableDefault(
                    size = 5,
                    sort = "createdDate",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        Page<CustomerTableViewProjection> customerPage = customerService.getAllCustomerSummariesPaginated(
                        request,
                        pageable
                );

        return ResponseEntity.ok(new StandardResponse(
                        HttpStatus.OK.value(),
                        "Customer summaries retrieved successfully",
                        customerPage
                )
        );
    }

    @GetMapping("get-customer-by-customerId/{customerId}")
    public ResponseEntity<StandardResponse> getCustomerByCustomerId(
        @PathVariable Long customerId) {
        CustomerResponseProjection customer = customerService.getCustomerByCustomerId(customerId);
        return ResponseEntity.ok(
                new StandardResponse(HttpStatus.OK.value(), "Success", customer)
        );
    }

    @GetMapping("/get-all-customer-name-email-ids")
    public ResponseEntity<StandardResponse> getAllCustomerContactNumberEmailIdsList() {

        List<CustomerContactNumberEmailIdsResponseDto> customerList =
                customerService.getAllCustomerContactNumberEmailIdsList();

        return ResponseEntity.ok(
                new StandardResponse(
                        HttpStatus.OK.value(),
                        "Customer contact details retrieved successfully",
                        customerList
                )
        );
    }
}
