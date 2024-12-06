DOCX to HTML Question Extractor
This project provides a Spring Boot-based REST API to convert .docx files into HTML and extract structured question data, including options, answers, and explanations. It also identifies any warnings during parsing.

Features
Convert .docx to HTML using the zwobble.mammoth library.
Extract structured questions with options, answers, and explanations from HTML content.
Handle multi-line questions and detect errors/warnings.
Modular design using Spring Boot with services and DTOs.
Prerequisites
Java 17 or later
Maven
Spring Boot
Mammoth Library for DOCX to HTML conversion
Jsoup for HTML parsing
Installation
Clone the repository:

bash
Copy code
git clone https://github.com/your-repo/docx-question-extractor.git
cd docx-question-extractor
Build the project:

bash
Copy code
mvn clean install
Run the application:

bash
Copy code
mvn spring-boot:run
Endpoints
1. POST /convert-doc-to-html
Description: Converts a DOCX file to HTML and extracts structured question data.

URL: /convert-doc-to-html
Method: POST![Screenshot (1918)](https://github.com/user-attachments/assets/59d9bb97-0f71-4d98-8a09-5dbb6d22b334)

Request Parameters:
file (MultipartFile): DOCX file to be uploaded.


Response:
A CombinedData object containing:
List of extracted QuestionData
List of QuestionHtmlData (raw HTML)
List of Warning messages


DTOs
CombinedData: Combines questions, HTML, and warnings.
QuestionData: Stores metadata for each question.
QuestionHtmlData: Stores raw HTML data for questions, options, answers, and explanations.
Warning: Represents parsing warnings.
Services
DocxService:
Converts MultipartFile to a temporary file.
Parses HTML using Jsoup.
Extracts structured question data.
Controller
DocxConverter:
Accepts file uploads and processes them using DocxService.
