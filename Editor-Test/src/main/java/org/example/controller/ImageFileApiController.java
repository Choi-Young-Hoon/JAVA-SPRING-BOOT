package org.example.controller;

import org.example.service.ImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ImageFileApiController {

    @Autowired
    private final ImageService imageService;

    @Autowired
    private final ResourceLoader resourceLoader;

    @PostMapping("/image/upload")
    public ResponseEntity<String> imageUpload(@RequestParam("file")MultipartFile multipartFile) {
        String responseUrl = this.imageService.getUploadPath();

        try {
            String fileName = this.imageService.save(multipartFile);
            responseUrl += fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }

        return ResponseEntity.status(HttpStatus.OK).body(responseUrl);
    }

    @GetMapping("/image/{filename}")
    public ResponseEntity<?> imageDownload(@PathVariable("filename") String filename) {
        try {
            Resource resource = this.resourceLoader.getResource("file:" + this.imageService.getFilePath(filename));
            return ResponseEntity.ok().body(resource);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().build();
        }
    }

}
