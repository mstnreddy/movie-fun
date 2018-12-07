package org.superbiz.moviefun.blobstore;

import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.util.IOUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;

public class S3Store implements BlobStore {

    private AmazonS3Client s3Client;
    private String photoStoragebucket;

    public S3Store(AmazonS3Client s3Client, String photoStoragebucket) {
        this.s3Client = s3Client;
        this.photoStoragebucket = photoStoragebucket;
    }

    @Override
    public void put(Blob blob) throws IOException {
        s3Client.putObject(photoStoragebucket, String.valueOf(blob.id), Files.write(Paths.get("filename"), blob.bytesStream).toFile());
    }

    @Override
    public Optional<Blob> get(Long id) throws IOException {
        S3Object s3Object = s3Client.getObject(photoStoragebucket, String.valueOf(id));
        byte[] byteArray = IOUtils.toByteArray(s3Object.getObjectContent());
        Blob blob = new Blob(id, byteArray, s3Object.getObjectMetadata().getContentType(), "");
        return Optional.of(blob);


    }

    @Override
    public void deleteAll() {

    }
}
