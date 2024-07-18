package supio.takehome.data_extractor.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DocumentExtract {

    private String title;
    private List<String> entities;
    private String summary;

    private Map<String, List<String>> outline;
}
