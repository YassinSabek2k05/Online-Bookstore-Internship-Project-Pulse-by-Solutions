package com.pulsebysolutions.onlinebookstoreinternshipproject.controller;

import com.pulsebysolutions.onlinebookstoreinternshipproject.interfaces.ImageStorageService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/images")
public class ImageStorageController {
    private final ImageStorageService imageStorageService;
    public ImageStorageController(ImageStorageService imageStorageService) {
        this.imageStorageService = imageStorageService;
    }
    @PostMapping
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        return ResponseEntity.ok(imageStorageService.store(file));
    }
    @GetMapping({"/{key}"})
    public ResponseEntity<byte[]> getImage(@PathVariable String key) {
        var img = imageStorageService.retrieve(key);
        return ResponseEntity.ok()
                .header("Content-Type", img.getContentType())
                .body(img.getData());
    }

    @DeleteMapping("/{key}")
    public ResponseEntity<Void> deleteImage(@PathVariable String key) {
        imageStorageService.delete(key);
        return ResponseEntity.noContent().build();
    }

}
