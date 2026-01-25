package dev.gotenberg;

import java.io.InputStream;

class InputStreamResource extends org.springframework.core.io.InputStreamResource {
    private final String filename;

    public InputStreamResource(InputStream inputStream, String filename) {
        super(inputStream);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
