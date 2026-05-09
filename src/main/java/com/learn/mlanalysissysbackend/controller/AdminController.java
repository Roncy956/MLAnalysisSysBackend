package com.learn.mlanalysissysbackend.controller;

import com.learn.mlanalysissysbackend.pojo.Result;
import com.learn.mlanalysissysbackend.service.AdminService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/admin")
public class AdminController {
    private static final Logger log = LoggerFactory.getLogger(AdminController.class);
    @Autowired
    AdminService adminService;


    @PostMapping("marine-data/import/csv")
    public Result postCsvData(@RequestParam("file") MultipartFile file) {

        try {
            adminService.importCsv(file);
            return Result.success();
        } catch (IllegalArgumentException e) {
            return Result.error(e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("上传失败");
        }
    }
}
