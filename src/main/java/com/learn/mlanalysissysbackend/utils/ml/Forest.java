package com.learn.mlanalysissysbackend.utils.ml;

import com.learn.mlanalysissysbackend.pojo.ml.MarineEconomyProduct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component // 1. 依然需要注册为 Bean，以便 Spring 能扫描到
public class Forest {
    /**
     * 将 MarineEconomyProduct 实体转换为 Python 脚本期望的 Map（下划线命名）
     */
    public static List<Map<String, Object>> convertToPythonFormat(List<MarineEconomyProduct> products) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (MarineEconomyProduct p : products) {
            Map<String, Object> map = new HashMap<>();
            map.put("location", p.getLocation());
            map.put("product", p.getProduct());
            map.put("value", p.getValue());
            map.put("year", p.getYear());
            map.put("diversity", p.getDiversity());
            map.put("ubiquity", p.getUbiquity());
            map.put("mcp", p.getMcp());
            map.put("eci", p.getEci());
            map.put("pci", p.getPci());
            map.put("density", p.getDensity());
            map.put("coi", p.getCoi());
            map.put("cog", p.getCog());
            map.put("rca", p.getRca());
            // 关键：驼峰转下划线
            map.put("product_name", p.getProductName());
            map.put("type", p.getType());
            map.put("color", p.getColor() != null ? p.getColor() : "Unknown");
            map.put("total_value_by_year", p.getTotalValueByYear());
            map.put("total_value_by_year_location", p.getTotalValueByYearLocation());
            map.put("total_value_by_year_product", p.getTotalValueByYearProduct());
            map.put("total_value_by_year_type", p.getTotalValueByYearType());
            map.put("total_value_by_year_location_type", p.getTotalValueByYearLocationType());
            // totalValueByYearTypeLocation 未被 Python 使用，可忽略
            list.add(map);
        }
        return list;
    }

    public static String executePythonCommand(String command, String jsonInput,
                                              String pythonInterpreter, String scriptPath) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(pythonInterpreter, scriptPath, command);
        // 将 Python 脚本的输出重定向到标准输出
        pb.redirectErrorStream(true);
        pb.environment().put("PYTHONIOENCODING", "utf-8");
        // 启动 Python 脚本
        Process process = pb.start();
        // 将输入写入 Python 脚本
        try (OutputStream os = process.getOutputStream();
             BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "utf-8"))) {
            writer.write(jsonInput);
            writer.flush();
        }
        // 读取 Python 脚本的输出
        StringBuilder output = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), "utf-8"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line);
            }
        }
        // 等待 Python 脚本执行完成
        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new RuntimeException("Python 脚本执行失败，退出码：" + exitCode + "，输出：" + output);
        }
        // 返回 Python 脚本的输出
        return output.toString();
    }
}
