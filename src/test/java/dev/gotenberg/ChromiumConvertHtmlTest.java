package dev.gotenberg;

import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.ResponseEntity;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

@Testcontainers
class ChromiumConvertHtmlTest extends GotenbergContainerTest {

    @Test
    void shouldConvertHtmlToPdf() {
        // Arrange
        String html = """
            <!DOCTYPE html>
            <html>
            <head><title>Test</title></head>
            <body><h1>Hello Gotenberg</h1></body>
            </html>
            """;

        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertHtml(html.getBytes(StandardCharsets.UTF_8), chromiumOptions);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
        assertDoesNotThrow(() -> {
            byte[] bytes = response.getBody().readAllBytes();
            assertTrue(bytes.length > 0);
            assertTrue(bytes[0] == '%' && bytes[1] == 'P' && bytes[2] == 'D' && bytes[3] == 'F');
        });
    }

    @Test
    void shouldConvertHtmlWithPrintBackground() {
        // Arrange
        String html = """
            <!DOCTYPE html>
            <html>
            <head>
                <style>
                    body { background-color: #e0e0e0; }
                </style>
            </head>
            <body><h1>Styled Content</h1></body>
            </html>
            """;
        var chromiumOptions = GotenbergClient.chromiumConvertOptions();

        // Act
        ResponseEntity<InputStream> response = gotenbergClient.convertHtml(html.getBytes(StandardCharsets.UTF_8), chromiumOptions);

        // Assert
        assertEquals(200, response.getStatusCode().value());
        assertNotNull(response.getBody());
    }
}
