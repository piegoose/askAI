# AskAI – Spring AI Product Advisor

**AskAI** is a simple chatbot built with **Spring Boot** and **Spring AI** that answers user questions using a **VectorStore knowledge base** (RAG – Retrieval-Augmented Generation).  
The knowledge is loaded from a CSV file containing product data (e.g., tires, oils, and ATV parts).

---

## Getting Started

###  Requirements
- Java **17+**
- Maven **3.9+**
- OpenAI API key (`OPENAI_API_KEY`)

---

### Configuration

Set your OpenAI API key in `.env` or `application.properties`:

```properties```
spring.ai.openai.api-key=${OPENAI_API_KEY}
spring.ai.openai.chat.options.model=gpt-4o
spring.ai.openai.embedding.options.model=text-embedding-3-small

### API endpoints
curl "http://localhost:8080/faq?message=Which mud tires do you recommend for an ATV?"
