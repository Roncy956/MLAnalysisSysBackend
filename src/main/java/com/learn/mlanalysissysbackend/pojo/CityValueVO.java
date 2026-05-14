// --- 2. VO: 每年城市数据项 ---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/pojo/vo/CityValueVO.java
package com.learn.mlanalysissysbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CityValueVO {
    private String name;   // 城市/省份名称
    private Long value;    // 该年该地区的总出口额
}