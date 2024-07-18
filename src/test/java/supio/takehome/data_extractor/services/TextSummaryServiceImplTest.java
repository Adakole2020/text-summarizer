package supio.takehome.data_extractor.services;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.ai.chat.client.ChatClient;
import supio.takehome.data_extractor.models.*;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doReturn;

class TextSummaryServiceImplTest {

    @Mock
    private ChatClient.Builder chatClientBuilder;

    @InjectMocks
    @Spy
    private TextSummaryServiceImpl textSummaryService;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // Mock the internal method calls
        doReturn(new OutlineExtractResponse(List.of(new SectionOutlineExtractResponse("Heading", List.of("Point1", "Point2"))))).when(textSummaryService).extractOutline(anyString());
        doReturn(new TitleExtractResponse("Test Title")).when(textSummaryService).extractTitle(anyString());
        doReturn(new EntityExtractResponse(List.of("Entity1", "Entity2"))).when(textSummaryService).extractEntities(anyString());
        doReturn(new SummaryExtractResponse("Test Summary")).when(textSummaryService).extractSummary(anyString());
    }

    @Test
    void testChunkString() {
        String text = "This\nis\na\ntest\nstring\nwith\nchunks";
        Iterator<String> actual = textSummaryService.chunkString(text, 4);

        List<String> list = new ArrayList<>();

        // Add each element of iterator to the List
        actual.forEachRemaining(list::add);

        assertEquals(Arrays.asList(new String[]{"This\nis", "a\ntest", "string\nwith", "chunks" }), list);
    }

    @Test
    void testSummarizeText() throws IOException {
        // Prepare the expected DocumentExtract object
        DocumentExtract expected = DocumentExtract.builder()
                .title("Test Title")
                .entities(List.of("Entity1", "Entity2"))
                .outline(Map.of("Heading", List.of("Point1", "Point2")))
                .summary("Test Summary")
                .build();

        // Call the method under test
        DocumentExtract actual = textSummaryService.summarizeText("Sample text for testing");

        // Assert the expected values
        assertEquals(expected.getTitle(), actual.getTitle());
        assertEquals(expected.getEntities(), actual.getEntities());
        assertEquals(expected.getOutline(), actual.getOutline());
        assertEquals(expected.getSummary(), actual.getSummary());
    }
}