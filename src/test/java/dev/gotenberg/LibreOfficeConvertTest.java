package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class LibreOfficeConvertTest extends GotenbergContainerTest {

//    @Test
//    void shouldMergeMultiplePdfs() {
//        // Arrange
//        var pdfBytes = new byte[] { 0x25, 0x50, 0x44, 0x46 }; // PDF magic number
//        var pdfResource = new ByteArrayResource(pdfBytes) {
//            @Override
//            public String getFilename() {
//                return "test.pdf";
//            }
//        };
//
//        // Act
//        ResponseEntity<InputStream> response = gotenbergClient.merge(List.of(pdfResource, pdfResource));
//
//        // Assert
//        assertEquals(200, response.getStatusCode().value());
//        assertNotNull(response.getBody());
//    }
}
