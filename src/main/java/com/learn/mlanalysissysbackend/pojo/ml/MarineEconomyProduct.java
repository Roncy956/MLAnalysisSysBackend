package com.learn.mlanalysissysbackend.pojo.ml;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarineEconomyProduct {
    private Long id;
    private Integer location;
    private String product;
    private Long value;
    private Integer year;
    private Integer diversity;
    private Integer ubiquity;
    private Boolean mcp;
    private Double eci;
    private Double pci;
    private Double density;
    private Double coi;
    private Double cog;
    private Double rca;
    private String productName;
    private String type;
    private String color;
    private Long totalValueByYear;
    private Long totalValueByYearLocation;
    private Long totalValueByYearProduct;
    private Long totalValueByYearType;
    private Long totalValueByYearLocationType;
    private Long totalValueByYearTypeLocation;
}