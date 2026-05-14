// --- 6. Service 接口 ---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/service/MarineEconomyService.java
package com.learn.mlanalysissysbackend.service;

import com.learn.mlanalysissysbackend.pojo.YearlyCityExportVO;
import java.util.List;

public interface MarineEconomyService {
    List<YearlyCityExportVO> getYearlyCityExport();
}