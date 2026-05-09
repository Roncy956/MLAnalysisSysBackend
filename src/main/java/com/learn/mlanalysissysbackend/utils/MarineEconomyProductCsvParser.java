package com.learn.mlanalysissysbackend.utils;

import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import com.opencsv.CSVReader;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.learn.mlanalysissysbackend.utils.MyParseSafely.*;

public class MarineEconomyProductCsvParser {
    public static List<MarineEconomyProduct> parseCsvToEntities(MultipartFile file) throws Exception {
        List<MarineEconomyProduct> list = new ArrayList<>();

        try (Reader reader = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            CSVReader csvReader = new CSVReader(reader);
            String[] headers = csvReader.readNext(); // 读取表头
            if (headers != null && headers.length > 0) {
                // 去除第一个元素可能的 BOM 头
                headers[0] = headers[0].replace("\uFEFF", "");
            }
            if (headers == null) {
                throw new IllegalArgumentException("CSV文件无表头");
            }

            Map<String, Integer> colIndex = new HashMap<>();
            for (int i = 0; i < headers.length; i++) {
                colIndex.put(headers[i].trim(), i);
            }

            // 必要字段检查
            String[] required = {
                    "location", "year", "product", "value", "diversity", "ubiquity", "mcp",
                    "eci", "pci", "density", "coi", "cog", "rca", "product_name", "type",
                    "color", "total_value_by_year", "total_value_by_year_location",
                    "total_value_by_year_product", "total_value_by_year_type",
                    "total_value_by_year_location_type", "total_value_by_year_type_location"
            };
            for (String field : required) {
                if (!colIndex.containsKey(field)) {
                    throw new IllegalArgumentException("CSV缺少必要列：" + field);
                }
            }

            String[] line;
            int rowNum = 1;
            while ((line = csvReader.readNext()) != null) {
                rowNum++;
                if (line.length < headers.length) {
                    // log.warn("第{}行列数不足，跳过", rowNum);
                    continue;
                }
                try {
                    MarineEconomyProduct entity = new MarineEconomyProduct();
                    entity.setLocation(parseIntegerSafely(line[colIndex.get("location")]));
                    entity.setYear(parseIntegerSafely(line[colIndex.get("year")]));
                    entity.setProduct(line[colIndex.get("product")].trim());
                    entity.setValue(parseLongSafely(line[colIndex.get("value")]));
                    entity.setDiversity(parseIntegerSafely(line[colIndex.get("diversity")]));
                    entity.setUbiquity(parseIntegerSafely(line[colIndex.get("ubiquity")]));
                    entity.setMcp(parseIntegerSafely(line[colIndex.get("mcp")]) == 1);
                    entity.setEci(parseDoubleSafely(line[colIndex.get("eci")]));
                    entity.setPci(parseDoubleSafely(line[colIndex.get("pci")]));
                    entity.setDensity(parseDoubleSafely(line[colIndex.get("density")]));
                    entity.setCoi(parseDoubleSafely(line[colIndex.get("coi")]));
                    entity.setCog(parseDoubleSafely(line[colIndex.get("cog")]));
                    entity.setRca(parseDoubleSafely(line[colIndex.get("rca")]));
                    entity.setProductName(line[colIndex.get("product_name")].trim());
                    entity.setType(line[colIndex.get("type")].trim());
                    entity.setColor(line[colIndex.get("color")].trim());
                    entity.setTotalValueByYear(parseLongSafely(line[colIndex.get("total_value_by_year")]));
                    entity.setTotalValueByYearLocation(parseLongSafely(line[colIndex.get("total_value_by_year_location")]));
                    entity.setTotalValueByYearProduct(parseLongSafely(line[colIndex.get("total_value_by_year_product")]));
                    entity.setTotalValueByYearType(parseLongSafely(line[colIndex.get("total_value_by_year_type")]));
                    entity.setTotalValueByYearLocationType(parseLongSafely(line[colIndex.get("total_value_by_year_location_type")]));
                    entity.setTotalValueByYearTypeLocation(parseLongSafely(line[colIndex.get("total_value_by_year_type_location")]));

                    if (entity.getYear() < 2000 || entity.getYear() > 2023) {
                        // log.warn("第{}行年份{}超出范围，跳过", rowNum, entity.getYear());
                        continue;
                    }
                    list.add(entity);
                } catch (Exception e) {
                    throw new IllegalArgumentException("第" + rowNum + "行数据格式错误: " + e.getMessage());
                }
            }
        }
        return list;
    }
}
