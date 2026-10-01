package com.revHub.service.impl;

import com.revHub.dto.response.*;
import com.revHub.entity.enums.RevenueFilter;
import com.revHub.repository.CustomerRepository;
import com.revHub.repository.InvoiceDetailRepository;
import com.revHub.repository.InvoiceRepository;
import com.revHub.repository.JobCardRepository;
import com.revHub.service.DashboardService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Limit;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class DashboardServiceImpl implements DashboardService {
    private final JobCardRepository jobCardRepository;
    private final CustomerRepository customerRepository;
    private final InvoiceDetailRepository invoiceDetailRepository;
    private final InvoiceRepository invoiceRepository;

    public DashboardServiceImpl(JobCardRepository jobCardRepository, CustomerRepository customerRepository, InvoiceDetailRepository invoiceDetailRepository, InvoiceRepository invoiceRepository) {
        this.jobCardRepository = jobCardRepository;
        this.customerRepository = customerRepository;
        this.invoiceDetailRepository = invoiceDetailRepository;
        this.invoiceRepository = invoiceRepository;
    }

    @Override
    public long getAllCustomersCount() {
        return customerRepository.countActiveCustomers();
    }

    @Override
    public Map<String, JobCardStatusResponseProjection> getAllJobCardStatus() {
        return jobCardRepository.getJobCardStatusCounts();
    }

    @Override
    public List<JobCardSummaryResponseProjection> getRecentJobCards() {
        return jobCardRepository.findRecentJobCards(PageRequest.of(0, 5));
    }

    @Override
    public List<InvoiceDashboardResponseProjection> getRecentInvoices() {
        return invoiceRepository.findRecentInvoices(PageRequest.of(0, 5));
    }

    @Override
    public List<TopLaborActivityResponseProjection> getTopLaborActivities() {
        return invoiceDetailRepository.findTopLaborActivities(Limit.of(5));
    }

    @Override
    public long getInvoicesCount() {
        return invoiceRepository.countTodayInvoices();
    }

    @Override
    public long getJobCardsCount() {
        return jobCardRepository.countTodayJobCards();
    }

    @Override
    public double getRevenue() {
        return invoiceRepository.getTodayRevenue();
    }

    @Override
    public ChartDataResponseDTO getRevenueByFilter(RevenueFilter filter) {
            List<String> labels = new ArrayList<>();
            List<Double> prices = new ArrayList<>();

            switch (filter) {
                case WEEK -> {
                    List<RevenueResponseProjection> results =
                            invoiceRepository.getWeeklyRevenue(LocalDateTime.now().minusDays(7));

                    for (RevenueResponseProjection row : results) {
                        labels.add(row.getLabel());
                        prices.add(row.getTotalRevenue());
                    }
                }

                case MONTH -> {
                    List<RevenueResponseProjection> results = invoiceRepository.getMonthlyRevenue();

                    for (RevenueResponseProjection row : results) {
                        labels.add(row.getLabel());
                        prices.add(row.getTotalRevenue());
                    }
                }

                case YEAR -> {
                    List<YearlyRevenueResponseProjection> results = invoiceRepository.getYearlyRevenue();

                    String[] monthNames = {
                            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
                            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
                    };

                    for (YearlyRevenueResponseProjection row : results) {
                        if (row.getMonthKey() != null) {
                            int monthIndex = row.getMonthKey() - 1;

                            if (monthIndex >= 0 && monthIndex < monthNames.length) {
                                labels.add(monthNames[monthIndex]);
                                prices.add(row.getTotalRevenue());
                            }
                        }
                    }
                }
            }

            double totalRevenue = prices.stream()
                    .mapToDouble(Double::doubleValue)
                    .sum();

            return new ChartDataResponseDTO(labels, prices, totalRevenue);
    }
}
