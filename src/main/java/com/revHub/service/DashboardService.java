package com.revHub.service;

import com.revHub.dto.response.*;
import com.revHub.entity.enums.RevenueFilter;

import java.util.List;
import java.util.Map;

public interface DashboardService {
    long getAllCustomersCount();

    Map<String, JobCardStatusResponseProjection> getAllJobCardStatus();

    List<JobCardSummaryResponseProjection> getRecentJobCards();

    List<InvoiceDashboardResponseProjection> getRecentInvoices();

    List<TopLaborActivityResponseProjection> getTopLaborActivities();

    long getInvoicesCount();

    long getJobCardsCount();

    double getRevenue();

    ChartDataResponseDTO getRevenueByFilter(RevenueFilter filter);
}
