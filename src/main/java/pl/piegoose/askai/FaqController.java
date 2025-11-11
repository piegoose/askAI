package pl.piegoose.askai;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.document.Document;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class FaqController {

    private final ChatClient chat;
    private final VectorStore vectorStore;

    public FaqController(ChatClient.Builder builder, VectorStore vectorStore) {
        this.vectorStore = vectorStore;
        this.chat = builder
                .defaultSystem("""
                    Jesteś doradcą produktowym sklepu z częściami do quadów.
                    Jeśli informacji nie ma w dostarczonym KONTEKŚCIE, odpowiedz: "Nie mam tego w bazie."
                    Zwracaj konkrety: nazwa, kategoria, cena, marka.
                """)
                .build();
    }


    @GetMapping("/faq1")
    public String faqGet(@RequestParam(defaultValue = "Jakie oleje 10W40 do quada polecasz?") String message,
                         @RequestParam(name = "n", defaultValue = "3") int n) {
        return answerWithTopK(message, n);
    }

    //POST with JSON-em: { "message": "..." }
    @PostMapping("/faq2")
    public String faqPost(@RequestBody Map<String, Object> payload,
                          @RequestParam(name = "n", defaultValue = "3") int n) {
        String message = String.valueOf(payload.get("message"));
        return answerWithTopK(message, n);
    }

    // Cleat LLM w/o RAG
    @PostMapping("/question")
    public String question(@RequestBody Map<String, String> payload) {
        String message = payload.get("message");
        return chat.prompt().user(message).call().content();
    }


    private String answerWithTopK(String message, int n) {
        List<Document> docs = vectorStore.similaritySearch(
                SearchRequest.builder()
                        .query(message)
                        .topK(n)
                        .build()
        );

        String context = docs.stream()
                .map(Document::getText)
                .limit(n)
                .collect(Collectors.joining("\n---\n"));

        String systemPrompt = """
                    Na podstawie KONTEKSTU wypisz dokładnie %d propozycje pasujące do pytania.
                    Format każdej pozycji:
                    - Nazwa (brand) — cena PLN — kategoria > podkategoria
                    - Krótko: dlaczego polecasz (1 zdanie)
                    Na końcu dodaj krótkie porównanie (1–2 zdania).
                    Jeśli czegoś brak w kontekście, nie zmyślaj.
                """.formatted(n);

        return chat.prompt()
                .system(systemPrompt)
                .user("Pytanie: " + message + "\n\nKONTEKST:\n" + context)
                .options(ChatOptions.builder()
                        .temperature(0.2)
                        .build())
                .call()
                .content();
    }
}
