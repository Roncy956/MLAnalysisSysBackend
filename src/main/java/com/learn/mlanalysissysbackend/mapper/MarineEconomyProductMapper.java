package com.learn.mlanalysissysbackend.mapper;

import com.learn.mlanalysissysbackend.pojo.LocationYearTotalDTO;
import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.ArrayList;
import java.util.List;

@Mapper
public interface MarineEconomyProductMapper {

    /**
     * 批量插入海洋经济产品数据
     * @param list 实体列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<MarineEconomyProduct> list);

    List<MarineEconomyProduct> selectAll(); // 查询所有数据

    // 查询每个地区、每年的总出口额（去重，按年份和地区代码排序）
    List<LocationYearTotalDTO> selectDistinctLocationYearTotal();
}