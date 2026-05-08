package com.learn.mlanalysissysbackend.service;

import org.springframework.web.multipart.MultipartFile;

public interface AdminService {
    void importCsv(MultipartFile file) throws Exception;
}
