package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.ml.TrainingResult;
import com.learn.mlanalysissysbackend.service.MarineEconomyAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/analysis")
public class AnalysisController {
    @Autowired
    private MarineEconomyAnalysisService analysisService;

    @PostMapping("/train")
    public TrainingResult train() {
        return analysisService.trainModel();
    }

    @PostMapping("/predict")
    public Double predict(@RequestBody Map<String, Object> features) {
        return analysisService.predictSingle(features);
    }
}