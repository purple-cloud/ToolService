package no.ntnu.toolservice.service;

import no.ntnu.toolservice.exception.StorageException;
import no.ntnu.toolservice.exception.StorageFileNotFoundException;
import no.ntnu.toolservice.files.StorageProperties;
import no.ntnu.toolservice.files.StorageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.nio.file.*;
import java.util.stream.Stream;

// TODO Write good comments in this class

@Service
public class FileSystemStorageService implements StorageService {

    private final Path rootPath;

    @Autowired
    public FileSystemStorageService(StorageProperties storageProperties) {
        this.rootPath = Paths.get(storageProperties.getLocation());
    }

    /**
     * Creates a new directory if there isn't already one
     */
    @Override
    public void init() {
        try {
            if (!Files.isDirectory(rootPath)) {
                Files.createDirectories(this.rootPath);
            } else {
                System.out.println("Directory already exists...");
            }
        }
        catch (IOException e) {
            throw new StorageException("Could not initialize storage", e);
        }
    }

    @Override
    public void store(MultipartFile file) {
        String filename = StringUtils.cleanPath(file.getOriginalFilename());
        if (filename.contains(".jpg")) {
            filename = filename.split("j", 2)[0].concat("png");
        }
        try {
            if (file.isEmpty()) {
                throw new StorageException("Failed to store empty file " + filename);
            }
            if (filename.contains("..")) {
                // This is a security check
                throw new StorageException(
                        "Cannot store file with relative path outside current directory "
                                + filename);
            }
            try (InputStream inputStream = file.getInputStream()) {
                Files.copy(inputStream, this.rootPath.resolve(filename),
                        StandardCopyOption.REPLACE_EXISTING);
            } catch (NoSuchFileException e) {
                e.printStackTrace();
            }
        }
        catch (IOException e) {
            throw new StorageException("Failed to store file " + filename, e);
        }
    }

    @Override
    public Stream<Path> loadAll() {
        try {
            return Files.walk(this.rootPath, 1)
                    .filter(path -> !path.equals(this.rootPath))
                    .map(this.rootPath::relativize);
        }
        catch (IOException e) {
            throw new StorageException("Failed to read stored files", e);
        }
    }

    @Override
    public Path load(String filename) {
        return this.rootPath.resolve(filename);
    }

    @Override
    public Resource loadAsResource(String filename) {
        try {
            Path file = load(filename);
            Resource resource = new UrlResource(file.toUri());
            if (resource.exists() || resource.isReadable()) {
                return resource;
            }
            else {
                throw new StorageFileNotFoundException(
                        "Could not read file: " + filename);

            }
        }
        catch (MalformedURLException e) {
            throw new StorageFileNotFoundException("Could not read file: " + filename, e);
        }
    }

    @Override
    public void deleteAll() {
        FileSystemUtils.deleteRecursively(this.rootPath.toFile());
    }
}
