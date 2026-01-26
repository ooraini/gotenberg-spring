# Gotenberg Spring

[![Maven Central](https://img.shields.io/maven-central/v/io.github.ooraini/gotenberg-spring)](https://central.sonatype.com/artifact/io.github.ooraini/gotenberg-spring)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)

A lightweight Java client for [Gotenberg](https://gotenberg.dev/) built using Spring HTTP interfaces. It provides a typesafe way to interact with Gotenberg's API for converting URLs, HTML, Markdown, and Office documents to PDF or images.

## Introduction

`gotenberg-spring` simplifies the integration of Gotenberg into Spring Boot 4.0+ applications. By utilizing Spring's declarative HTTP interfaces, it offers a clean API that integrates seamlessly with your existing Spring environment.

## Installation

### Maven
Add the following dependency to your `pom.xml`:

```xml
<dependency>
    <groupId>io.github.ooraini</groupId>    
    <artifactId>gotenberg-spring</artifactId>
    <version>0.3</version>
</dependency>
```

### Gradle
Add the dependency to your `build.gradle.kts`:

```kotlin
implementation("io.github.ooraini:gotenberg-spring:0.3")
```

## Usage

### 1. Register the Client

#### Autoconfiguration
If you are using Spring Boot's autoconfiguration, simply provide the base URL in your `application.properties` or `application.yml`:

```properties
gotenberg.client.base-url=http://localhost:3000
```

This will automatically register a `GotenbergClient` bean in your application context.

#### Manual Registration
If you prefer to configure the client manually (e.g., to add custom interceptors or use a specific `RestClient`), you can create the bean as follows:

```java
import dev.gotenberg.GotenbergClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;
import org.springframework.web.client.support.RestClientAdapter;

@Configuration
public class GotenbergConfig {
    @Bean
    public GotenbergClient gotenbergClient() {
        RestClient restClient = RestClient.builder()
            .baseUrl("http://localhost:3000")
            .build();
            
        return HttpServiceProxyFactory
            .builderFor(RestClientAdapter.create(restClient))
            .build()
            .createClient(GotenbergClient.class);
    }
}
```

### 2. Basic Conversions

#### Convert URL to PDF
```java
ResponseEntity<InputStream> response = client.convertUrl(
    "https://example.com",
    GotenbergClient.chromiumConvertOptions()
        .landscape(true)
        .paperWidth(8.5)
        .paperHeight(11.0)
);
```

#### Convert HTML to PDF
The main file must be named `index.html`.
```java
Resource indexHtml = new ClassPathResource("templates/index.html");
ResponseEntity<InputStream> response = client.convertHtml(indexHtml.getContentAsByteArray(), null);
```

#### Take a Screenshot
```java
ResponseEntity<InputStream> image = client.screenshotUrl(
    "https://github.com",
    GotenbergClient.chromiumScreenshotOptions()
        .format(GotenbergClient.ScreenshotFormat.PNG)
        .width(1920)
        .height(1080)
);
```

### 3. Advanced Features

#### Merge Multiple PDFs
```java
GotenbergClient.PdfMergeOptions options = GotenbergClient.pdfMergeOptions()
    .file(new FileSystemResource("report_part1.pdf"))
    .file(new FileSystemResource("report_part2.pdf"));
ResponseEntity<InputStream> merged = client.pdfMerge(options);
```

#### Office to PDF Conversion
Requires Gotenberg with LibreOffice enabled.
```java
ResponseEntity<InputStream> pdf = client.convertLibreOffice(
    GotenbergClient.libreOfficeOptions()
        .file(new FileSystemResource("resume.docx"))
        .pdfa(PdfAFormat.A1B)
        .exportBookmarks(true)
        .exportNotes(false)
);
```


## Spring Docker Compose Support

If you are using `spring-boot-docker-compose` a `ConnectionDetails` object will be automaticlly regisetered and used by the AutoConfiguration:

```yaml
services:
  gotenberg:
    image: gotenberg/gotenberg:latest
    ports:
      - "3000"
```

## Releasing
- `./gradlew release -PreleaseType=minor`
- `git checkout <latest>`
- `./gradlew publishToMavenCentral`
- Publish release in https://central.sonatype.com
- Create release in Github

## License

This project is licensed under the [Apache License 2.0](LICENSE).
