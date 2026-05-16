// --- 7. Service 实现类（核心业务逻辑）---
// 文件路径: src/main/java/com/learn/mlanalysissysbackend/service/impl/MarineEconomyServiceImpl.java
package com.learn.mlanalysissysbackend.service.impl;

import com.github.pagehelper.PageHelper;
import com.learn.mlanalysissysbackend.mapper.MarineEconomyProductMapper;
import com.learn.mlanalysissysbackend.pojo.TableQueryParam;
import com.learn.mlanalysissysbackend.pojo.LocationYearTotalDTO;
import com.learn.mlanalysissysbackend.pojo.CityValueVO;
import com.learn.mlanalysissysbackend.pojo.YearlyCityExportVO;
import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import com.learn.mlanalysissysbackend.service.MarineEconomyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MarineEconomyServiceImpl implements MarineEconomyService {

    // 地区代码 -> 城市/省份名称 映射表（根据文档）
    private static final Map<Integer, String> LOCATION_MAP = new HashMap<>();

    static {
        LOCATION_MAP.put(12, "天津市");
        LOCATION_MAP.put(13, "河北省");
        LOCATION_MAP.put(21, "辽宁省");
        LOCATION_MAP.put(31, "上海市");
        LOCATION_MAP.put(32, "江苏省");
        LOCATION_MAP.put(33, "浙江省");
        LOCATION_MAP.put(35, "福建省");
        LOCATION_MAP.put(37, "山东省");
        LOCATION_MAP.put(44, "广东省");
        LOCATION_MAP.put(45, "广西壮族自治区");
        LOCATION_MAP.put(46, "海南省");
    }

    @Autowired
    private MarineEconomyProductMapper marineEconomyProductMapper;

    @Override
    public List<YearlyCityExportVO> getYearlyCityExport() {
        // 1. 查询数据库：每个地区每年的总出口额
        List<LocationYearTotalDTO> dbList = marineEconomyProductMapper.selectDistinctLocationYearTotal();

        // 2. 按年份分组，每组内构建 CityValueVO 列表
        Map<Integer, List<CityValueVO>> yearMap = new TreeMap<>(); // TreeMap保证年份有序
        for (LocationYearTotalDTO item : dbList) {
            Integer year = item.getYear();
            Integer locationCode = item.getLocation();
            Long totalValue = item.getTotalValue();

            // 将地区代码转换为中文名称，若找不到映射则使用代码本身（防御性编程）
            String cityName = LOCATION_MAP.getOrDefault(locationCode, String.valueOf(locationCode));

            CityValueVO cityValue = new CityValueVO(cityName, totalValue);
            yearMap.computeIfAbsent(year, k -> new ArrayList<>()).add(cityValue);
        }

        // 3. 转换为最终的 VO 列表
        List<YearlyCityExportVO> result = new ArrayList<>();
        for (Map.Entry<Integer, List<CityValueVO>> entry : yearMap.entrySet()) {
            // 每年内的城市顺序按照数据库返回的 location 顺序（已在SQL中按location ASC排序）
            // 如果需要自定义顺序（如按映射表顺序），可在此处对 entry.getValue() 重新排序
            result.add(new YearlyCityExportVO(entry.getKey(), entry.getValue()));
        }
        return result;
    }

    @Override
    public List<MarineEconomyProduct> getMarineEconomyProductData(TableQueryParam tableQueryParam) {
        // 分页
        PageHelper.startPage(tableQueryParam.getPage(), tableQueryParam.getPageSize());
        // 查询数据
        List<MarineEconomyProduct> fileList = marineEconomyProductMapper.selectPageData();
        return fileList;
    }

    @Override
    public Integer getTableLen() {
        return marineEconomyProductMapper.getTableLen();
    }
}