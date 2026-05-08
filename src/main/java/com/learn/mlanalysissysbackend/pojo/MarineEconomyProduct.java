package com.learn.mlanalysissysbackend.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MarineEconomyProduct {
    private Long id;
    private Integer locationCode;
    private String productCode;
    private Long exportValue;
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
    private String marineType;
    private String color;
    private Long totalValueByYear;
    private Long totalValueByYearLocation;
    private Long totalValueByYearProduct;
    private Long totalValueByYearType;
    private Long totalValueByYearLocationType;
    private Long totalValueByYearTypeLocation;
}