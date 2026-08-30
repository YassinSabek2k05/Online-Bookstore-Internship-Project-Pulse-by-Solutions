package com.pulsebysolutions.onlinebookstoreinternshipproject.interfaces;

import com.pulsebysolutions.onlinebookstoreinternshipproject.entity.BookImage;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface ImageStorageService {

    String store(MultipartFile file) throws IOException;
    BookImage retrieve(String key);
    void delete(String key);
}
