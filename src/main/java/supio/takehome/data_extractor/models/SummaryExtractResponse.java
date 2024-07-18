package supio.takehome.data_extractor.models;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record SummaryExtractResponse(
        @JsonPropertyDescription("Summary of the entire document") String summary
) {
}
