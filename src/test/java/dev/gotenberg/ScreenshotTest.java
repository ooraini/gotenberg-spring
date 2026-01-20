package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ScreenshotTest extends GotenbergContainerTest {

    @Test
    void shouldTakeScreenshotAsPng() {
        // Arrange
        String url = "https://example.com";

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.screenshotUrl(url,
            GotenbergClient.chromiumScreenshotOptions());

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertDoesNotThrow(() -> {
            byte[] bytes = response.getBody().readAllBytes();
            assertTrue(bytes.length > 0);
        });
    }

    @Test
    void shouldTakeScreenshotWithOmitBackground() {
        // Arrange
        String url = "https://example.com";
        var chromiumOptions = GotenbergClient.chromiumScreenshotOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.screenshotUrl(url, chromiumOptions);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
