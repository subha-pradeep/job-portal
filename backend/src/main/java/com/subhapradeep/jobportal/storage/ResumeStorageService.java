package com.subhapradeep.jobportal.storage;

import com.subhapradeep.jobportal.common.ApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.UUID;

/** Saves resume PDFs to disk and reads them back. */
@Service
public class ResumeStorageService {

    // Every real PDF file starts with these bytes: "%PDF"
    private static final byte[] PDF_MAGIC = {'%', 'P', 'D', 'F'};

    private final Path uploadDir;

    public ResumeStorageService(@Value("${app.upload-dir}") String uploadDir) throws IOException {
        this.uploadDir = Path.of(uploadDir).toAbsolutePath().normalize();
        Files.createDirectories(this.uploadDir);
    }

    /** Checks the file really is a PDF, saves it under a random name, returns that name. */
    public String store(MultipartFile file, byte[] content) {
        if (file == null || file.isEmpty()) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Please attach your resume as a PDF");
        }
        // Don't trust the file name or Content-Type alone; check the actual bytes
        if (content.length < PDF_MAGIC.length
                || !Arrays.equals(Arrays.copyOf(content, PDF_MAGIC.length), PDF_MAGIC)) {
            throw new ApiException(HttpStatus.BAD_REQUEST, "Resume must be a PDF file");
        }

        // Random name: users can't overwrite each other's files or use "../" tricks
        String storedName = UUID.randomUUID() + ".pdf";
        try {
            Files.write(uploadDir.resolve(storedName), content);
        } catch (IOException e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Could not save the resume");
        }
        return storedName;
    }

    public Resource load(String storedName) {
        Path file = uploadDir.resolve(storedName).normalize();
        if (!file.startsWith(uploadDir) || !Files.exists(file)) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Resume file not found");
        }
        return new FileSystemResource(file);
    }
}
