package com.project.Convert.Service;

import org.springframework.web.multipart.MultipartFile;

public interface IConvertHandler {
    public void fileToPDFConverter(String from, MultipartFile file, String fileName);
    public void fileFromPDFConverter(String to, MultipartFile file,String fileName);
}
