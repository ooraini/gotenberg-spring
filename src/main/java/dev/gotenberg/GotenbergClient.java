package dev.gotenberg;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.io.InputStream;
import java.util.List;
import java.util.Objects;
import java.util.function.Function;

import static org.springframework.http.MediaType.*;

/// Spring HTTP Interface for Gotenberg API.
/// Reference: <a href="https://gotenberg.dev/docs/routes">Routes</a>
@SuppressWarnings("unused")
@HttpExchange(accept = APPLICATION_PDF_VALUE)
public interface GotenbergClient {

    static ChromiumScreenshotOptions chromiumScreenshotOptions() {
        return new ChromiumScreenshotOptions();
    }

    static ChromiumConvertOptions chromiumConvertOptions() {
        return new ChromiumConvertOptions();
    }

    //region High-Level Methods
    default ResponseEntity<InputStream> convertUrl(String url, ChromiumConvertOptions options, List<Resource> resources) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("url", url);
        return chromium(builder, options, resources, this::convertUrl);
    }

    // MultiValueMap<String, Object>
    private ResponseEntity<InputStream> chromium(MultiValueMap<String, Object> map,
                                                 Options options,
                                                 List<Resource> resources,
                                                 Function<MultiValueMap<String, Object>, ResponseEntity<InputStream>> api) {
        if (options != null) options.fill(map);
        if (resources != null) resources.forEach(r -> {
            map.add("files", r);
        });
        return api.apply(map);
    }

    private ResponseEntity<InputStream> chromium(MultipartBodyBuilder builder,
                                                 Options options,
                                                 List<Resource> resources,
                                                 Function<MultiValueMap<String, HttpEntity<?>>, ResponseEntity<InputStream>> api) {
        if (options != null) options.fill(builder);
        if (resources != null) resources.forEach(r -> {
            String filename = Objects.requireNonNull(r.getFilename(), "Resource filename cannot be null");
            builder.part("files", r).filename(filename);
        });
        return api.apply(builder.build());
    }

    default ResponseEntity<InputStream> convertHtml(Resource indexHtml, ChromiumConvertOptions options, List<Resource> resources) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (!Objects.equals(indexHtml.getFilename(), "index.html")) {
            throw new IllegalArgumentException("indexHtml resource filename must be 'index.html'");
        }
        body.add("files", indexHtml);
        return chromium(body, options, resources, this::convertHtml);
    }


    default ResponseEntity<InputStream> screenshotUrl(String url, ChromiumScreenshotOptions options) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("url", url);
        return chromium(builder, options, null, this::screenshotUrl);
    }

    default ResponseEntity<InputStream> screenshotHtml(Resource indexHtml, ChromiumScreenshotOptions options) {
        if (!Objects.equals(indexHtml.getFilename(), "index.html")) {
            throw new IllegalArgumentException("indexHtml resource filename must be 'index.html'");
        }
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", indexHtml).filename("index.html");
        return chromium(builder, options, null, this::screenshotHtml);
    }

    default ResponseEntity<InputStream> screenshotMarkdown(Resource markdownFile, ChromiumScreenshotOptions options) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", markdownFile).filename("index.md");
        return chromium(builder, options, null, this::screenshotMarkdown);
    }


    default ResponseEntity<InputStream> convertLibreOffice(List<Resource> files, LibreOfficeOptions options) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (files != null) files.forEach(f -> body.add("files", f));
        if (options != null) options.fill(body);
        return convertLibreOffice(body);
    }

    default ResponseEntity<InputStream> merge(List<Resource> pdfs) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (pdfs != null) pdfs.forEach(p -> body.add("files", p));
        return merge(body);
    }

    default ResponseEntity<InputStream> convertMarkdown(Resource indexHtml, List<Resource> markdownFiles, ChromiumConvertOptions options) {
        MultipartBodyBuilder builder = new MultipartBodyBuilder();
        builder.part("files", indexHtml).filename("index.html");
        if (markdownFiles != null) markdownFiles.forEach(m -> {
            String filename = Objects.requireNonNull(m.getFilename(), "Resource filename cannot be null");
            builder.part("files", m).filename(filename);
        });
        if (options != null) options.fill(builder);
        return convertMarkdown(builder.build());
    }

    default ResponseEntity<InputStream> convertPdf(List<Resource> pdfs, ChromiumConvertOptions options) {
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        if (pdfs != null) pdfs.forEach(p -> body.add("files", p));
        if (options != null) options.fill(body);
        return convertPdf(body);
    }


    //endregion

    //region Chromium
    @PostExchange(url = "/forms/chromium/convert/url", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertUrl(@RequestBody MultiValueMap<String, HttpEntity<?>> body);

    @PostExchange(url = "/forms/chromium/convert/html", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertHtml(@RequestBody MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/chromium/convert/markdown", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertMarkdown(@RequestBody MultiValueMap<String, HttpEntity<?>> body);

    @PostExchange(url = "/forms/chromium/screenshot/url", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotUrl(@RequestBody MultiValueMap<String, HttpEntity<?>> body);

    @PostExchange(url = "/forms/chromium/screenshot/html", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotHtml(@RequestBody MultiValueMap<String, HttpEntity<?>> body);

    @PostExchange(url = "/forms/chromium/screenshot/html", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotMarkdown(@RequestBody MultiValueMap<String, HttpEntity<?>> body);
    //endregion

    //region LibreOffice
    @PostExchange(url = "/forms/libreoffice/convert", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertLibreOffice(@RequestPart MultiValueMap<String, Object> body);
    //endregion


    //region PDF Engines
    @PostExchange(url = "/forms/pdfengines/merge", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> merge(@RequestPart MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/pdfengines/convert", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertPdf(@RequestPart MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/pdfengines/metadata", contentType = MULTIPART_FORM_DATA_VALUE, accept = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getMetadata(@RequestPart MultiValueMap<String, Object> body);
    //endregion


    //region Models
    enum PdfAFormat {
        A1B("PDF/A-1b"),
        A2B("PDF/A-2b"),
        A3B("PDF/A-3b");

        private final String value;

        PdfAFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum ScreenshotFormat {
        PNG("png"), JPEG("jpeg"), WEBP("webp");

        private final String value;

        ScreenshotFormat(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return value;
        }
    }

    enum EmulatedMediaType {
        SCREEN("screen"), PRINT("print"),
        ;


        private final String value;

        EmulatedMediaType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    interface Options {
        void fill(MultipartBodyBuilder builder);

        void fill(MultiValueMap<String, Object> map);
    }

    record Encryption(
            String userPassword, String ownerPassword
    ) {
        public void fill(MultiValueMap<String, Object> map) {
            if (userPassword != null) map.add("userPassword", userPassword);
            if (ownerPassword != null) map.add("ownerPassword", ownerPassword);
        }

        public Encryption userPassword(String userPassword) {
            return new Encryption(userPassword, this.ownerPassword);
        }

        public Encryption ownerPassword(String ownerPassword) {
            return new Encryption(this.userPassword, ownerPassword);
        }
    }

    record Split(String mode, String span, Boolean unify) {
        public void fill(MultiValueMap<String, Object> map) {
            if (mode != null) map.add("splitMode", mode);
            if (span != null) map.add("splitSpan", span);
            if (unify != null) map.add("splitUnify", unify);
        }

        public Split mode(String mode) {
            return new Split(mode, this.span, this.unify);
        }

        public Split span(String span) {
            return new Split(this.mode, span, this.unify);
        }

        public Split unify(Boolean unify) {
            return new Split(this.mode, this.span, unify);
        }
    }

    class ChromiumScreenshotOptions implements Options {
        private final java.util.Map<String, Object> options = new java.util.HashMap<>();

        public void fill(MultiValueMap<String, Object> map) {
            options.forEach(map::add);
        }

        public void fill(MultipartBodyBuilder builder) {
            options.forEach(builder::part);
        }

        /**
         * The device width of the screenshot (in pixels).
         */
        public ChromiumScreenshotOptions width(Integer width) {
            options.put("width", width);
            return this;
        }

        /**
         * The device height of the screenshot (in pixels).
         */
        public ChromiumScreenshotOptions height(Integer height) {
            options.put("height", height);
            return this;
        }

        /**
         * Define if the screenshot should be clipped to the viewport.
         */
        public ChromiumScreenshotOptions clip(Boolean clip) {
            options.put("clip", clip);
            return this;
        }

        /**
         * The image format of the screenshot.
         */
        public ChromiumScreenshotOptions format(ScreenshotFormat format) {
            options.put("format", format.getValue());
            return this;
        }

        /**
         * The compression quality of the screenshot, from 0 to 100.
         * Only available for jpeg and webp.
         */
        public ChromiumScreenshotOptions quality(Integer quality) {
            options.put("quality", quality);
            return this;
        }

        /**
         * Hide the default white background and allow capturing screenshots with transparency.
         */
        public ChromiumScreenshotOptions omitBackground(Boolean omitBackground) {
            options.put("omitBackground", omitBackground);
            return this;
        }

        /**
         * Optimize image encoding for speed, not for resulting file size.
         */
        public ChromiumScreenshotOptions optimizeForSpeed(Boolean optimizeForSpeed) {
            options.put("optimizeForSpeed", optimizeForSpeed);
            return this;
        }

        /**
         * The duration (e.g., <pre>5s</pre>) to wait when loading the page.
         */
        public ChromiumScreenshotOptions waitDelay(String waitDelay) {
            options.put("waitDelay", waitDelay);
            return this;
        }

        /**
         * The JavaScript expression to wait before converting the page to PDF/screenshot.
         */
        public ChromiumScreenshotOptions waitForExpression(String waitForExpression) {
            options.put("waitForExpression", waitForExpression);
            return this;
        }

        /**
         * Overrides the default User-Agent header.
         */
        public ChromiumScreenshotOptions userAgent(String userAgent) {
            options.put("userAgent", userAgent);
            return this;
        }

        /**
         * HTTP headers to send with the request (JSON format).
         */
        public ChromiumScreenshotOptions extraHttpHeaders(String extraHttpHeaders) {
            options.put("extraHttpHeaders", extraHttpHeaders);
            return this;
        }

        /**
         * Return an error if a JavaScript exception happens during the page's loading.
         */
        public ChromiumScreenshotOptions failOnConsoleExceptions(Boolean failOnConsoleExceptions) {
            options.put("failOnConsoleExceptions", failOnConsoleExceptions);
            return this;
        }

        /**
         * Do not wait for the <pre>networkIdle</pre> event.
         */
        public ChromiumScreenshotOptions skipNetworkIdleEvent(Boolean skipNetworkIdleEvent) {
            options.put("skipNetworkIdleEvent", skipNetworkIdleEvent);
            return this;
        }

        /**
         * The media type to emulate.
         */
        public ChromiumScreenshotOptions emulatedMediaType(EmulatedMediaType emulatedMediaType) {
            options.put("emulatedMediaType", emulatedMediaType.getValue());
            return this;
        }

        /**
         * The cookies to add to the request (JSON format).
         */
        public ChromiumScreenshotOptions cookies(String cookies) {
            options.put("cookies", cookies);
            return this;
        }

        /**
         * Return an error if the main page's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumScreenshotOptions failOnHttpStatusCodes(String failOnHttpStatusCodes) {
            options.put("failOnHttpStatusCodes", failOnHttpStatusCodes);
            return this;
        }

        /**
         * Return an error if a resource's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumScreenshotOptions failOnResourceHttpStatusCodes(String failOnResourceHttpStatusCodes) {
            options.put("failOnResourceHttpStatusCodes", failOnResourceHttpStatusCodes);
            return this;
        }

        /**
         * Return an error if a resource fails to load.
         */
        public ChromiumScreenshotOptions failOnResourceLoadingFailed(Boolean failOnResourceLoadingFailed) {
            options.put("failOnResourceLoadingFailed", failOnResourceLoadingFailed);
            return this;
        }
    }

    class ChromiumConvertOptions implements Options {
        private final java.util.Map<String, Object> options = new java.util.HashMap<>();

        public void fill(MultiValueMap<String, Object> map) {
            options.forEach(map::add);
        }

        public void fill(MultipartBodyBuilder builder) {
            options.forEach(builder::part);
        }

        /**
         * Whether to print the entire content in one single page.
         */
        public ChromiumConvertOptions singlePage(Boolean singlePage) {
            options.put("singlePage", singlePage);
            return this;
        }

        /**
         * The paper width (in inches).
         */
        public ChromiumConvertOptions paperWidth(Double paperWidth) {
            options.put("paperWidth", paperWidth);
            return this;
        }

        /**
         * The paper height (in inches).
         */
        public ChromiumConvertOptions paperHeight(Double paperHeight) {
            options.put("paperHeight", paperHeight);
            return this;
        }

        /**
         * The top margin (in inches).
         */
        public ChromiumConvertOptions marginTop(Double marginTop) {
            options.put("marginTop", marginTop);
            return this;
        }

        /**
         * The bottom margin (in inches).
         */
        public ChromiumConvertOptions marginBottom(Double marginBottom) {
            options.put("marginBottom", marginBottom);
            return this;
        }

        /**
         * The left margin (in inches).
         */
        public ChromiumConvertOptions marginLeft(Double marginLeft) {
            options.put("marginLeft", marginLeft);
            return this;
        }

        /**
         * The right margin (in inches).
         */
        public ChromiumConvertOptions marginRight(Double marginRight) {
            options.put("marginRight", marginRight);
            return this;
        }

        /**
         * Define whether to prefer page size as defined by CSS.
         */
        public ChromiumConvertOptions preferCSSPageSize(Boolean preferCSSPageSize) {
            options.put("preferCSSPageSize", preferCSSPageSize);
            return this;
        }

        /**
         * Whether or not to generate a document outline from the HTML headers.
         */
        public ChromiumConvertOptions generateDocumentOutline(Boolean generateDocumentOutline) {
            options.put("generateDocumentOutline", generateDocumentOutline);
            return this;
        }

        /**
         * Whether or not to generate a tagged PDF.
         */
        public ChromiumConvertOptions generateTaggedPdf(Boolean generateTaggedPdf) {
            options.put("generateTaggedPdf", generateTaggedPdf);
            return this;
        }

        /**
         * Print the background graphics.
         */
        public ChromiumConvertOptions printBackground(Boolean printBackground) {
            options.put("printBackground", printBackground);
            return this;
        }

        /**
         * Hide the default white background and allow generating PDFs with transparency.
         */
        public ChromiumConvertOptions omitBackground(Boolean omitBackground) {
            options.put("omitBackground", omitBackground);
            return this;
        }

        /**
         * The paper orientation to landscape.
         */
        public ChromiumConvertOptions landscape(Boolean landscape) {
            options.put("landscape", landscape);
            return this;
        }

        /**
         * The scale of the page rendering (e.g., <pre>1.0</pre>).
         */
        public ChromiumConvertOptions scale(String scale) {
            options.put("scale", scale);
            return this;
        }

        /**
         * Page ranges to print, e.g., <pre>1-5, 8, 11-13</pre>. Empty means all pages.
         */
        public ChromiumConvertOptions nativePageRanges(String nativePageRanges) {
            options.put("nativePageRanges", nativePageRanges);
            return this;
        }

        /**
         * The duration (e.g., <pre>5s</pre>) to wait when loading the page.
         */
        public ChromiumConvertOptions waitDelay(String waitDelay) {
            options.put("waitDelay", waitDelay);
            return this;
        }

        /**
         * The JavaScript expression to wait before converting the page to PDF/screenshot.
         */
        public ChromiumConvertOptions waitForExpression(String waitForExpression) {
            options.put("waitForExpression", waitForExpression);
            return this;
        }

        /**
         * Overrides the default User-Agent header.
         */
        public ChromiumConvertOptions userAgent(String userAgent) {
            options.put("userAgent", userAgent);
            return this;
        }

        /**
         * HTTP headers to send with the request (JSON format).
         */
        public ChromiumConvertOptions extraHttpHeaders(String extraHttpHeaders) {
            options.put("extraHttpHeaders", extraHttpHeaders);
            return this;
        }

        /**
         * Return an error if a JavaScript exception happens during the page's loading.
         */
        public ChromiumConvertOptions failOnConsoleExceptions(Boolean failOnConsoleExceptions) {
            options.put("failOnConsoleExceptions", failOnConsoleExceptions);
            return this;
        }

        /**
         * Do not wait for the <pre>networkIdle</pre> event.
         */
        public ChromiumConvertOptions skipNetworkIdleEvent(Boolean skipNetworkIdleEvent) {
            options.put("skipNetworkIdleEvent", skipNetworkIdleEvent);
            return this;
        }

        /**
         * Convert the resulting PDF into the given PDF/A format.
         */
        public ChromiumConvertOptions pdfa(PdfAFormat pdfa) {
            options.put("pdfa", pdfa.getValue());
            return this;
        }

        /**
         * Enable PDF/UA (Universal Accessibility) compliance.
         */
        public ChromiumConvertOptions pdfua(Boolean pdfua) {
            options.put("pdfua", pdfua);
            return this;
        }

        /**
         * The metadata to write into the PDF (JSON format).
         */
        public ChromiumConvertOptions metadata(String metadata) {
            options.put("metadata", metadata);
            return this;
        }

        /**
         * Flatten the PDF.
         */
        public ChromiumConvertOptions flatten(Boolean flatten) {
            options.put("flatten", flatten);
            return this;
        }

        /**
         * The encryption settings to protect the resulting PDF.
         */
        public ChromiumConvertOptions encryption(Encryption encryption) {
            if (encryption != null) {
                if (encryption.userPassword != null) options.put("userPassword", encryption.userPassword);
                if (encryption.ownerPassword != null) options.put("ownerPassword", encryption.ownerPassword);
            }
            return this;
        }

        /**
         * The files to embed into the resulting PDF.
         */
        public ChromiumConvertOptions embedFiles(List<Resource> embedFiles) {
            if (embedFiles != null) {
                options.put("embeds", embedFiles);
            }
            return this;
        }

        /**
         * The split settings for the resulting PDF.
         */
        public ChromiumConvertOptions split(Split split) {
            if (split != null) {
                if (split.mode != null) options.put("splitMode", split.mode);
                if (split.span != null) options.put("splitSpan", split.span);
                if (split.unify != null) options.put("splitUnify", split.unify);
            }
            return this;
        }

        /**
         * The media type to emulate.
         */
        public ChromiumConvertOptions emulatedMediaType(EmulatedMediaType emulatedMediaType) {
            options.put("emulatedMediaType", emulatedMediaType.getValue());
            return this;
        }

        /**
         * The cookies to add to the request (JSON format).
         */
        public ChromiumConvertOptions cookies(String cookies) {
            options.put("cookies", cookies);
            return this;
        }

        /**
         * Return an error if the main page's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumConvertOptions failOnHttpStatusCodes(String failOnHttpStatusCodes) {
            options.put("failOnHttpStatusCodes", failOnHttpStatusCodes);
            return this;
        }

        /**
         * Return an error if a resource's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumConvertOptions failOnResourceHttpStatusCodes(String failOnResourceHttpStatusCodes) {
            options.put("failOnResourceHttpStatusCodes", failOnResourceHttpStatusCodes);
            return this;
        }

        /**
         * Return an error if a resource fails to load.
         */
        public ChromiumConvertOptions failOnResourceLoadingFailed(Boolean failOnResourceLoadingFailed) {
            options.put("failOnResourceLoadingFailed", failOnResourceLoadingFailed);
            return this;
        }
    }

    class LibreOfficeOptions {
        private final java.util.Map<String, Object> options = new java.util.HashMap<>();

        public static LibreOfficeOptions of() {
            return new LibreOfficeOptions();
        }

        public void fill(MultiValueMap<String, Object> map) {
            options.forEach(map::add);
        }

        /**
         * If more than one file is provided, merge them into one PDF.
         */
        public LibreOfficeOptions merge(Boolean merge) {
            options.put("merge", merge);
            return this;
        }

        /**
         * The paper orientation to landscape.
         */
        public LibreOfficeOptions landscape(Boolean landscape) {
            options.put("landscape", landscape);
            return this;
        }

        /**
         * Page ranges to print, e.g., <pre>1-5, 8, 11-13</pre>. Empty means all pages.
         */
        public LibreOfficeOptions pageRanges(String pageRanges) {
            options.put("pageRanges", pageRanges);
            return this;
        }

        /**
         * Convert the resulting PDF into the given PDF/A format.
         */
        public LibreOfficeOptions pdfa(PdfAFormat pdfa) {
            options.put("pdfa", pdfa.getValue());
            return this;
        }

        /**
         * Enable PDF/UA (Universal Accessibility) compliance.
         */
        public LibreOfficeOptions pdfua(Boolean pdfua) {
            options.put("pdfua", pdfua);
            return this;
        }

        /**
         * The metadata to write into the PDF (JSON format).
         */
        public LibreOfficeOptions metadata(String metadata) {
            options.put("metadata", metadata);
            return this;
        }

        /**
         * Flatten the PDF.
         */
        public LibreOfficeOptions flatten(Boolean flatten) {
            options.put("flatten", flatten);
            return this;
        }

        /**
         * The encryption settings to protect the resulting PDF.
         */
        public LibreOfficeOptions encryption(Encryption encryption) {
            if (encryption != null) {
                if (encryption.userPassword != null) options.put("userPassword", encryption.userPassword);
                if (encryption.ownerPassword != null) options.put("ownerPassword", encryption.ownerPassword);
            }
            return this;
        }

        public LibreOfficeOptions embeds(List<Resource> embedFiles) {
            if (embedFiles != null) {
                options.put("embeds", embedFiles);
            }
            return this;
        }

        public LibreOfficeOptions split(Split split) {
            if (split != null) {
                if (split.mode != null) options.put("splitMode", split.mode);
                if (split.span != null) options.put("splitSpan", split.span);
                if (split.unify != null) options.put("splitUnify", split.unify);
            }
            return this;
        }
    }
}
