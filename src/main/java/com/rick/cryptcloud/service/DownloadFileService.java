package com.rick.cryptcloud.service;

import com.rick.cryptcloud.VO.ResultVO;

public interface DownloadFileService {

    ResultVO<String> downloadFile(String username, String filename, String privatekey);
    
}
