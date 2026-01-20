package dev.gotenberg;

import org.springframework.boot.context.properties.ConfigurationProperties;

/// @param baseUrl The base URL for the Gotenberg service. For example: `http://localhost:3000`
@ConfigurationProperties(prefix = "gotenberg")
record GotenbergProperties(String baseUrl) {
}
