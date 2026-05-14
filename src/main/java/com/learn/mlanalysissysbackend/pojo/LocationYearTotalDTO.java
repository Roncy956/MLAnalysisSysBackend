// --- 1. DTO: 用于封装数据库查询结果 ---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/pojo/dto/LocationYearTotalDTO.java
package com.learn.mlanalysissysbackend.pojo;

import lombok.Data;

@Data
public class LocationYearTotalDTO {
    private Integer location;   // 地区代码
    private Integer year;       // 年份
    private Long totalValue;    // 地区年度总出口额
}