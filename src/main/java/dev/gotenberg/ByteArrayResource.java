package dev.gotenberg;

class ByteArrayResource extends org.springframework.core.io.ByteArrayResource {
    private final String filename;

    public ByteArrayResource(byte[] content, String filename) {
        super(content);
        this.filename = filename;
    }

    @Override
    public String getFilename() {
        return filename;
    }
}
