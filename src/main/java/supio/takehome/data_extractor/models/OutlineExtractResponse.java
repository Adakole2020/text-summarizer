package supio.takehome.data_extractor.models;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record OutlineExtractResponse(
        @JsonPropertyDescription("Extract a detailed outline of the document, including section headings\n" +
                "and bullet points summarizing key points in each section.") List<SectionOutlineExtractResponse> outline
) {
}
