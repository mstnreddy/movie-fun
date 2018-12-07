package org.superbiz.moviefun.blobstore;

import java.io.IOException;
import java.util.Optional;

public interface BlobStore {

    void put(Blob blob) throws IOException;

    Optional<Blob> get(Long name) throws IOException;

    void deleteAll();
}