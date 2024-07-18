package supio.takehome.data_extractor.models;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record EntityExtractResponse(
        @JsonPropertyDescription("Key entities (names, dates, locations)") List<String> entities
) {
}

