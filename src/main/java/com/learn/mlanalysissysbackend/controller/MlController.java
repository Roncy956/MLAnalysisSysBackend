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

    @PostMapping("/train")
    public Map<String, Object> train() throws Exception {
        return pythonScriptService.trainModel();
    }

    @GetMapping("/result/forest")
    public Result getForestData() {
        // 调用 Service 读取数据
//        return mlResult.readForestData();
        return Result.success(mlResult.readForestData().getFirst());
    }

    @GetMapping("/yearly-city-export")
    public Result getYearlyCityExport() {
        return Result.success(marineEconomyService.getYearlyCityExport());
    }
}