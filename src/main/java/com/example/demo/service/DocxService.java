package com.example.demo.service;
import com.example.demo.dto.CombinedData;
import com.example.demo.dto.QuestionData;
import com.example.demo.dto.QuestionHtmlData;
import com.example.demo.dto.Warning;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
public class DocxService {

    public CombinedData extractQuestions(String htmlContent) {

        Document doc = Jsoup.parse(htmlContent);
        Elements paragraphs = doc.select("p");

        List<QuestionData> questions = new ArrayList<>();
        List<QuestionHtmlData> htmlDataList = new ArrayList<>();
        List<Warning> warnings = new ArrayList<>();
        QuestionData currentQuestion = null;
        QuestionHtmlData currentHtmlData = null;

        for (int i = 0; i < paragraphs.size(); i++) {
            Element paragraph = paragraphs.get(i);
            String text = paragraph.text().trim();
            boolean isValidQuestion = true;

            // Detect questions using "startsWith" for "(number.)" format
            if (text.matches("^\\(\\d+\\.\\)\\s?.*")) {
                if (currentQuestion != null) {
                    questions.add(currentQuestion); // Save previous question before starting a new one
                    htmlDataList.add(currentHtmlData); // Save previous HTML data as well
                }

                int questionNumber = extractQuestionNumber(text);
                currentQuestion = new QuestionData(questionNumber);
                currentHtmlData = new QuestionHtmlData();
                currentHtmlData.setQuestionHtml(cleanHtmlTags(paragraph.html())); // Save question as HTML

                // Handle multi-line questions
                while (i + 1 < paragraphs.size() && !paragraphs.get(i + 1).text().startsWith("(a.)")) {
                    i++;
                    Element multiLineParagraph = paragraphs.get(i);
                    String multiLineText = multiLineParagraph.text().trim();

                    // Check for unexpected start patterns in multi-line questions
                    if (multiLineText.matches("^\\(\\d+\\.\\)\\s?.*")) {
                        warnings.add(new Warning(questionNumber, "Unexpected new question comes " + multiLineText));
                        isValidQuestion=false;
                        break;
                    } else if (multiLineText.startsWith("(b.)") || multiLineText.startsWith("(c.)") || multiLineText.startsWith("(d.)")) {
                        warnings.add(new Warning(questionNumber, "Unexpected option format in multi-line question: " + multiLineText));
                        isValidQuestion=false;
                        break;
                    } else if (multiLineText.startsWith("Ans:")) {
                        warnings.add(new Warning(questionNumber, "Unexpected answer format in multi-line question: " + multiLineText));
                        isValidQuestion=false;
                        break;
                    } else if (multiLineText.startsWith("Exp:")) {
                        warnings.add(new Warning(questionNumber, "Unexpected explanation format in multi-line question: " + multiLineText));
                        isValidQuestion=false;
                        break;
                    }

                    currentHtmlData.appendQuestionHtml(cleanHtmlTags(multiLineParagraph.outerHtml()));
                }

                if (!isValidQuestion) {
                    // Skip storing the invalid question and move to the next iteration
                    currentQuestion = null;
                    currentHtmlData = null;
                    continue; // Moves to the next iteration of the 'for' loop
                }

                // Extract options
                while (i + 1 < paragraphs.size() && !paragraphs.get(i + 1).text().startsWith("Ans:") && !paragraphs.get(i + 1).text().startsWith("Exp:") && !paragraphs.get(i + 1).text().matches("^\\(\\d+\\.\\)\\s?.*")) {
                    i++;
                    Element optionParagraph = paragraphs.get(i);
                    if (optionParagraph.text().startsWith("(a.)") || optionParagraph.text().startsWith("(b.)") || optionParagraph.text().startsWith("(c.)") || optionParagraph.text().startsWith("(d.)")) {
                        currentQuestion.incrementOptionCount();
                        currentHtmlData.addOptionHtml(cleanHtmlTags(optionParagraph.html()));
                    }
                }

                // Extract answer
                if (i + 1 < paragraphs.size() && paragraphs.get(i + 1).text().startsWith("Ans:")) {
                    i++;
                    currentQuestion.setHasAnswer(true);
                    String answerText = paragraphs.get(i).text();
                    String contentAfterAns = answerText.substring("Ans:".length()).trim();
                    currentHtmlData.setAnswerHtml(cleanHtmlTags(contentAfterAns));
                }

                // Extract explanation
                if (i + 1 < paragraphs.size() && paragraphs.get(i + 1).text().startsWith("Exp:")) {
                    i++;
                    currentQuestion.setHasExplanation(true);
                    currentHtmlData.setExplanationHtml(cleanHtmlTags(paragraphs.get(i).html()));
                    while (i + 1 < paragraphs.size() && !(paragraphs.get(i + 1).text().startsWith("(") && Character.isDigit(paragraphs.get(i + 1).text().charAt(1)))) {
                        i++;
                        currentHtmlData.appendExplanationHtml(cleanHtmlTags(paragraphs.get(i).outerHtml()));
                    }
                }
            }
        }

        if (currentQuestion != null) {
            questions.add(currentQuestion);
            htmlDataList.add(currentHtmlData);
        }

        // Return combined data
        CombinedData combinedData = new CombinedData();
        combinedData.setQuestionDataList(questions);
        combinedData.setQuestionHtmlDataList(htmlDataList);
        combinedData.setWarnings(warnings);
        return combinedData;
    }

    private int extractQuestionNumber(String text) {
        try {
            String number = text.substring(1, text.indexOf('.')).trim();
            return Integer.parseInt(number);
        } catch (NumberFormatException | IndexOutOfBoundsException e) {
            return -1;
        }
    }

    public File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convFile = File.createTempFile("uploaded", ".docx");
        try (FileOutputStream fos = new FileOutputStream(convFile)) {
            fos.write(file.getBytes());
        }
        return convFile;
    }


    // clean html
    private String cleanHtmlTags(String input) {
        if (input == null) return null;
        return input.replaceAll("(?i)</?(p|strong)>", "");
    }
}


