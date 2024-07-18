package supio.takehome.data_extractor.models;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record TitleExtractResponse(
        @JsonPropertyDescription("The title of the document (if available, otherwise first line of the document)")  String title
) {
}
