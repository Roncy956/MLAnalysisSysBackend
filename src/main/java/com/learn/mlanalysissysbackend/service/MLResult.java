package com.learn.mlanalysissysbackend.service;

import java.util.List;
import java.util.Map;

public interface MLResult {
    public List<Map> readRandomForestData();

    public List<Map> readXGBoostData();

    public List<Map> readLightGBMData();

    public List<Map> readCatBoostData();

    public List<Map> readEnsembleData();
}
