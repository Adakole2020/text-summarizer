package supio.takehome.data_extractor.services;

import org.springframework.web.multipart.MultipartFile;
import supio.takehome.data_extractor.models.DocumentExtract;

import java.io.IOException;

public interface TextExtractService {

    DocumentExtract extractTextFromFile(MultipartFile file) throws IOException;
}
