package com.pulsebysolutions.onlinebookstoreinternshipproject.service;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.BookImage;
import com.pulsebysolutions.onlinebookstoreinternshipproject.exception.ResourceNotFoundException;
import com.pulsebysolutions.onlinebookstoreinternshipproject.interfaces.ImageStorageService;
import com.pulsebysolutions.onlinebookstoreinternshipproject.repository.ImageRespository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

@Service
public class FakeImageManager implements ImageStorageService {

    private static final Set<String> ALLOWED_EXTENSIONS = Set.of(".jpg", ".jpeg", ".png", ".webp");
    private final ImageRespository imageRepository;

    public FakeImageManager(ImageRespository imageRepository) {
        this.imageRepository = imageRepository;
    }

    public String store(MultipartFile file) throws IOException {
        String extension = getExtension(file.getOriginalFilename()); // ".jpg", ".png"
        String key = UUID.randomUUID() + extension;

        BookImage img = BookImage.builder().build();
        img.setKey(key);
        img.setData(file.getBytes());
        img.setContentType(file.getContentType());
        imageRepository.save(img);

        return "/api/images/" + key;
    }


    public String getExtension(String filename) {
        if (filename == null || !filename.contains(".")) {
            throw new IllegalArgumentException("Invalid filename: " + filename);
        }
        String ext = filename.substring(filename.lastIndexOf(".")).toLowerCase();
        if (!ALLOWED_EXTENSIONS.contains(ext)) {
            throw new IllegalArgumentException("Unsupported file extension: " + ext);
        }
        return ext;
    }

    public BookImage retrieve(String key) {
        return imageRepository.findById(key)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found: " + key));
    }
}
