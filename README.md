# Running and Testing the Application

This guide covers how to run the packaged version of this data extractor application and test it using Postman.

## Setting Environment Variables

Before running the application, you need to set the environment variable for the OpenAI API key. This is crucial for the application to interact with OpenAI services.

- For **Unix/Linux/macOS** systems, use the export command in your terminal:
```shell
export SPRING_AI_OPENAI_API_KEY=your_openai_api_key_here
```

- For **Windows** systems, use the set command in Command Prompt:
```shell
set SPRING_AI_OPENAI_API_KEY=your_openai_api_key_here
```

Replace `your_openai_api_key_here` with your actual OpenAI API key.

## Running the Application

1. Ensure you have Java Runtime Environment (JRE) installed on your system. You can check this by running `java -version` in your terminal. If Java is not installed, please install the latest version from [Oracle's website](https://www.oracle.com/java/technologies/javase-jdk11-downloads.html) or use OpenJDK.

2. Locate the packaged JAR file of the application. This file should be named something like `application-name-version.jar` (e.g., `data-extractor-1.0.0.jar`).

3. Open your terminal or command prompt.

4. Navigate to the directory containing the JAR file.

5. Run the application using the following command:

```shell
java -jar data-extractor-0.0.1-SNAPSHOT.jar
```
Replace `data-extractor-0.0.1-SNAPSHOT.jar` with the actual name of the JAR file as stored on your computer.

The application should now be running and accessible on `http://localhost:8080`.

## Testing the Application with Postman

1. Open Postman. If you don't have Postman installed, download it from [Postman's website](https://www.postman.com/downloads/) and install it.

2. Create a new request by clicking the `New` button and selecting `Request`.

3. Set the request method to `POST`.

4. Enter the URL for the file upload endpoint. If running locally, this will be `http://localhost:8080/api/v1/extract`.

5. In the `Body` tab, select `form-data`.

6. Add a new key of type `File` with the key name `file`. Click on the `Select Files` button and choose the example text file you want to upload. The example text file can be found under the `resources/templates` folder of the project.

7. Click the `Send` button to make the request.

### Expected Response

Upon successfully uploading the file, you should receive a JSON response containing the extracted data. The structure of the response will depend on the implementation of your application. An example response might look like this:

```json
{
  "title": "Example Title",
  "entities": ["Entity1", "Entity2"],
  "outline": {
    "Heading": ["Point1", "Point2"]
  },
  "summary": "This is a summary of the text file."
}
```

This response indicates that the application has successfully processed the uploaded text file and returned the extracted title, entities, outline, and summary.

## Approach

The application adopts a Model-View-Controller (MVC) architecture, integrating two primary services: a file extraction service and a data summarization service. Here's a breakdown of the approach:

1. **File Extraction Service**: This service is responsible for handling the initial processing of the uploaded files. It verifies that the uploaded file is a text file, which is essential for the subsequent data processing steps. If the file meets the criteria, the service then extracts the content of the file to be passed on for further analysis.

2. **Data Summarization Service**: Utilizing OpenAI's GPT-3.5 Turbo, this service takes the extracted text content and generates an outline. This outline serves as a basis for identifying key entities within the text and constructing a comprehensive summary of the content. The service employs a chunking strategy for longer text files, dividing the text based on the number of lines to ensure that each prompt sent to the AI model stays within a manageable token limit (approximately 12,000 tokens). This approach helps in maintaining the efficiency and accuracy of the model's responses.

3. **Title Extraction**: The application assumes that the title of the document may span up to the first three lines, accommodating documents with varying title formats. This flexibility ensures that the title is accurately captured even in less structured documents.

4. **Bean Output Parsers and Prompt Templates**: To ensure that the responses from the AI model are formatted correctly and align with the application's requirements, bean output parsers and well-crafted prompt templates are utilized. These tools help in structuring the AI's output into a usable format for the application. The associated prompt templates could be found in the `resources/templates` folder of the project.

5. **Potential Improvements**:
    - **Reactive Framework**: Implementing a reactive framework could enhance the application's performance by enabling asynchronous request handling. This would reduce blocking while waiting for responses from the OpenAI model, leading to a more efficient processing flow.
    - **Preserving Outline History**: For text files processed in chunks, maintaining a history of previous chunks' outlines could improve the consistency and relevance of the model's output across different sections of the text.