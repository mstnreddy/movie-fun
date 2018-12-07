package org.superbiz.moviefun.blobstore;

import org.apache.tika.Tika;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;

import static java.lang.ClassLoader.getSystemResource;
import static java.lang.String.format;
import static java.nio.file.Files.readAllBytes;

public class FileStore implements BlobStore {

    @Override
    public void put(Blob blob) throws IOException {
        // ...

        saveUploadToFile(blob);
    }

    @Override
    public Optional<Blob> get(Long id) throws IOException {
        // ...

        Path coverFilePath = null;
        try {
            coverFilePath = getExistingCoverPath(id);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        byte[] imageBytes = readAllBytes(coverFilePath);

        Blob blob=new Blob(id, imageBytes , new Tika().detect(coverFilePath),coverFilePath.toUri().getPath());

        return Optional.of(blob);

    }

    @Override
    public void deleteAll() {
        // ...
    }

    private void saveUploadToFile(Blob blob) throws IOException {
        File targetFile= getCoverFile(blob.id);

        targetFile.delete();
        targetFile.getParentFile().mkdirs();
        targetFile.createNewFile();

        try (FileOutputStream outputStream = new FileOutputStream(targetFile)) {
            outputStream.write(blob.bytesStream);
        }
    }



    private File getCoverFile(@PathVariable long albumId) {
        String coverFileName = format("covers/%d", albumId);
        return new File(coverFileName);
    }

    private Path getExistingCoverPath(@PathVariable long albumId) throws URISyntaxException {
        File coverFile = getCoverFile(albumId);
        Path coverFilePath;

        if (coverFile.exists()) {
            coverFilePath = coverFile.toPath();
        } else {
            coverFilePath = Paths.get(getSystemResource("default-cover.jpg").toURI());
        }

        return coverFilePath;
    }

}