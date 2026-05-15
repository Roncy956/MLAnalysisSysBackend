package com.learn.mlanalysissysbackend.service.impl;

import com.learn.mlanalysissysbackend.service.MLResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Service
public class MLResultImpl implements MLResult {

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public List<Map> readRandomForestData() {
        String collectionName = "RandomForest";
        // 2. 查询集合中的所有文档
        // MongoTemplate 会自动将 BSON 转换为 Java 的 Map/List 结构
        return mongoTemplate.findAll(Map.class, collectionName);
    }

    @Override
    public List<Map> readXGBoostData() {
        String collectionName = "XGBoost";
        // 2. 查询集合中的所有文档
        // MongoTemplate 会自动将 BSON 转换为 Java 的 Map/List 结构
        return mongoTemplate.findAll(Map.class, collectionName);
    }

    @Override
    public List<Map> readLightGBMData() {
        String collectionName = "LightGBM";
        // 2. 查询集合中的所有文档
        // MongoTemplate 会自动将 BSON 转换为 Java 的 Map/List 结构
        return mongoTemplate.findAll(Map.class, collectionName);
    }

    @Override
    public List<Map> readCatBoostData() {
        String collectionName = "CatBoost";
        // 2. 查询集合中的所有文档
        // MongoTemplate 会自动将 BSON 转换为 Java 的 Map/List 结构
        return mongoTemplate.findAll(Map.class, collectionName);
    }
}
