package supio.takehome.data_extractor.services;


import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.tokenizer.JTokkitTokenCountEstimator;
import org.springframework.ai.tokenizer.TokenCountEstimator;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import supio.takehome.data_extractor.models.*;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TextSummaryServiceImpl implements TextSummaryService {

    private final ChatClient chatClient;

    private final TokenCountEstimator tokenCountEstimator;

    private final static int MAX_TOKENS = 10000;

    public TextSummaryServiceImpl(ChatClient.Builder chatClientBuilder, ChatMemory chatMemory) {
        this.chatClient = chatClientBuilder
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory))// CHAT MEMORY
                .build();
        this.tokenCountEstimator = new JTokkitTokenCountEstimator();
    }

    @Value("classpath:templates/get-summary-for-outline.st")
    private Resource getSummaryPrompt;

    @Value("classpath:templates/get-entities-for-text.st")
    private Resource getEntitiesPrompt;

    @Value("classpath:templates/get-title-for-text.st")
    private Resource getTitlePrompt;

    @Value("classpath:templates/get-outline-for-text.st")
    private Resource getOutlinePrompt;


    @Override
    public DocumentExtract summarizeText(String fileText) {

        // Use entire text to build outline
        OutlineExtractResponse outline = extractOutline(fileText);

        // Use only the first three lines of text file to get title
        TitleExtractResponse title =
                extractTitle(fileText.lines().limit(3).reduce("", (a, b) -> a + b + "\n"));

        // Use outline to build summary
        SummaryExtractResponse summary = extractSummary(outline.outline().toString());

        // Use outline to extract entities
        EntityExtractResponse entities = extractEntities(outline.outline().toString());

        return DocumentExtract.builder().entities(entities.entities()).title(title.title()).outline(outline.outline().stream().collect(Collectors.toMap(SectionOutlineExtractResponse::heading, SectionOutlineExtractResponse::points))).summary(summary.summary()).build();
    }


    EntityExtractResponse extractEntities(String fileText) {
        BeanOutputConverter<EntityExtractResponse> parser = new BeanOutputConverter<>(EntityExtractResponse.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getEntitiesPrompt);
        Prompt prompt = promptTemplate.create(Map.of("text", fileText, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        return parser.convert(response.getResult().getOutput().getContent());
    }

    TitleExtractResponse extractTitle(String fileText) {
        BeanOutputConverter<TitleExtractResponse> parser = new BeanOutputConverter<>(TitleExtractResponse.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getTitlePrompt);
        Prompt prompt = promptTemplate.create(Map.of("text", fileText, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        return parser.convert(response.getResult().getOutput().getContent());
    }

    OutlineExtractResponse extractOutline(String fileText) {
        BeanOutputConverter<OutlineExtractResponse> parser = new BeanOutputConverter<>(OutlineExtractResponse.class);


        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getOutlinePrompt);

        int num_tokens = tokenCountEstimator.estimate(fileText);
        if (num_tokens >= MAX_TOKENS) {
            Iterator<String> chunks = chunkString(fileText, (int) Math.ceil((double) num_tokens / MAX_TOKENS));
            List<SectionOutlineExtractResponse> outlines = new ArrayList<>();
            while (chunks.hasNext()) {
                String chunk = chunks.next();
                Prompt prompt = promptTemplate.create(Map.of("text", chunk, "format", format));
                ChatResponse response = chatClient.prompt(prompt).call().chatResponse();
                outlines.addAll(parser.convert(response.getResult().getOutput().getContent()).outline());
            }
            return new OutlineExtractResponse(outlines);
        }
        Prompt prompt = promptTemplate.create(Map.of("text", fileText, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        return parser.convert(response.getResult().getOutput().getContent());
    }


    public Iterator<String> chunkString(String str, int numChunks) {
        // Split the string by newline
        String[] lines = str.split("\n");
        int totalLines = lines.length;
        int linesPerChunk = (int) Math.ceil((double) totalLines / numChunks);

        List<String> chunks = new ArrayList<>();

        for (int i = 0; i < totalLines; i += linesPerChunk) {
            StringBuilder chunk = new StringBuilder();
            for (int j = i; j < i + linesPerChunk && j < totalLines; j++) {
                chunk.append(lines[j]).append("\n");
            }
            chunks.add(chunk.toString().trim()); // trim to remove trailing newline
        }

        return chunks.iterator();
    }

    SummaryExtractResponse extractSummary(String fileText) {
        BeanOutputConverter<SummaryExtractResponse> parser = new BeanOutputConverter<>(SummaryExtractResponse.class);
        String format = parser.getFormat();
        PromptTemplate promptTemplate = new PromptTemplate(getSummaryPrompt);
        Prompt prompt = promptTemplate.create(Map.of("text", fileText, "format", format));

        ChatResponse response = chatClient.prompt(prompt).call().chatResponse();

        return parser.convert(response.getResult().getOutput().getContent());
    }
}