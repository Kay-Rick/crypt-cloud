package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DTO.UploadDTO;

public interface StoreFileService {
    
    UploadDTO storeFile(String username, String filename);
    
}
