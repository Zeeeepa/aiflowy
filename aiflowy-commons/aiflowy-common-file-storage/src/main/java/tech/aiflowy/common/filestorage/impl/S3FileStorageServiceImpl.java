package tech.aiflowy.common.filestorage.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import tech.aiflowy.common.filestorage.FileStorageService;
import tech.aiflowy.common.filestorage.OssClient;
import tech.aiflowy.common.filestorage.StorageConfig;
import tech.aiflowy.common.util.DateUtil;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Date;
import java.util.UUID;


@Component("s3")
public class S3FileStorageServiceImpl implements FileStorageService {
    private static final Logger LOG = LoggerFactory.getLogger(S3FileStorageServiceImpl.class);


    private OssClient client;

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        StorageConfig instance = StorageConfig.getInstance();
        client = new OssClient(instance);
    }


    @Override
    public String save(MultipartFile file) {
        try {
            String path = client.upload(file);
            return path;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Override
    public InputStream readStream(String path) throws IOException {
        return client.getObjectContent(path);
    }


}
