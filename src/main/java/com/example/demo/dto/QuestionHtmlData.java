package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QuestionHtmlData {

    private String questionHtml;
    private List<String> optionsHtml = new ArrayList<>();
    private String answerHtml;
    private String explanationHtml;

    public void addOptionHtml(String optionHtml) {
        this.optionsHtml.add(optionHtml);
    }

    public void appendQuestionHtml(String html) {
        this.questionHtml = (this.questionHtml == null ? "" : this.questionHtml) + html;
    }

    public void appendExplanationHtml(String html) {
        this.explanationHtml = (this.explanationHtml == null ? "" : this.explanationHtml) + html;
    }
}
