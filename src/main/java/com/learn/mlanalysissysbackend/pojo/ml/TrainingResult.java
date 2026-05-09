package com.learn.mlanalysissysbackend.pojo.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingResult {
    private TrainingMetrics metrics;
    private List<FeatureImportance> featureImportance;
    private Map<String, List<Double>> plotData; // actual vs predicted, residuals
    private Map<String, Integer> modelInfo;
    private Double singlePrediction; // 仅当 trainAndPredict 时存在
}