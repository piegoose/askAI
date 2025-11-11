package pl.piegoose.askai;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.prompt.PromptTemplate;
import org.springframework.ai.document.Document;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.reader.TextReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Configuration
public class RagConfig {

    private static final Logger log = LoggerFactory.getLogger(RagConfig.class);

    @Value("classpath:docs/database.csv")
    private Resource faq;
    @Value("vectorstore.json")
    private String  vectorStoreName;


    @Bean
    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
        SimpleVectorStore store = SimpleVectorStore.builder(embeddingModel).build();

        File vectorStoreFile = getVectorStoreFile();
        if (vectorStoreFile.exists()) {
            log.info("VectorStore file exists, loading");
            store.load(vectorStoreFile);
        } else {
            log.info("VectorStore file does not exist, will be created on save");
            TextReader textReader = new TextReader(faq);
            textReader.getCustomMetadata().put("filename","database1.1.csv");
            List<Document> documents = textReader.get();
            TokenTextSplitter tokenTextSplitter = new TokenTextSplitter();
            List<Document> splitDocuments  = tokenTextSplitter.apply(documents);

            store.add(splitDocuments);
            store.save(vectorStoreFile);
        }
        return store;
    }

    private File getVectorStoreFile(){
        Path path = Paths.get("src","main","resources","data");
        String absolutePath = path.toFile().getAbsolutePath() +"/"+vectorStoreName;
        return new File(absolutePath);


    }
}