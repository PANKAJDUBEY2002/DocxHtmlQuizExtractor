package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class QuestionData {

    private int questionNumber;
    private int optionCount = 0;
    private boolean hasAnswer = false;
    private boolean hasExplanation = false;

    public QuestionData(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public void incrementOptionCount() {
        this.optionCount++;
    }

}
