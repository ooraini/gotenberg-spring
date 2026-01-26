package dev.gotenberg;

import org.jspecify.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.service.annotation.HttpExchange;
import org.springframework.web.service.annotation.PostExchange;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.springframework.http.MediaType.*;

/// Spring HTTP Interface for Gotenberg API.
/// Reference: <a href="https://gotenberg.dev/docs/routes">Routes</a>
@SuppressWarnings("unused")
@HttpExchange(accept = APPLICATION_PDF_VALUE)
public interface GotenbergClient {
    //region Chromium Convert

    static ChromiumConvertOptions chromiumConvertOptions() {
        return new ChromiumConvertOptions(null);
    }

    default ResponseEntity<InputStream> convertHtml(String indexHtml, @Nullable ChromiumConvertOptions options) {
        return convertHtml(indexHtml.getBytes(StandardCharsets.UTF_8), options);
    }

    default ResponseEntity<InputStream> convertHtml(byte[] indexHtml, @Nullable ChromiumConvertOptions options) {
        return convertHtml(new ChromiumConvertOptions(options).file("index.html", indexHtml).parts);
    }

    default ResponseEntity<InputStream> convertUrl(String url, @Nullable ChromiumConvertOptions options) {
        return convertUrl(new ChromiumConvertOptions(options).add("url", url).parts);
    }

    default ResponseEntity<InputStream> convertMarkdown(ChromiumConvertOptions options) {
        return convertMarkdown(options.parts);
    }

    @PostExchange(url = "/forms/chromium/convert/html", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertHtml(@RequestBody MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/chromium/convert/url", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertUrl(@RequestBody MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/chromium/convert/markdown", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertMarkdown(@RequestBody MultiValueMap<String, Object> body);
    //endregion


    //region Chromium Screenshot

    static ChromiumScreenshotOptions chromiumScreenshotOptions() {
        return new ChromiumScreenshotOptions(null);
    }

    default ResponseEntity<InputStream> screenshotHtml(String indexHtml, @Nullable ChromiumScreenshotOptions options) {
        return screenshotHtml(indexHtml.getBytes(StandardCharsets.UTF_8), options);
    }

    default ResponseEntity<InputStream> screenshotHtml(byte[] indexHtml, @Nullable ChromiumScreenshotOptions options) {
        return screenshotHtml(new ChromiumScreenshotOptions(options).file("index.html", indexHtml).parts);
    }

    default ResponseEntity<InputStream> screenshotUrl(String url, @Nullable ChromiumScreenshotOptions options) {
        return screenshotUrl(new ChromiumScreenshotOptions(options).add("url", url).parts);
    }

    default ResponseEntity<InputStream> screenshotMarkdown(ChromiumScreenshotOptions options) {
        return screenshotMarkdown(options.parts);
    }

    @PostExchange(url = "/forms/chromium/screenshot/url", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotUrl(@RequestBody MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/chromium/screenshot/html", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotHtml(@RequestBody MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/chromium/screenshot/markdown", contentType = MULTIPART_FORM_DATA_VALUE, accept = {IMAGE_PNG_VALUE, IMAGE_JPEG_VALUE, "image/webp"})
    ResponseEntity<InputStream> screenshotMarkdown(@RequestBody MultiValueMap<String, Object> body);
    //endregion


    //region Libre Office Convert

    static LibreOfficeOptions libreOfficeOptions() {
        return new LibreOfficeOptions(null);
    }

    default ResponseEntity<InputStream> convertLibreOffice(LibreOfficeOptions options) {
        return convertLibreOffice(options.parts);
    }

    @PostExchange(url = "/forms/libreoffice/convert", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertLibreOffice(@RequestPart MultiValueMap<String, Object> body);
    //endregion


    //region PDF Merge
    static PdfMergeOptions pdfMergeOptions() {
        return new PdfMergeOptions(null);
    }

    default ResponseEntity<InputStream> pdfMerge(PdfMergeOptions options) {
        return pdfMerge(options.parts);
    }

    @PostExchange(url = "/forms/pdfengines/merge", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> pdfMerge(@RequestPart MultiValueMap<String, Object> body);
    //endregion

    @PostExchange(url = "/forms/pdfengines/convert", contentType = MULTIPART_FORM_DATA_VALUE)
    ResponseEntity<InputStream> convertPdf(@RequestPart MultiValueMap<String, Object> body);

    @PostExchange(url = "/forms/pdfengines/metadata", contentType = MULTIPART_FORM_DATA_VALUE, accept = APPLICATION_JSON_VALUE)
    ResponseEntity<String> getMetadata(@RequestPart MultiValueMap<String, Object> body);


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


    abstract class Options<O extends Options<O>> {
        final LinkedMultiValueMap<String, Object> parts = new LinkedMultiValueMap<>();
        final LinkedMultiValueMap<String, Object> headers = new LinkedMultiValueMap<>();

        public Options(@Nullable O copy) {
            if (copy != null) parts.putAll(copy.parts);
        }

        public O add(String key, Object value) {
            parts.add(key, value);
            //noinspection unchecked
            return (O) this;
        }

        public O file(String filename, String content) {
            return file(filename, content.getBytes(StandardCharsets.UTF_8));
        }

        public O file(String filename, byte[] content) {
            return add("files", new ByteArrayResource(content, filename));
        }

        public O file(String filename, InputStream content) {
            return add("files", new InputStreamResource(content, filename));
        }

        public O file(Resource resource) {
            return add("files", resource);
        }


        public O embed(String filename, String content) {
            return file(filename, content.getBytes(StandardCharsets.UTF_8));
        }

        public O embed(String filename, byte[] content) {
            return add("embeds", new ByteArrayResource(content, filename));
        }

        public O embed(String filename, InputStream content) {
            return add("embeds", new InputStreamResource(content, filename));
        }

        public O embed(Resource resource) {
            return add("embeds", resource);
        }
    }

    class ChromiumScreenshotOptions extends Options<ChromiumScreenshotOptions> {

        ChromiumScreenshotOptions(@Nullable ChromiumScreenshotOptions copy) {
            super(copy);
        }

        /**
         * The device width of the screenshot (in pixels).
         */
        public ChromiumScreenshotOptions width(Integer width) {
            return add("width", width);
        }

        /**
         * The device height of the screenshot (in pixels).
         */
        public ChromiumScreenshotOptions height(Integer height) {
            return add("height", height);
        }

        /**
         * Define if the screenshot should be clipped to the viewport.
         */
        public ChromiumScreenshotOptions clip(Boolean clip) {
            return add("clip", clip);
        }

        /**
         * The image format of the screenshot.
         */
        public ChromiumScreenshotOptions format(ScreenshotFormat format) {
            return add("format", format.getValue());
        }

        /**
         * The compression quality of the screenshot, from 0 to 100.
         * Only available for jpeg and webp.
         */
        public ChromiumScreenshotOptions quality(Integer quality) {
            return add("quality", quality);
        }

        /**
         * Hide the default white background and allow capturing screenshots with transparency.
         */
        public ChromiumScreenshotOptions omitBackground(Boolean omitBackground) {
            return add("omitBackground", omitBackground);
        }

        /**
         * Optimize image encoding for speed, not for resulting file size.
         */
        public ChromiumScreenshotOptions optimizeForSpeed(Boolean optimizeForSpeed) {
            return add("optimizeForSpeed", optimizeForSpeed);
        }

        /**
         * The duration (e.g., <pre>5s</pre>) to wait when loading the page.
         */
        public ChromiumScreenshotOptions waitDelay(String waitDelay) {
            return add("waitDelay", waitDelay);
        }

        /**
         * The JavaScript expression to wait before converting the page to PDF/screenshot.
         */
        public ChromiumScreenshotOptions waitForExpression(String waitForExpression) {
            return add("waitForExpression", waitForExpression);
        }

        /**
         * Overrides the default User-Agent header.
         */
        public ChromiumScreenshotOptions userAgent(String userAgent) {
            return add("userAgent", userAgent);
        }

        /**
         * HTTP headers to send with the request (JSON format).
         */
        public ChromiumScreenshotOptions extraHttpHeaders(String extraHttpHeaders) {
            return add("extraHttpHeaders", extraHttpHeaders);
        }

        /**
         * Return an error if a JavaScript exception happens during the page's loading.
         */
        public ChromiumScreenshotOptions failOnConsoleExceptions(Boolean failOnConsoleExceptions) {
            return add("failOnConsoleExceptions", failOnConsoleExceptions);
        }

        /**
         * Do not wait for the <pre>networkIdle</pre> event.
         */
        public ChromiumScreenshotOptions skipNetworkIdleEvent(Boolean skipNetworkIdleEvent) {
            return add("skipNetworkIdleEvent", skipNetworkIdleEvent);
        }

        /**
         * The media type to emulate.
         */
        public ChromiumScreenshotOptions emulatedMediaType(EmulatedMediaType emulatedMediaType) {
            return add("emulatedMediaType", emulatedMediaType.getValue());
        }

        /**
         * The cookies to add to the request (JSON format).
         */
        public ChromiumScreenshotOptions cookies(String cookies) {
            return add("cookies", cookies);
        }

        /**
         * Return an error if the main page's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumScreenshotOptions failOnHttpStatusCodes(String failOnHttpStatusCodes) {
            return add("failOnHttpStatusCodes", failOnHttpStatusCodes);
        }

        /**
         * Return an error if a resource's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumScreenshotOptions failOnResourceHttpStatusCodes(String failOnResourceHttpStatusCodes) {
            return add("failOnResourceHttpStatusCodes", failOnResourceHttpStatusCodes);
        }

        /**
         * Return an error if a resource fails to load.
         */
        public ChromiumScreenshotOptions failOnResourceLoadingFailed(Boolean failOnResourceLoadingFailed) {
            return add("failOnResourceLoadingFailed", failOnResourceLoadingFailed);
        }
    }

    /**
     * Defines options for `/forms/chromium/convert/` route.
     * This class provides a chainable API to configure various parameters such as page dimensions, margins,
     * rendering options, metadata, page ranges, and security settings.
     */
    class ChromiumConvertOptions extends Options<ChromiumConvertOptions> {
        public ChromiumConvertOptions(@Nullable ChromiumConvertOptions copy) {
            super(copy);
        }

        /**
         * Whether to print the entire content in one single page.
         */
        public ChromiumConvertOptions singlePage(Boolean singlePage) {
            return add("singlePage", singlePage);
        }

        /**
         * The paper width (in inches).
         */
        public ChromiumConvertOptions paperWidth(Double paperWidth) {
            return add("paperWidth", paperWidth);
        }

        /**
         * The paper height (in inches).
         */
        public ChromiumConvertOptions paperHeight(Double paperHeight) {
            return add("paperHeight", paperHeight);
        }

        /**
         * The top margin (in inches).
         */
        public ChromiumConvertOptions marginTop(Double marginTop) {
            return add("marginTop", marginTop);
        }

        /**
         * The bottom margin (in inches).
         */
        public ChromiumConvertOptions marginBottom(Double marginBottom) {
            return add("marginBottom", marginBottom);
        }

        /**
         * The left margin (in inches).
         */
        public ChromiumConvertOptions marginLeft(Double marginLeft) {
            return add("marginLeft", marginLeft);
        }

        /**
         * The right margin (in inches).
         */
        public ChromiumConvertOptions marginRight(Double marginRight) {
            return add("marginRight", marginRight);
        }

        /**
         * Define whether to prefer page size as defined by CSS.
         */
        public ChromiumConvertOptions preferCSSPageSize(Boolean preferCSSPageSize) {
            return add("preferCSSPageSize", preferCSSPageSize);
        }

        /**
         * Whether or not to generate a document outline from the HTML headers.
         */
        public ChromiumConvertOptions generateDocumentOutline(Boolean generateDocumentOutline) {
            return add("generateDocumentOutline", generateDocumentOutline);
        }

        /**
         * Whether or not to generate a tagged PDF.
         */
        public ChromiumConvertOptions generateTaggedPdf(Boolean generateTaggedPdf) {
            return add("generateTaggedPdf", generateTaggedPdf);
        }

        /**
         * Print the background graphics.
         */
        public ChromiumConvertOptions printBackground(Boolean printBackground) {
            return add("printBackground", printBackground);
        }

        /**
         * Hide the default white background and allow generating PDFs with transparency.
         */
        public ChromiumConvertOptions omitBackground(Boolean omitBackground) {
            return add("omitBackground", omitBackground);
        }

        /**
         * The paper orientation to landscape.
         */
        public ChromiumConvertOptions landscape(Boolean landscape) {
            return add("landscape", landscape);
        }

        /**
         * The scale of the page rendering (e.g., <pre>1.0</pre>).
         */
        public ChromiumConvertOptions scale(String scale) {
            return add("scale", scale);
        }

        /**
         * Page ranges to print, e.g., <pre>1-5, 8, 11-13</pre>. Empty means all pages.
         */
        public ChromiumConvertOptions nativePageRanges(String nativePageRanges) {
            return add("nativePageRanges", nativePageRanges);
        }

        /**
         * The duration (e.g., <pre>5s</pre>) to wait when loading the page.
         */
        public ChromiumConvertOptions waitDelay(String waitDelay) {
            return add("waitDelay", waitDelay);
        }

        /**
         * The JavaScript expression to wait before converting the page to PDF/screenshot.
         */
        public ChromiumConvertOptions waitForExpression(String waitForExpression) {
            return add("waitForExpression", waitForExpression);
        }

        /**
         * Overrides the default User-Agent header.
         */
        public ChromiumConvertOptions userAgent(String userAgent) {
            return add("userAgent", userAgent);
        }

        /**
         * HTTP headers to send with the request (JSON format).
         */
        public ChromiumConvertOptions extraHttpHeaders(String extraHttpHeaders) {
            return add("extraHttpHeaders", extraHttpHeaders);
        }

        /**
         * Return an error if a JavaScript exception happens during the page's loading.
         */
        public ChromiumConvertOptions failOnConsoleExceptions(Boolean failOnConsoleExceptions) {
            return add("failOnConsoleExceptions", failOnConsoleExceptions);
        }

        /**
         * Do not wait for the <pre>networkIdle</pre> event.
         */
        public ChromiumConvertOptions skipNetworkIdleEvent(Boolean skipNetworkIdleEvent) {
            return add("skipNetworkIdleEvent", skipNetworkIdleEvent);
        }

        /**
         * Convert the resulting PDF into the given PDF/A format.
         */
        public ChromiumConvertOptions pdfa(PdfAFormat pdfa) {
            return add("pdfa", pdfa.getValue());
        }

        /**
         * Enable PDF/UA (Universal Accessibility) compliance.
         */
        public ChromiumConvertOptions pdfua(Boolean pdfua) {
            return add("pdfua", pdfua);
        }

        /**
         * The metadata to write into the PDF (JSON format).
         */
        public ChromiumConvertOptions metadata(String metadata) {
            return add("metadata", metadata);
        }

        /**
         * Flatten the resulting PDF.
         */
        public ChromiumConvertOptions flatten(Boolean flatten) {
            return add("flatten", flatten);
        }

        /**
         * Password for opening the resulting PDF(s).
         */
        public ChromiumConvertOptions userPassword(String userPassword) {
            return add("userPassword", userPassword);
        }

        /**
         * Password for full access on the resulting PDF(s).
         */
        public ChromiumConvertOptions ownerPassword(String ownerPassword) {
            return add("ownerPassword", ownerPassword);
        }

        /**
         * The files to embed into the resulting PDF.
         */
        public ChromiumConvertOptions embedFiles(List<Resource> embedFiles) {
            if (embedFiles != null) {
                parts.addAll("embeds", embedFiles);
            }
            return this;
        }

        /**
         * The split mode for the resulting PDF.
         */
        public ChromiumConvertOptions splitMode(String mode) {
            return add("splitMode", mode);
        }

        /**
         * The split span for the resulting PDF.
         */
        public ChromiumConvertOptions splitSpan(String span) {
            return add("splitSpan", span);
        }

        /**
         * Whether to unify the resulting PDF after split.
         */
        public ChromiumConvertOptions splitUnify(Boolean unify) {
            return add("splitUnify", unify);
        }

        /**
         * The media type to emulate.
         */
        public ChromiumConvertOptions emulatedMediaType(EmulatedMediaType emulatedMediaType) {
            return add("emulatedMediaType", emulatedMediaType.getValue());
        }

        /**
         * The cookies to add to the request (JSON format).
         */
        public ChromiumConvertOptions cookies(String cookies) {
            return add("cookies", cookies);
        }

        /**
         * Return an error if the main page's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumConvertOptions failOnHttpStatusCodes(String failOnHttpStatusCodes) {
            return add("failOnHttpStatusCodes", failOnHttpStatusCodes);
        }

        /**
         * Return an error if a resource's HTTP status code is one of the codes in the list (JSON format).
         */
        public ChromiumConvertOptions failOnResourceHttpStatusCodes(String failOnResourceHttpStatusCodes) {
            return add("failOnResourceHttpStatusCodes", failOnResourceHttpStatusCodes);
        }

        /**
         * Return an error if a resource fails to load.
         */
        public ChromiumConvertOptions failOnResourceLoadingFailed(Boolean failOnResourceLoadingFailed) {
            return add("failOnResourceLoadingFailed", failOnResourceLoadingFailed);
        }
    }

    class LibreOfficeOptions extends Options<LibreOfficeOptions> {
        public LibreOfficeOptions(@Nullable LibreOfficeOptions copy) {
            super(copy);
        }

        /**
         * Set the password for opening the source file.
         */
        public LibreOfficeOptions password(String password) {
            return add("password", password);
        }

        /**
         * The paper orientation to landscape.
         */
        public LibreOfficeOptions landscape(Boolean landscape) {
            return add("landscape", landscape);
        }


        /**
         * Page ranges to print, e.g., '1-4' - empty means all pages.
         */
        public LibreOfficeOptions nativePageRanges(String nativePageRanges) {
            return add("nativePageRanges", nativePageRanges);
        }

        /**
         * Specify whether to update the indexes before conversion, keeping in mind that doing so might result in missing links in the final PDF.
         */
        public LibreOfficeOptions updateIndexes(Boolean updateIndexes) {
            return add("updateIndexes", updateIndexes);
        }


        /**
         * Specify whether form fields are exported as widgets or only their fixed print representation is exported.
         */
        public LibreOfficeOptions exportFormFields(Boolean exportFormFields) {
            return add("exportFormFields", exportFormFields);
        }

        /**
         * Specify whether multiple form fields exported are allowed to have the same field name.
         */
        public LibreOfficeOptions allowDuplicateFieldNames(Boolean allowDuplicateFieldNames) {
            return add("allowDuplicateFieldNames", allowDuplicateFieldNames);
        }

        /**
         * Specify if bookmarks are exported to PDF.
         */
        public LibreOfficeOptions exportBookmarks(Boolean exportBookmarks) {
            return add("exportBookmarks", exportBookmarks);
        }

        /**
         * Specify that the bookmarks contained in the source LibreOffice file should be exported to the PDF file as Named Destination.
         */
        public LibreOfficeOptions exportBookmarksToPdfDestination(Boolean exportBookmarksToPdfDestination) {
            return add("exportBookmarksToPdfDestination", exportBookmarksToPdfDestination);
        }

        /**
         * Export the placeholders fields visual markings only. The exported placeholder is ineffective.
         */
        public LibreOfficeOptions exportPlaceholders(Boolean exportPlaceholders) {
            return add("exportPlaceholders", exportPlaceholders);
        }

        /**
         * Specify if notes are exported to PDF.
         */
        public LibreOfficeOptions exportNotes(Boolean exportNotes) {
            return add("exportNotes", exportNotes);
        }

        /**
         * Specify if notes pages are exported to PDF. Notes pages are available in Impress documents only.
         */
        public LibreOfficeOptions exportNotesPages(Boolean exportNotesPages) {
            return add("exportNotesPages", exportNotesPages);
        }

        /**
         * Specify, if the form field exportNotesPages is set to true, if only notes pages are exported to PDF.
         */
        public LibreOfficeOptions exportOnlyNotesPages(Boolean exportOnlyNotesPages) {
            return add("exportOnlyNotesPages", exportOnlyNotesPages);
        }

        /**
         * Specify if notes in margin are exported to PDF.
         */
        public LibreOfficeOptions exportNotesInMargin(Boolean exportNotesInMargin) {
            return add("exportNotesInMargin", exportNotesInMargin);
        }

        /**
         * Specify that the target documents with .od[tpqs] extension, will have that extension changed to .pdf when the link is exported to PDF.
         * The source document remains untouched.
         */
        public LibreOfficeOptions convertOooTargetToPdfTarget(Boolean convertOooTargetToPdfTarget) {
            return add("convertOooTargetToPdfTarget", convertOooTargetToPdfTarget);
        }

        /**
         * Specify that the file system related hyperlinks (file:// protocol) present in the document will be exported as relative to the source document location.
         */
        public LibreOfficeOptions exportLinksRelativeFsys(Boolean exportLinksRelativeFsys) {
            return add("exportLinksRelativeFsys", exportLinksRelativeFsys);
        }

        /**
         * Export, for LibreOffice Impress, slides that are not included in slide shows.
         */
        public LibreOfficeOptions exportHiddenSlides(Boolean exportHiddenSlides) {
            return add("exportHiddenSlides", exportHiddenSlides);
        }

        /**
         * Specify that automatically inserted empty pages are suppressed. This option is active only if storing Writer documents.
         */
        public LibreOfficeOptions skipEmptyPages(Boolean skipEmptyPages) {
            return add("skipEmptyPages", skipEmptyPages);
        }

        /**
         * Specify that a stream is inserted to the PDF file which contains the original document for archiving purposes.
         */
        public LibreOfficeOptions addOriginalDocumentAsStream(Boolean addOriginalDocumentAsStream) {
            return add("addOriginalDocumentAsStream", addOriginalDocumentAsStream);
        }

        /**
         * Ignore each sheet's paper size, print ranges and shown/hidden status and puts every sheet (even hidden sheets) on exactly one page.
         */
        public LibreOfficeOptions singlePageSheets(Boolean singlePageSheets) {
            return add("singlePageSheets", singlePageSheets);
        }

        /**
         * Specify if images are exported to PDF using a lossless compression format like PNG or compressed using the JPEG format.
         */
        public LibreOfficeOptions losslessImageCompression(Boolean losslessImageCompression) {
            return add("losslessImageCompression", losslessImageCompression);
        }

        /**
         * Specify the quality of the JPG export. A higher value produces a higher-quality image and a larger file.
         * Between 1 and 100.
         */
        public LibreOfficeOptions quality(Integer quality) {
            return add("quality", quality);
        }

        /**
         * Specify if the resolution of each image is reduced to the resolution specified by the form field maxImageResolution.
         */
        public LibreOfficeOptions reduceImageResolution(Boolean reduceImageResolution) {
            return add("reduceImageResolution", reduceImageResolution);
        }

        /**
         * If the form field reduceImageResolution is set to true, tell if all images will be reduced to the given value in DPI.
         * Possible values are: 75, 150, 300, 600 and 1200.
         */
        public LibreOfficeOptions maxImageResolution(Integer maxImageResolution) {
            return add("maxImageResolution", maxImageResolution);
        }


        /**
         * If more than one file is provided, merge them into one PDF.
         */
        public LibreOfficeOptions merge(Boolean merge) {
            return add("merge", merge);
        }


        public LibreOfficeOptions splitMode(String mode) {
            return add("splitMode", mode);
        }

        public LibreOfficeOptions splitSpan(String span) {
            return add("splitSpan", span);
        }

        public LibreOfficeOptions splitUnify(Boolean unify) {
            return add("splitUnify", unify);
        }


        /**
         * Convert the resulting PDF into the given PDF/A format.
         */
        public LibreOfficeOptions pdfa(PdfAFormat pdfa) {
            return add("pdfa", pdfa.getValue());
        }

        /**
         * Enable PDF for Universal Access for optimal accessibility.
         */
        public LibreOfficeOptions pdfua(Boolean pdfua) {
            return add("pdfua", pdfua);
        }

        /**
         * The metadata to write into the PDF (JSON format).
         */
        public LibreOfficeOptions metadata(String metadata) {
            return add("metadata", metadata);
        }

        /**
         * Flatten the PDF.
         */
        public LibreOfficeOptions flatten(Boolean flatten) {
            return add("flatten", flatten);
        }

        /**
         * The user password to open the resulting PDF.
         */
        public LibreOfficeOptions userPassword(String userPassword) {
            return add("userPassword", userPassword);
        }

        /**
         * The owner password to protect the resulting PDF.
         */
        public LibreOfficeOptions ownerPassword(String ownerPassword) {
            return add("ownerPassword", ownerPassword);
        }

        public LibreOfficeOptions embeds(List<Resource> embedFiles) {
            if (embedFiles != null) {
                parts.add("embeds", embedFiles);
            }
            return this;
        }
    }

    class PdfMergeOptions extends Options<PdfMergeOptions> {
        PdfMergeOptions(@Nullable PdfMergeOptions copy) {
            super(copy);
        }

        /**
         * Convert the resulting PDF into the given PDF/A format.
         */
        public PdfMergeOptions pdfa(PdfAFormat pdfa) {
            return add("pdfa", pdfa.getValue());
        }

        /**
         * Enable PDF for Universal Access for optimal accessibility.
         */
        public PdfMergeOptions pdfua(Boolean pdfua) {
            return add("pdfua", pdfua);
        }

        /**
         * The metadata to write into the PDF (JSON format).
         */
        public PdfMergeOptions metadata(String metadata) {
            return add("metadata", metadata);
        }

        /**
         * Flatten the resulting PDF.
         */
        public PdfMergeOptions flatten(Boolean flatten) {
            return add("flatten", flatten);
        }

        /**
         * Password for opening the resulting PDF(s).
         */
        public PdfMergeOptions userPassword(String userPassword) {
            return add("userPassword", userPassword);
        }

        /**
         * Password for full access on the resulting PDF(s).
         */
        public PdfMergeOptions ownerPassword(String ownerPassword) {
            return add("ownerPassword", ownerPassword);
        }
    }
    //endregion
}
