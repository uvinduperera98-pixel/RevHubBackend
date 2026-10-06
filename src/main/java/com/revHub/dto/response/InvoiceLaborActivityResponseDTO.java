package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceLaborActivityResponseDTO {
    private Long laborActivityId;
    private String activityName;
    private Boolean isAutoFetched;
    private Double laborFee;
    private List<InvoiceItemResponseDTO> parts;
}
