package com.revHub.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PdfPreviewResponseDTO {
    private long id;
    private byte[] pdfBytes;
}
