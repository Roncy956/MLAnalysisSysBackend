package com.learn.mlanalysissysbackend.pojo.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FeatureImportance {
    private String feature;
    private Double importance;
}