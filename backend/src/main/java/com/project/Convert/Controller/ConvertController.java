package com.project.Convert.Controller;

import com.project.Convert.Service.IConvertHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
public class ConvertController {
    @Autowired
    IConvertHandler handler;
    private static final Logger logger = LogManager.getLogger(ConvertController.class);

    @PostMapping("/convertTofile/{from}/{fileName}")
    public void convertTofile(@PathVariable("from") String from, @PathVariable("fileName") String fileName,@RequestParam("file") MultipartFile file){
        logger.debug(String.format("The values path params are from = %s and file name = %s",from,fileName));
        handler.fileToPDFConverter(from, file, fileName);
    }
    @PostMapping("/convertPdffile/{to}/{fileName}")
    public void convertPDFFile(@PathVariable("to") String to, @PathVariable("fileName") String fileName,@RequestParam("file") MultipartFile file){
        logger.debug(String.format("The values path params are to = %s and file name = %s",to,fileName));
        handler.fileFromPDFConverter(to, file, fileName);
    }
}
