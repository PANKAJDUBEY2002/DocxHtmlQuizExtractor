package com.example.demo.controller;


import com.example.demo.dto.CombinedData;
import com.example.demo.service.DocxService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.zwobble.mammoth.DocumentConverter;
import org.zwobble.mammoth.Result;

import java.io.File;
import java.io.IOException;
import java.util.Set;

@RestController
public class DocxConverter {

    @Autowired
    DocxService docxService;

    @PostMapping("/convert-doc-to-html")
    public CombinedData docToHtml(@RequestParam("file") MultipartFile file) {
        DocumentConverter converter = new DocumentConverter();

        String html = "";

        try {
            // Convert MultipartFile to File for processing
            File tempFile = docxService.convertMultiPartToFile(file);
            Result<String> result = converter.convertToHtml(tempFile);
            html = result.getValue();
            Set<String> warnings = result.getWarnings();
            // Clean up the temporary file
            tempFile.delete();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return docxService.extractQuestions(html);

    }



}