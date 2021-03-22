package com.rick.cryptcloud.service;

import com.rick.cryptcloud.DTO.FileContentDTO;

public interface DownloadFileService {

    FileContentDTO downloadFile(String username, String filename, String privatekey);
    
}
