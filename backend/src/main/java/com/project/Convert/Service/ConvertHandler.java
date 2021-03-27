package com.project.Convert.Service;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.tool.xml.XMLWorkerHelper;
import com.project.Convert.Controller.ConvertController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.pdfbox.cos.COSDocument;
import org.apache.pdfbox.io.RandomAccessFile;

import org.apache.pdfbox.pdfparser.PDFParser;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;
import org.fit.pdfdom.PDFDomTree;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.xml.parsers.ParserConfigurationException;
import java.awt.image.BufferedImage;
import java.io.*;


@Service
public class ConvertHandler implements IConvertHandler {
    @Value("${input.path}")
    private String inputPath;

    private static final Logger logger = LogManager.getLogger(ConvertController.class);

    public void fileToPDFConverter(String from, MultipartFile file,String fileName) {
        fileName = fileName.split("\\.")[0];
        switch(from){
            case "HTML":convertFromHTMLtoPDF(file,fileName);
        break;
            case "Image":convertFromIMGtoPDF(file,fileName);
            break;
            case "Text": convertFromTXTtoPDF(file,fileName);

        }

    }


    public void fileFromPDFConverter(String to, MultipartFile file,String fileName) {
        fileName = fileName.split("\\.")[0];
        switch (to) {
            case "HTML": generateHTMLFromPDF(file,fileName);
                break;
            case "Image":
                generateImageFromPDF(file, fileName);
                break;
            case "Text":
                generateTextFromPDF(file,fileName);
        }
    }
    private void generateHTMLFromPDF(MultipartFile file,String fileName) {
        try{
        PDDocument pdf = PDDocument.load(file.getBytes());
        Writer output = new PrintWriter("src/main/Output/"+fileName+".html", "utf-8");
        new PDFDomTree().writeText(pdf, output);

        output.close();
        logger.info("Successfully converted to HTML from PDF");
        }
        catch(ParserConfigurationException | IOException e){
            e.printStackTrace();
        }
    }

    private void generateImageFromPDF(MultipartFile file, String fileName) {
      try{  PDDocument document = PDDocument.load(file.getBytes());
        PDFRenderer pdfRenderer = new PDFRenderer(document);
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            BufferedImage bim = pdfRenderer.renderImageWithDPI(
                    page, 300, ImageType.RGB);
            ImageIOUtil.writeImage(
                    bim, String.format("src/main/Output/"+fileName+".%s", "jpg"), 300);
        }
        document.close();
          logger.info("Successfully converted to Image from PDF");
      }
      catch(IOException e){
          e.printStackTrace();
      }
    }

    private void generateTextFromPDF(MultipartFile file,String fileName){
        File f=new File(inputPath+"input.pdf");
        String parsedText;
       try{
           file.transferTo(f);
        PDFParser parser = new PDFParser(new RandomAccessFile(f, "r"));
        parser.parse();
        COSDocument cosDoc = parser.getDocument();
        PDFTextStripper pdfStripper = new PDFTextStripper();
        PDDocument pdDoc = new PDDocument(cosDoc);
        parsedText = pdfStripper.getText(pdDoc);
        PrintWriter pw = new PrintWriter("src/main/Output/"+fileName+".txt");
        pw.print(parsedText);
        pw.close();
           logger.info("Successfully converted to Text from PDF");}
       catch(IOException e){
           e.printStackTrace();
        }

    }

    private void convertFromHTMLtoPDF(MultipartFile file,String fileName) {
        Document document = new Document();
        PdfWriter writer;
        try {
            writer = PdfWriter.getInstance(document,
                    new FileOutputStream("src/main/Output/"+fileName+".pdf"));
            document.open();
            XMLWorkerHelper.getInstance().parseXHtml(writer, document,
                    new BufferedInputStream(file.getInputStream()));
            document.close();
            logger.info("Successfully converted to PDF from HTML");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
    private void convertFromIMGtoPDF(MultipartFile file,String fileName) {
        Document document = new Document();
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document,
                    new FileOutputStream("src/main/Output/"+fileName+".pdf"));
            writer.open();
            document.open();
            document.add(Image.getInstance(file.getBytes()));
            document.close();
            writer.close();
            logger.info("Successfully converted to PDF from Image");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
    private void convertFromTXTtoPDF(MultipartFile file,String fileName) {
        File file1=new File(inputPath+"input.txt");

        Document document = new Document(PageSize.A4);
        try {
            PdfWriter.getInstance(document,
                    new FileOutputStream("src/main/Output/"+fileName+".pdf"))
                    .setPdfVersion(PdfWriter.PDF_VERSION_1_7);
            file.transferTo(file1);
            BufferedReader br = new BufferedReader(new FileReader(file1));
            document.open();
            Font myfont = new Font();
            myfont.setStyle(Font.NORMAL);
            myfont.setSize(11);
            document.add(new Paragraph("\n"));

            String strLine;
            while ((strLine = br.readLine()) != null) {
                Paragraph para = new Paragraph(strLine + "\n", myfont);
                para.setAlignment(Element.ALIGN_JUSTIFIED);
                document.add(para);
            }
            document.close();
            br.close();
            logger.info("Successfully converted to PDF from Text");
        } catch (DocumentException | IOException e) {
            e.printStackTrace();
        }
    }
}