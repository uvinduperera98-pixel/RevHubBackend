package com.revHub.service;

import com.revHub.entity.JobCard;
import com.revHub.entity.LaborActivity;
import com.revHub.entity.Technician;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.awt.Color;
import java.util.stream.Collectors;

@Service
public class PdfGenerationService {

    // RevHub Brand Colors matching target image layout profile
    private static final Color COLOR_PRIMARY_DARK = new Color(18, 18, 18);   // Top Dark Banner / Section Headers
    private static final Color COLOR_ACCENT_RED   = new Color(211, 47, 47);  // RevHub Detailing Red
    private static final Color COLOR_TEXT_MUTED   = new Color(110, 110, 115); // Gray for secondary text labels
    private static final Color COLOR_LIGHT_BG     = new Color(248, 249, 250); // Alternating light rows / text backgrounds

    public byte[] generateJobCardPdf(JobCard jobCard) throws DocumentException {
        // Create an A4 Document with precise 0.5-inch (36pt) margins for clean multi-column balance
        Document document = new Document(PageSize.A4, 36, 36, 36, 36);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        PdfWriter.getInstance(document, out);

        document.open();

        // ==========================================
        // 1. BRAND HEADER BLOCK (RevHub Details & Job Metrics)
        // ==========================================
        PdfPTable headerTable = new PdfPTable(2);
        headerTable.setWidthPercentage(100);
        headerTable.setWidths(new float[]{60, 40});

        // Left Side: Corporate Logo and Address Structure
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.addElement(new Paragraph("RevHub", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 28, Font.BOLD, COLOR_ACCENT_RED)));

        Paragraph companyInfo = new Paragraph("AUTO SOLUTIONS & DETAILING\n123, Pannipitiya Road, Battaramulla\nContact: 077 123 4567 | info@revhub.lk",
                FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, COLOR_PRIMARY_DARK));
        companyInfo.setLeading(12);
        logoCell.addElement(companyInfo);
        headerTable.addCell(logoCell);

        // Right Side: Job Invoice Title and Matrix Meta Info Blocks
        PdfPCell metaCell = new PdfPCell();
        metaCell.setBorder(Rectangle.NO_BORDER);
        metaCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph docTitle = new Paragraph("JOB CARD", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 24, Font.BOLD, COLOR_PRIMARY_DARK));
        docTitle.setAlignment(Element.ALIGN_RIGHT);
        docTitle.setSpacingAfter(8);
        metaCell.addElement(docTitle);

        // Nested Data Summary Row Box
        PdfPTable idBoxTable = new PdfPTable(2);
        idBoxTable.setWidthPercentage(90);
        idBoxTable.setHorizontalAlignment(Element.ALIGN_RIGHT);
        idBoxTable.setWidths(new float[]{40, 60});

        addMetaBoxRow(idBoxTable, "JOB NO:",  jobCard.getJobCardNumber());
        metaCell.addElement(idBoxTable);

        headerTable.addCell(metaCell);
        document.add(headerTable);
        document.add(new Paragraph("\n")); // Space spacing line element

        // ==========================================
        // 2. INFORMATION PROFILE GRID (Customer vs Vehicle side-by-side)
        // ==========================================
        PdfPTable profileGrid = new PdfPTable(2);
        profileGrid.setWidthPercentage(100);
        profileGrid.setSpacingAfter(15);
        profileGrid.setWidths(new float[]{50, 50});

        // Left Segment Card: Customer Info Block
        PdfPCell clientSubCell = new PdfPCell();
        clientSubCell.setPadding(8);
        clientSubCell.setBorderColor(Color.LIGHT_GRAY);

        Paragraph clientHeader = new Paragraph("CUSTOMER DETAILS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, Color.WHITE));
        PdfPCell chCell = new PdfPCell(clientHeader);
        chCell.setBackgroundColor(COLOR_PRIMARY_DARK);
        chCell.setPadding(6);
        chCell.setBorder(Rectangle.NO_BORDER);

        PdfPTable clientTable = new PdfPTable(2);
        clientTable.setWidthPercentage(100);
        clientTable.setWidths(new float[]{35, 65});
        clientTable.setSpacingBefore(5);
        addInlineDetailField(clientTable, "Customer Name", jobCard.getVehicle().getCustomer().getCustomerName());
        addInlineDetailField(clientTable, "Phone Number", jobCard.getVehicle().getCustomer().getContactNumber());
        addInlineDetailField(clientTable, "Email Address", jobCard.getVehicle().getCustomer().getEmail());
        addInlineDetailField(clientTable, "Driving License", jobCard.getVehicle().getCustomer().getDrivingLicenseNumber());

        clientSubCell.addElement(chCell);
        clientSubCell.addElement(clientTable);
        profileGrid.addCell(clientSubCell);

        // Right Segment Card: Vehicle Data Specifications Block
        PdfPCell vehicleSubCell = new PdfPCell();
        vehicleSubCell.setPadding(8);
        vehicleSubCell.setBorderColor(Color.LIGHT_GRAY);

        Paragraph vHeader = new Paragraph("VEHICLE DETAILS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, Color.WHITE));
        PdfPCell vhCell = new PdfPCell(vHeader);
        vhCell.setBackgroundColor(COLOR_ACCENT_RED);
        vhCell.setPadding(6);
        vhCell.setBorder(Rectangle.NO_BORDER);

        PdfPTable vehicleTable = new PdfPTable(2);
        vehicleTable.setWidthPercentage(100);
        vehicleTable.setWidths(new float[]{35, 65});
        vehicleTable.setSpacingBefore(5);
        addInlineDetailField(vehicleTable, "Registration No", jobCard.getVehicle().getVehicleRegNo());
        addInlineDetailField(vehicleTable, "Make / Model", jobCard.getVehicle().getVehicleMake() + " " + jobCard.getVehicle().getVehicleModel());
        addInlineDetailField(vehicleTable, "Year / Color", jobCard.getVehicle().getVehicleYear() + " / " + jobCard.getVehicle().getColour());
        addInlineDetailField(vehicleTable, "Other Specs", jobCard.getVehicle().getOtherSpecs());

        vehicleSubCell.addElement(vhCell);
        vehicleSubCell.addElement(vehicleTable);
        profileGrid.addCell(vehicleSubCell);

        document.add(profileGrid);

        // ==========================================
        // 3. DIAGNOSTIC NOTES / COMPLAINT INNER CARD PANEL
        // ==========================================
        PdfPTable complaintTable = new PdfPTable(1);
        complaintTable.setWidthPercentage(100);
        complaintTable.setSpacingAfter(15);

        PdfPCell ccHeader = new PdfPCell(new Paragraph("CUSTOMER COMPLAINT", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 10, Font.BOLD, COLOR_ACCENT_RED)));
        ccHeader.setBorder(Rectangle.NO_BORDER);
        ccHeader.setPaddingBottom(4);
        complaintTable.addCell(ccHeader);

        PdfPCell ccBody = new PdfPCell(new Paragraph(jobCard.getCustomerComplaintText() != null ? jobCard.getCustomerComplaintText() : "No mechanical notes provided.", FontFactory.getFont(FontFactory.HELVETICA, 9.5f, Font.NORMAL)));
        ccBody.setPadding(10);
        ccBody.setBackgroundColor(COLOR_LIGHT_BG);
        ccBody.setBorderColor(COLOR_ACCENT_RED);
        ccBody.setBorderWidth(1f);
        complaintTable.addCell(ccBody);

        document.add(complaintTable);

        // ==========================================
        // 4. LABORS ACTIVITIES / WORK DONE ITEMS MATRIX
        // ==========================================
        PdfPTable laborTable = new PdfPTable(3);
        laborTable.setWidthPercentage(100);
        laborTable.setWidths(new float[]{10, 65, 25});
        laborTable.setSpacingAfter(20);

        // Build Table Matrix Headers Row
        String[] columnHeaders = {"NO.", "DESCRIPTION OF WORK / TASKS", "STATUS"};
        for (String colText : columnHeaders) {
            PdfPCell cell = new PdfPCell(new Paragraph(colText, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, Color.WHITE)));
            cell.setBackgroundColor(COLOR_PRIMARY_DARK);
            cell.setPadding(8);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            laborTable.addCell(cell);
        }

        // Loop over the assigned labor elements array payload
        int index = 1;
        if (jobCard.getLaborActivities() != null && !jobCard.getLaborActivities().isEmpty()) {
            for (LaborActivity activity : jobCard.getLaborActivities()) {
                laborTable.addCell(createMatrixBodyCell(String.valueOf(index++), Element.ALIGN_CENTER));
                laborTable.addCell(createMatrixBodyCell(activity.getActivityName(), Element.ALIGN_LEFT));
                laborTable.addCell(createMatrixStatusCell(jobCard.getStatus()));
            }
        } else {
            // Empty State Row Fallback Protection
            PdfPCell emptyCell = new PdfPCell(new Paragraph("No service tasks assigned to this job card record.", FontFactory.getFont(FontFactory.HELVETICA, 9, Font.ITALIC)));
            emptyCell.setColspan(3);
            emptyCell.setPadding(10);
            emptyCell.setHorizontalAlignment(Element.ALIGN_CENTER);
            laborTable.addCell(emptyCell);
        }
        document.add(laborTable);

        // ==========================================
        // 5. SIGN-OFF AUTHORIZATION RUNSHEET FOOTER
        // ==========================================
        PdfPTable footerGrid = new PdfPTable(2);
        footerGrid.setWidthPercentage(100);
        footerGrid.setWidths(new float[]{55, 45});

        PdfPCell techAssignmentCell = new PdfPCell();
        techAssignmentCell.setBorder(Rectangle.NO_BORDER);
        String mechanicsList = "Not Assigned";
        if (jobCard.getTechnicians() != null && !jobCard.getTechnicians().isEmpty()) {
            mechanicsList = jobCard.getTechnicians().stream()
                    .map(Technician::getTechnicianName)
                    .collect(Collectors.joining(", "));
        }
        techAssignmentCell.addElement(new Paragraph("Assigned Crew / Mechanics:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9.5f, Font.BOLD, COLOR_TEXT_MUTED)));
        techAssignmentCell.addElement(new Paragraph(mechanicsList, FontFactory.getFont(FontFactory.HELVETICA, 10, Font.BOLD, COLOR_PRIMARY_DARK)));
        footerGrid.addCell(techAssignmentCell);

        PdfPCell signatureBlockCell = new PdfPCell();
        signatureBlockCell.setBorder(Rectangle.NO_BORDER);
        signatureBlockCell.setHorizontalAlignment(Element.ALIGN_RIGHT);

        Paragraph lineSeparator = new Paragraph("___________________________\nAuthorized Representative Signature", FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, COLOR_TEXT_MUTED));
        lineSeparator.setAlignment(Element.ALIGN_RIGHT);
        lineSeparator.setLeading(14);
        signatureBlockCell.addElement(lineSeparator);
        footerGrid.addCell(signatureBlockCell);

        document.add(footerGrid);
        document.close();

        return out.toByteArray();
    }

    // Helper: Meta identity card field grid rows
    private void addMetaBoxRow(PdfPTable table, String label, String val) {
        PdfPCell lCell = new PdfPCell(new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, Color.WHITE)));
        lCell.setBackgroundColor(COLOR_PRIMARY_DARK);
        lCell.setPadding(5);
        lCell.setBorderColor(Color.BLACK);

        PdfPCell vCell = new PdfPCell(new Paragraph(val, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, COLOR_PRIMARY_DARK)));
        vCell.setPadding(5);
        vCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        vCell.setBorderColor(Color.LIGHT_GRAY);

        table.addCell(lCell);
        table.addCell(vCell);
    }

    // Helper: Inline layout profile text mapping
    private void addInlineDetailField(PdfPTable table, String label, String value) {
        PdfPCell lCell = new PdfPCell(new Paragraph(label, FontFactory.getFont(FontFactory.HELVETICA, 9, Font.NORMAL, COLOR_TEXT_MUTED)));
        lCell.setBorder(Rectangle.NO_BORDER);
        lCell.setPadding(4);

        PdfPCell vCell = new PdfPCell(new Paragraph(value != null ? value : "-", FontFactory.getFont(FontFactory.HELVETICA, 9, Font.BOLD, COLOR_PRIMARY_DARK)));
        vCell.setBorder(Rectangle.NO_BORDER);
        vCell.setPadding(4);

        table.addCell(lCell);
        table.addCell(vCell);
    }

    // Helper: Build crisp standardized content matrix layout boxes
    private PdfPCell createMatrixBodyCell(String text, int horizontalAlignment) {
        PdfPCell cell = new PdfPCell(new Paragraph(text, FontFactory.getFont(FontFactory.HELVETICA, 9)));
        cell.setHorizontalAlignment(horizontalAlignment);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }

    // Helper: Highlighting Status Matrix elements natively with thematic font weights
    private PdfPCell createMatrixStatusCell(String status) {
        PdfPCell cell = new PdfPCell(new Paragraph(status != null ? status : "PENDING", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 9, Font.BOLD, COLOR_ACCENT_RED)));
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        cell.setPadding(7);
        cell.setBorderColor(Color.LIGHT_GRAY);
        return cell;
    }
}