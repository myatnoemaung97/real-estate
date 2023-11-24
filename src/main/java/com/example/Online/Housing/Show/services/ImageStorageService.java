package com.example.Online.Housing.Show.services;

import com.backblaze.b2.client.B2StorageClient;
import com.backblaze.b2.client.B2StorageClientFactory;
import com.backblaze.b2.client.contentSources.B2ContentTypes;
import com.backblaze.b2.client.contentSources.B2FileContentSource;
import com.backblaze.b2.client.credentialsSources.B2Credentials;
import com.backblaze.b2.client.exceptions.B2Exception;
import com.backblaze.b2.client.structures.B2Bucket;
import com.backblaze.b2.client.structures.B2FileVersion;
import com.backblaze.b2.client.structures.B2UploadFileRequest;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.*;
import java.io.File;
import java.io.IOException;

@Service
@NoArgsConstructor
public class ImageStorageService {

    private final String keyId = "2956c37f412b";
    private final String keyName = "00526a7c9a9e681c0cb147d45c5586eee7f3c4d231";
    private final B2StorageClient client = B2StorageClientFactory.createDefaultFactory().create(keyId, keyName, "housing app");

    public String uploadImage(File file, String imageName, String bucketName) {
        B2FileContentSource source = B2FileContentSource.build(file);
        B2Bucket bucket = getBucket(bucketName);
        B2UploadFileRequest request = B2UploadFileRequest.builder(bucket.getBucketId(), imageName, B2ContentTypes.B2_AUTO, source).build();
        try {
            B2FileVersion uploadedFile = client.uploadSmallFile(request);
            String fileId = uploadedFile.getFileId();
            String fileUrl = client.getDownloadByIdUrl(fileId);
            return fileUrl;
        } catch (B2Exception e) {
            throw new RuntimeException(e);
        }
    }



    private B2Bucket getBucket(String bucketName) {
        try {
            return client.getBucketOrNullByName(bucketName);
        } catch (B2Exception e) {
            e.printStackTrace();
        }
        return null;
    }


}
