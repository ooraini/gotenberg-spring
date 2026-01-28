package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class PdfMetadataTest extends GotenbergContainerTest {

    @Test
    void shouldReadMetadata() {
        // Arrange
        var resource = new ClassPathResource("sample.pdf");
        var options = GotenbergClient.readMetadataOptions()
                .file(resource);

        // Act
        ResponseEntity<Map<String, Map<String, Object>>> response = gotenbergClient.readMetadata(options);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        Map<String, Map<String, Object>> body = response.getBody();
        assertNotNull(body);
        assertFalse(body.isEmpty());
        Map<String, Object> metadata = body.values().iterator().next();
        assertNotNull(metadata.get("PDFVersion"));
    }

    @Test
    void shouldWriteMetadata() {
        // Arrange
        String updatedTitle = "Gotenberg Metadata Test";
        ClassPathResource resource = new ClassPathResource("sample.pdf");

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.writeMetadata(
                GotenbergClient.writeMetadataOptions()
                        .file(resource)
                        .metadata("Title", updatedTitle)
        );

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertDoesNotThrow(() -> {
            byte[] bytes = response.getBody().readAllBytes();
            assertTrue(bytes.length > 0);
            assertTrue(bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F');

            ResponseEntity<Map<String, Map<String, Object>>> verifyResponse = gotenbergClient.readMetadata(
                    GotenbergClient.readMetadataOptions().file("updated.pdf", bytes)
            );
            assertNotNull(verifyResponse.getBody());
            Map<String, Object> verifyMetadata = verifyResponse.getBody().values().iterator().next();
            assertEquals(updatedTitle, verifyMetadata.get("Title"));
        });
    }
}
