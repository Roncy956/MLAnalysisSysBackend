package com.learn.mlanalysissysbackend.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.learn.mlanalysissysbackend.mapper.MarineEconomyProductMapper;
import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import com.learn.mlanalysissysbackend.service.PythonScriptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learn.mlanalysissysbackend.utils.ml.Forest.*;

@Service
public class PythonScriptServiceImpl implements PythonScriptService {

    @Autowired
    private MarineEconomyProductMapper mapper;
    @Autowired
    private MongoTemplate mongoTemplate;

    private final ObjectMapper objectMapper = new ObjectMapper();

    // 注入配置值
    @Value("${python.command}")
    private String interpreter;
    @Value("${python.script.path}")
    private String scriptPath;

    // 随机森林
    public Map<String, Object> trainRandomForestModel() throws IOException, InterruptedException {
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 选择随机森林脚本
        String path = scriptPath + "RandomForest.py";
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, path);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        // 定义集合名称
        String collectionName = "RandomForest";
        // 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 创建新的集合并插入数据
        mongoTemplate.insert(resultMap, collectionName);
        // 返回结果
        return resultMap;
    }

    @Override
    public Map<String, Object> trainXGBoostModel() throws IOException, InterruptedException {
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 选择随机森林脚本
        String path = scriptPath + "XGBoost.py";
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, path);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        // 定义集合名称
        String collectionName = "XGBoost";
        // 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 创建新的集合并插入数据
        mongoTemplate.insert(resultMap, collectionName);
        // 返回结果
        return resultMap;
    }


    @Override
    public Map<String, Object> trainLightGBMModel() throws IOException, InterruptedException {
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 选择随机森林脚本
        String path = scriptPath + "lightGBM.py";
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, path);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        // 定义集合名称
        String collectionName = "LightGBM";
        // 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 创建新的集合并插入数据
        mongoTemplate.insert(resultMap, collectionName);
        // 返回结果
        return resultMap;
    }

    @Override
    public Map<String, Object> trainCatBoostModel() throws IOException, InterruptedException {
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 选择随机森林脚本
        String path = scriptPath + "CatBoost.py";
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, path);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        // 定义集合名称
        String collectionName = "CatBoost";
        // 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 创建新的集合并插入数据
        mongoTemplate.insert(resultMap, collectionName);
        // 返回结果
        return resultMap;
    }

    @Override
    public Map<String, Object> trainEnsembleModel() throws IOException, InterruptedException {
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 选择随机森林脚本
        String path = scriptPath + "Ensemble.py";
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, path);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);
        // 定义集合名称
        String collectionName = "Ensemble";
        // 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 创建新的集合并插入数据
        mongoTemplate.insert(resultMap, collectionName);
        // 返回结果
        return resultMap;
    }
}