package supio.takehome.data_extractor.controllers;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import supio.takehome.data_extractor.models.DocumentExtract;
import supio.takehome.data_extractor.services.TextExtractService;

import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TextExtractService textExtractService;

    @Test
    void extractData_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Some content".getBytes());
        DocumentExtract expectedResponse = new DocumentExtract();
        expectedResponse.setTitle("Test Title");

        given(textExtractService.extractTextFromFile(file)).willReturn(expectedResponse);

        mockMvc.perform(multipart(ApplicationController.EXTRACT_PATH)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Title"));
    }

    @Test
    void extractData_Failure() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "filename.txt", "text/plain", "Some content".getBytes());

        given(textExtractService.extractTextFromFile(file)).willThrow(new RuntimeException("Test Exception"));

        mockMvc.perform(multipart(ApplicationController.EXTRACT_PATH)
                        .file(file)
                        .contentType(MediaType.MULTIPART_FORM_DATA))
                .andExpect(status().isBadRequest());
    }
}