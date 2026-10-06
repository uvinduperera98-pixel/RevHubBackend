package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChartDataResponseDTO {
    private List<String> dates;
    private List<Double> prices;
    private Double totalRevenue;
}