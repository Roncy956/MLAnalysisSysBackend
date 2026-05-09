package com.learn.mlanalysissysbackend.pojo.ml;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class TrainingMetrics {
    private Double trainR2;
    private Double testR2;
    private Double trainMse;
    private Double testMse;
    private Double testMae;
    private Double testRmse;
}