# AskAI – Spring Boot RAG Chatbot for Product Advice

## Overview  
AskAI is a backend service built in Java 17 with Spring Boot and Spring AI. The service offers natural-language advice about product catalog items (e.g., tires, oils, ATV parts) by leveraging retrieval-augmented generation (RAG) and the OpenAI API.

## Features  
- REST endpoint `/faq?message=…` to ask product-advice questions  
- Knowledge base loaded from CSV into a vector store (RAG)  
- Query processing: user question → embedding → vector store retrieval → OpenAI completion  
- Clean architecture (Controller → Service → Repository/VectorStore)  
- Configurable via `application.properties` / `.env` (OpenAI API key, model options)  
- Ready for containerisation and deployment

## Tech Stack  
- Java 17  
- Spring Boot & Spring AI  
- Maven 3.9+  
- OpenAI API  
- Vector store for retrieval (embedded)  
- REST API (JSON)  
- (Optional) Docker / any container runtime  

## Getting Started  
### Requirements  
- Java 17 or higher  
- Maven 3.9+  
- OpenAI API Key (`OPENAI_API_KEY`)  
### Configuration  
Set your OpenAI API key in `.env` or `application.properties`, e.g.:  
