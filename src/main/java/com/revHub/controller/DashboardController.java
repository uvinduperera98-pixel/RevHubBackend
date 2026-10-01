package com.revHub.controller;

import com.revHub.dto.response.*;
import com.revHub.entity.enums.RevenueFilter;
import com.revHub.service.DashboardService;
import com.revHub.util.StandardResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin
@RestController
@RequestMapping("/dashboard")
public class DashboardController {
    @Autowired
    DashboardService dashboardService;

    @GetMapping("/get-all-customers-count/")
    public ResponseEntity<StandardResponse> getAllCustomersCount() {
        long count = dashboardService.getAllCustomersCount();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                count));
    }

    @GetMapping("/get-all-job-card-status/")
    public ResponseEntity<StandardResponse> getAllJobCardStatus() {
        Map<String, JobCardStatusResponseProjection> statusCounts = dashboardService.getAllJobCardStatus();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                statusCounts
        ));
    }

    @GetMapping("/get-recent-job-cards/")
    public ResponseEntity<StandardResponse> getRecentJobCards() {
        List<JobCardSummaryResponseProjection> recentJobCards = dashboardService.getRecentJobCards();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                recentJobCards
        ));
    }

    @GetMapping("/get-recent-invoices/")
    public ResponseEntity<StandardResponse> getRecentInvoices() {
        List<InvoiceDashboardResponseProjection> recentInvoices = dashboardService.getRecentInvoices();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                recentInvoices
        ));
    }

    @GetMapping("/get-top-labor-activities/")
    public ResponseEntity<StandardResponse> getTopLaborActivities() {
        List<TopLaborActivityResponseProjection> topLaborActivities = dashboardService.getTopLaborActivities();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                topLaborActivities
        ));
    }

    @GetMapping("/get-invoices-count/")
    public ResponseEntity<StandardResponse> getInvoicesCount() {
        long count = dashboardService.getInvoicesCount();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                count
        ));
    }

    @GetMapping("/get-job-cards-count/")
    public ResponseEntity<StandardResponse> getJobCardsCount() {
        long count = dashboardService.getJobCardsCount();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                count
        ));
    }

    @GetMapping("/get-revenue/")
    public ResponseEntity<StandardResponse> getRevenue() {
        double revenue = dashboardService.getRevenue();

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Success",
                revenue
        ));
    }

    @GetMapping("/revenue/chart")
    public ResponseEntity<StandardResponse> getRevenueChartData(@RequestParam RevenueFilter filter) {
        ChartDataResponseDTO chartData = dashboardService.getRevenueByFilter(filter);

        return ResponseEntity.ok(new StandardResponse(
                HttpStatus.OK.value(),
                "Revenue chart data retrieved successfully for filter: " + filter.name().toLowerCase(),
                chartData
        ));
    }
}
