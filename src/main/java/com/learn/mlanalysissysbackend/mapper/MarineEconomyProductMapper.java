package com.learn.mlanalysissysbackend.mapper;

import com.learn.mlanalysissysbackend.pojo.MarineEconomyProduct;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MarineEconomyProductMapper {
    
    /**
     * 批量插入海洋经济产品数据
     * @param list 实体列表
     * @return 影响行数
     */
    int batchInsert(@Param("list") List<MarineEconomyProduct> list);
    
    /**
     * 根据年份和地区删除数据（用于增量更新场景，可选）
     */
    int deleteByYearAndLocation(@Param("year") Integer year, @Param("locationCode") Integer locationCode);
}