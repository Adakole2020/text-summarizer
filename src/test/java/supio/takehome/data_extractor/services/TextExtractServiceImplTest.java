package supio.takehome.data_extractor.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;
import supio.takehome.data_extractor.models.DocumentExtract;

import java.io.IOException;

import static org.mockito.Mockito.when;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class TextExtractServiceImplTest {

    @Mock
    private TextSummaryService textSummaryService;

    @InjectMocks
    private TextExtractServiceImpl textExtractService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testExtractTextFromFile_EmptyFile() {
        MultipartFile emptyFile = new MockMultipartFile("file", "test.txt", "text/plain", new byte[0]);
        assertThrows(IOException.class, () -> textExtractService.extractTextFromFile(emptyFile));
    }

    @Test
    void testExtractTextFromFile_NotATextFile() {
        MultipartFile notATextFile = new MockMultipartFile("file", "image.png", "image/png", "This is not a text file".getBytes());
        assertThrows(IOException.class, () -> textExtractService.extractTextFromFile(notATextFile));
    }

    @Test
    void testExtractTextFromFile_Success() throws IOException {
        String fileContent = "This is a test file content";
        MultipartFile textFile = new MockMultipartFile("file", "test.txt", "text/plain", fileContent.getBytes());
        DocumentExtract expectedDocumentExtract = new DocumentExtract();
        // Assuming DocumentExtract has a method to set some properties. Adjust according to the actual implementation.
        expectedDocumentExtract.setTitle("Test Title");

        when(textSummaryService.summarizeText(fileContent)).thenReturn(expectedDocumentExtract);

        DocumentExtract actualDocumentExtract = textExtractService.extractTextFromFile(textFile);
        assertEquals(expectedDocumentExtract.getTitle(), actualDocumentExtract.getTitle());
    }
}