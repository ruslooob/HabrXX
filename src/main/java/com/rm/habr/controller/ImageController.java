package com.rm.habr.controller;

import com.rm.habr.service.FileStorageService;
import lombok.AllArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@AllArgsConstructor
public class ImageController {
    private FileStorageService fileStorageService;

    @GetMapping(value = "/image", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<Resource> getImage(@RequestParam String filePath) {
        return ResponseEntity.ok().body(fileStorageService.load(filePath));
    }
}
