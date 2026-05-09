// service/impl/MarineEconomyAnalysisServiceImpl.java
package com.learn.mlanalysissysbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.mlanalysissysbackend.pojo.ml.*;
import com.learn.mlanalysissysbackend.mapper.MarineEconomyProductMapper;
import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import com.learn.mlanalysissysbackend.service.MarineEconomyAnalysisService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class MarineEconomyAnalysisServiceImpl implements MarineEconomyAnalysisService {

    @Autowired
    private MarineEconomyProductMapper mapper; // 假设 Mapper 已有 selectAll 方法

    @Value("${python.script.path:forest.py}") // 可配置脚本路径，默认项目根目录
    private String pythonScriptPath;

    @Value("${python.command:python}") // Python 解释器，可按需改为 python3
    private String pythonCommand;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 训练模型（从数据库读取数据）
    @Override
    public TrainingResult trainModel() {
        List<MarineEconomyProduct> productList = mapper.selectAll(); // 需实现
        if (productList == null || productList.isEmpty()) {
            throw new RuntimeException("没有可用数据进行训练");
        }
        List<Map<String, Object>> dataList = convertToPythonFormat(productList);
        return executePythonCommand("train", dataList, null);
    }

    // 使用已训练模型预测单条数据（需先执行过训练）
    @Override
    public Double predictSingle(Map<String, Object> inputFeatures) {
        return executePythonCommand("predict", null, inputFeatures);
    }

    // 训练并预测（适用于需要即时预测的场景）
    @Override
    public TrainingResult trainAndPredict(Map<String, Object> inputFeatures) {
        List<MarineEconomyProduct> productList = mapper.selectAll();
        if (productList == null || productList.isEmpty()) {
            throw new RuntimeException("没有可用数据进行训练");
        }
        List<Map<String, Object>> dataList = convertToPythonFormat(productList);
        return executePythonCommand("load_and_predict", dataList, inputFeatures);
    }

    // ---------- 私有辅助方法 ----------

    /**
     * 将实体列表转换为 Python 脚本期望的字段名 Map 列表
     */
    private List<Map<String, Object>> convertToPythonFormat(List<MarineEconomyProduct> list) {
        List<Map<String, Object>> result = new ArrayList<>();
        for (MarineEconomyProduct p : list) {
            Map<String, Object> map = new HashMap<>();
            map.put("location", p.getLocation());
            map.put("product", p.getProduct());
            map.put("year", p.getYear());
            map.put("diversity", p.getDiversity());
            map.put("ubiquity", p.getUbiquity());
            map.put("mcp", p.getMcp());          // Boolean -> JSON true/false
            map.put("eci", p.getEci());
            map.put("pci", p.getPci());
            map.put("density", p.getDensity());
            map.put("coi", p.getCoi());
            map.put("cog", p.getCog());
            map.put("rca", p.getRca());
            map.put("product_name", p.getProductName());
            map.put("type", p.getType());
            map.put("color", p.getColor());
            map.put("total_value_by_year", p.getTotalValueByYear());
            map.put("total_value_by_year_location", p.getTotalValueByYearLocation());
            map.put("total_value_by_year_product", p.getTotalValueByYearProduct());
            map.put("total_value_by_year_type", p.getTotalValueByYearType());
            map.put("total_value_by_year_location_type", p.getTotalValueByYearLocationType());
            // 目标变量 value
            map.put("value", p.getValue());
            result.add(map);
        }
        return result;
    }

    /**
     * 通用 Python 脚本执行方法
     * @param command  train / predict / load_and_predict
     * @param dataList 训练数据（train/load_and_predict 需要，predict 可为 null）
     * @param inputSample 预测样本（predict/load_and_predict 需要，train 可为 null）
     * @return 根据命令返回不同结果（TrainingResultDTO 或 Double）
     */
    @SuppressWarnings("unchecked")
    private <T> T executePythonCommand(String command, List<Map<String, Object>> dataList, Map<String, Object> inputSample) {
        ProcessBuilder processBuilder = new ProcessBuilder(pythonCommand, pythonScriptPath, command);
        processBuilder.redirectErrorStream(true); // 合并错误流
        Process process = null;
        try {
            process = processBuilder.start();

            // 构造输入 JSON
            Map<String, Object> inputJson = new HashMap<>();
            if (dataList != null) {
                inputJson.put("data", dataList);
            }
            if (inputSample != null) {
                inputJson.put("input", inputSample);
            }
            String jsonInput = objectMapper.writeValueAsString(inputJson);

            // 写入 stdin
            try (OutputStream os = process.getOutputStream();
                 BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os))) {
                writer.write(jsonInput);
                writer.flush();
            }

            // 读取 stdout
            StringBuilder output = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line);
                }
            }

            boolean finished = process.waitFor(60, TimeUnit.SECONDS);
            if (!finished) {
                process.destroyForcibly();
                throw new RuntimeException("Python 脚本执行超时（60秒）");
            }
            int exitCode = process.exitValue();
            String resultJson = output.toString();
            if (exitCode != 0) {
                log.error("Python 脚本执行失败，退出码: {}, 输出: {}", exitCode, resultJson);
                throw new RuntimeException("Python 脚本执行失败: " + resultJson);
            }

            // 解析结果
            if ("predict".equals(command)) {
                Map<String, Object> resultMap = objectMapper.readValue(resultJson, Map.class);
                Double prediction = ((Number) resultMap.get("prediction")).doubleValue();
                return (T) prediction;
            } else {
                return (T) objectMapper.readValue(resultJson, TrainingResult.class);
            }
        } catch (IOException | InterruptedException e) {
            log.error("调用 Python 脚本异常", e);
            throw new RuntimeException("调用 Python 脚本失败: " + e.getMessage(), e);
        } finally {
            if (process != null) {
                process.destroyForcibly();
            }
        }
    }
}