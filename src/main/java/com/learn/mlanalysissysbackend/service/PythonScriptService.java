// service/MarineEconomyAnalysisService.java
package com.learn.mlanalysissysbackend.service;

import java.io.IOException;
import java.util.Map;

public interface PythonScriptService {
    Map<String, Object> trainModel() throws IOException, InterruptedException;
}