package org.superbiz.moviefun.blobstore;

import java.io.InputStream;

public class Blob {
    public final Long id;
    public final byte[] bytesStream;
    public final String contentType;
    public final String path;

    public Blob(Long id, byte[] bytesStream, String contentType, String path) {
        this.id = id;
        this.bytesStream = bytesStream;
        this.contentType = contentType;
        this.path=path;
    }
}
