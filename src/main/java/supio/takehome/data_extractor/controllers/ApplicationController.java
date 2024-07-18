package supio.takehome.data_extractor.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import supio.takehome.data_extractor.models.DocumentExtract;
import supio.takehome.data_extractor.services.TextExtractService;

@RestController
@RequiredArgsConstructor
@RequestMapping(ApplicationController.EXTRACT_PATH)
public class ApplicationController {
    public static final String EXTRACT_PATH = "/api/v1/extract";

    private final TextExtractService textExtractService;


    @PostMapping
    public ResponseEntity<DocumentExtract> extractData(@RequestParam("file") MultipartFile file) {
        try {
            // Extract data from the given document
            return ResponseEntity.ok(textExtractService.extractTextFromFile(file));
        } catch (Exception e) {
            // Bad request for any exceptions not expected
            return ResponseEntity.badRequest().build();
        }
    }
}
