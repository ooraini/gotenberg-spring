package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class LibreOfficeConvertTest extends GotenbergContainerTest {

    Resource samplePdf = new ClassPathResource("sample.pdf");

    @Test
    void shouldMergeMultiplePdfs() throws IOException {
        // Arrange
        var pdfBytes = samplePdf.getContentAsByteArray();

        // Act
        GotenbergClient.PdfMergeOptions options = GotenbergClient.pdfMergeOptions()
                .addFile("a.pdf", pdfBytes)
                .addFile("b.pdf", pdfBytes);

        ResponseEntity<InputStream> response = gotenbergClient.pdfMerge(options);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
