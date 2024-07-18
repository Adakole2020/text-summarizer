package supio.takehome.data_extractor.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import supio.takehome.data_extractor.models.DocumentExtract;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TextExtractServiceImpl implements TextExtractService {

    private final TextSummaryService textSummaryService;

    @Override
    public DocumentExtract extractTextFromFile(MultipartFile file) throws IOException {

        if(file.isEmpty()) {
            // Exception for file being empty
            throw new IOException("File is empty");
        }

        // Check if file is a text file
        String fileName = file.getOriginalFilename();
        if(fileName == null || !fileName.endsWith(".txt")) {
            // Exception for file not being a text file
            throw new IOException("File is not a text file");
        }

        // Read the file
        String text = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))
                .lines()
                .collect(Collectors.joining("\n"));

        // Use summary service to summarize the text
        DocumentExtract details = textSummaryService.summarizeText(text);

        // Return document extract
        return details;
    }
}
