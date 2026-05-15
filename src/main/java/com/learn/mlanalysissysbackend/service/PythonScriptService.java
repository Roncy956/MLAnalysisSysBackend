// service/MarineEconomyAnalysisService.java
package com.learn.mlanalysissysbackend.service;

import java.io.IOException;
import java.util.Map;

public interface PythonScriptService {
    Map<String, Object> trainRandomForestModel() throws IOException, InterruptedException;

    Map<String, Object> trainXGBoostModel() throws IOException, InterruptedException;

    Map<String, Object> trainLightGBMModel() throws IOException, InterruptedException;

    Map<String, Object> trainCatBoostModel() throws IOException, InterruptedException;
}