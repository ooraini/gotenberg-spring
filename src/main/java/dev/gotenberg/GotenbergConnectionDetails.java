package dev.gotenberg;

import org.springframework.boot.autoconfigure.service.connection.ConnectionDetails;

public interface GotenbergConnectionDetails extends ConnectionDetails {
    String baseUrl();
}
