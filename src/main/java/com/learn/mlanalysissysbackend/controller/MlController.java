package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.pojo.YearlyCityExportVO;
import com.learn.mlanalysissysbackend.service.MLResult;
import com.learn.mlanalysissysbackend.service.MarineEconomyService;
import com.learn.mlanalysissysbackend.service.PythonScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/ml")
public class MlController {

    @Autowired
    private PythonScriptService pythonScriptService;
    @Autowired
    private MarineEconomyService marineEconomyService;
    @Autowired
    private MLResult mlResult;

    @PostMapping("/train/random-forest")
    public Map<String, Object> trainRandomForest() throws Exception {
        return pythonScriptService.trainRandomForestModel();
    }

    @PostMapping("/train/xgboost")
    public Map<String, Object> trainXGBoost() throws Exception {
        return pythonScriptService.trainXGBoostModel();
    }

    @PostMapping("/train/light-gbm")
    public Map<String, Object> trainLightGBM() throws Exception {
        return pythonScriptService.trainLightGBMModel();
    }

    @PostMapping("/train/cat-boost")
    public Map<String, Object> trainCatBoost() throws Exception {
        return pythonScriptService.trainCatBoostModel();
    }

    @GetMapping("/result/random-forest")
    public Result getRandomForestData() {
        // 调用 Service 读取数据
//        return mlResult.readForestData();
        return Result.success(mlResult.readRandomForestData().getFirst());
    }

    @GetMapping("/result/xgboost")
    public Result getXGBoostData() {
        // 调用 Service 读取数据
//        return mlResult.readForestData();
        return Result.success(mlResult.readXGBoostData().getFirst());
    }

    @GetMapping("/result/light-gbm")
    public Result getLightGBMData() {
        // 调用 Service 读取数据
//        return mlResult.readForestData();
        return Result.success(mlResult.readLightGBMData().getFirst());
    }

    @GetMapping("/result/cat-boost")
    public Result getCatBoostData() {
        // 调用 Service 读取数据
//        return mlResult.readForestData();
        return Result.success(mlResult.readCatBoostData().getFirst());
    }

    @GetMapping("/yearly-city-export")
    public Result getYearlyCityExport() {
        return Result.success(marineEconomyService.getYearlyCityExport());
    }
}