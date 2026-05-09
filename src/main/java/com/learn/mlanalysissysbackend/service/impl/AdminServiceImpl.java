package com.learn.mlanalysissysbackend.service.impl;

import com.learn.mlanalysissysbackend.mapper.MarineEconomyProductMapper;
import com.learn.mlanalysissysbackend.pojo.MarineEconomyProduct;
import com.learn.mlanalysissysbackend.service.AdminService;
import com.learn.mlanalysissysbackend.utils.MarineEconomyProductCsvParser;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class AdminServiceImpl implements AdminService {

    @Autowired
    private MarineEconomyProductMapper mapper;

    // 导入CSV文件
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void importCsv(MultipartFile file) throws Exception {
        // 检测是否为空和文件格式
        if (file.isEmpty()) {
            throw new IllegalArgumentException("文件为空");
        }
        if (!file.getOriginalFilename().endsWith(".csv")) {
            throw new IllegalArgumentException("请上传CSV文件");
        }
        // 解析CSV文件
        List<MarineEconomyProduct> list = MarineEconomyProductCsvParser.parseCsvToEntities(file);
        if (list.isEmpty()) {
            throw new IllegalArgumentException("CSV文件中没有有效数据");
        }
        // 批量插入
        int batchSize = 500;
        for (int i = 0; i < list.size(); i += batchSize) {
            int end = Math.min(i + batchSize, list.size());
            mapper.batchInsert(list.subList(i, end));
        }
    }
}
