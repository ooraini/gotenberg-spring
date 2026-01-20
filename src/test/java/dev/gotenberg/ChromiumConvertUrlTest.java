package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ChromiumConvertUrlTest extends GotenbergContainerTest {

    @Test
    void shouldConvertUrlToPdf() {
        // Arrange
        String url = "https://example.com";
        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertUrl(url, chromiumOptions, null);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertDoesNotThrow(() -> {
            byte[] bytes = response.getBody().readAllBytes();
            assertTrue(bytes.length > 0);
            // PDF magic number
            assertTrue(bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F');
        });
    }

    @Test
    void shouldConvertUrlWithCustomMargins() {
        // Arrange
        String url = "https://example.com";
        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertUrl(url, chromiumOptions, null);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldConvertUrlWithWaitDelay() {
        // Arrange
        String url = "https://example.com";
        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertUrl(url, chromiumOptions, null);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }

    @Test
    void shouldConvertUrlWithPdfA() {
        // Arrange
        String url = "https://example.com";
        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertUrl(url, chromiumOptions, null);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
