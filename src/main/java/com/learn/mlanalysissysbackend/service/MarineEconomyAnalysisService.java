// service/MarineEconomyAnalysisService.java
package com.learn.mlanalysissysbackend.service;

import com.learn.mlanalysissysbackend.pojo.ml.TrainingResult;

import java.util.Map;

public interface MarineEconomyAnalysisService {
    TrainingResult trainModel();
    Double predictSingle(Map<String, Object> inputFeatures);
    TrainingResult trainAndPredict(Map<String, Object> inputFeatures);
}