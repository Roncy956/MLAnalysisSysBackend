package com.learn.mlanalysissysbackend.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TableQueryParam {
    private Integer page;
    private Integer pageSize;
}
