package com.example.demo.dto;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CombinedData {

    private List<QuestionData> questionDataList;
    private List<QuestionHtmlData> questionHtmlDataList;
    private List<Warning> warnings;
}
