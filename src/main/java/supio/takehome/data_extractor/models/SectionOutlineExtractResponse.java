package supio.takehome.data_extractor.models;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record SectionOutlineExtractResponse(
        @JsonPropertyDescription("Cection heading for present section") String heading,
        @JsonPropertyDescription("Bullet points summarizing key points and retaining key entities in present section") List<String> points) {

    @Override
    public String heading() {
        return heading;
    }

    @Override
    public List<String> points() {
        return points;
    }
}
