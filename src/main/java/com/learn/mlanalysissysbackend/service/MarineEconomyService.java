// --- 6. Service 接口 ---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/service/MarineEconomyService.java
package com.learn.mlanalysissysbackend.service;

import com.learn.mlanalysissysbackend.pojo.TableQueryParam;
import com.learn.mlanalysissysbackend.pojo.YearlyCityExportVO;
import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;

import java.util.List;

public interface MarineEconomyService {
    List<YearlyCityExportVO> getYearlyCityExport();

    List<MarineEconomyProduct> getMarineEconomyProductData(TableQueryParam tableQueryParam);

    Integer getTableLen();
}