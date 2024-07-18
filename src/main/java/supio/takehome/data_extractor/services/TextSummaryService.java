package supio.takehome.data_extractor.services;

import supio.takehome.data_extractor.models.DocumentExtract;

import java.util.Iterator;

public interface TextSummaryService {

    DocumentExtract summarizeText(String fileText);

    Iterator<String> chunkString(String text, int chunkSize);
}
