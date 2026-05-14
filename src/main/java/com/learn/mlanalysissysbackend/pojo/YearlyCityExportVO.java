// --- 3. VO: 按年分组的最终响应结构 ---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/pojo/vo/YearlyCityExportVO.java
package com.learn.mlanalysissysbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class YearlyCityExportVO {
    private Integer year;               // 年份
    private List<CityValueVO> data;     // 该年各城市出口数据
}