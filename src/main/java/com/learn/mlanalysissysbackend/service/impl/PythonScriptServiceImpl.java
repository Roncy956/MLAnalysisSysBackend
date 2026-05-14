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
    @Value("${python.command:python}")
    private String interpreter;
    @Value("${python.script.path:forest.py}")
    private String scriptPath;


    public Map<String, Object> trainModel() throws IOException, InterruptedException {
        // --- 1. 原有的数据处理和脚本执行逻辑 ---
        // 获取所有数据并且转换为 Python 脚本所需的格式
        List<MarineEconomyProduct> productList = mapper.selectAll();
        List<Map<String, Object>> pythonData = convertToPythonFormat(productList);
        // 转换为 JSON
        Map<String, Object> input = new HashMap<>();
        input.put("data", pythonData);
        String inputJson = objectMapper.writeValueAsString(input);
        // 执行 Python 脚本
        String result = executePythonCommand("train", inputJson, interpreter, scriptPath);
        Map<String, Object> resultMap = objectMapper.readValue(result, Map.class);

        // --- 2. 新增：将结果存入 MongoDB 的 forest 集合 ---
        // 定义集合名称
        String collectionName = "forest";
        // 2.1 如果集合存在，则删除（这会清空所有旧数据）
        if (mongoTemplate.collectionExists(collectionName)) {
            mongoTemplate.dropCollection(collectionName);
        }
        // 2.2 创建新的集合并插入数据
        // 注意：insert 操作会自动创建集合（如果不存在）
        // 假设你想把整个 resultMap 作为一个文档存进去，或者根据实际结构调整
        mongoTemplate.insert(resultMap, collectionName);
        // --- 返回结果 ---
        return resultMap;
    }
}