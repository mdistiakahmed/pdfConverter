package com.istiak.Controllers;

import com.convertapi.client.Config;
import com.convertapi.client.ConversionResult;
import com.convertapi.client.ConvertApi;
import com.convertapi.client.Param;
import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.commons.io.FilenameUtils;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/api")
public class FileConverterController {
    private Random rand = new Random();
    private final String TMP_FILE_PATH = "/home/ec2-user/tmpFiles/";
    Logger log = LoggerFactory.getLogger(FileConverterController.class);

    @CrossOrigin
    @GetMapping("/test")
    public ResponseEntity<String> test() {
        log.info("start test method");
        return ResponseEntity.ok("Server is running ok !! Dukkher Bepar");
    }


    /**
     * test upload using library method
     * @param file
     * @return
     */
    @CrossOrigin
    @PostMapping(
            value = "fileTest",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] uploadFile(@RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            return new byte[0];
        }
        byte[] fileContent = new byte[0];
        try {
            byte[] bytes = file.getBytes();
            // Convert to pdf
            InputStream inputStream =  new ByteArrayInputStream(bytes);
            XWPFDocument doc = new XWPFDocument(inputStream);
            PdfOptions pdfOptions = PdfOptions.create();
            String outputFilePath = "src/test/resources/" +
                    file.getOriginalFilename().replaceFirst("[.][^.]+$", "") + ".pdf";
            OutputStream out = new FileOutputStream(new File(outputFilePath));
            PdfConverter.getInstance().convert(doc, out, pdfOptions);
            doc.close();
            out.close();
            // Make output file to byte array
            fileContent = Files.readAllBytes(Paths.get(outputFilePath));
            System.out.println("Done");
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
        return fileContent;
    }

    /**
     * pdf convert using third party API call
     * @param file
     * @return
     */
    @CrossOrigin
    @PostMapping(
            value = "pdfConvert",
            produces = MediaType.APPLICATION_PDF_VALUE)
    public @ResponseBody byte[] uploadTest(@RequestPart("file") MultipartFile file) {
        if (null == file.getOriginalFilename()) {
            log.info("File name is empty");
            return new byte[0];
        }
        log.info("Entered into the converter method");
        //Get file name without extension
        String fileName = file.getOriginalFilename().replaceFirst("[.][^.]+$", "");
        String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());
        log.info("File name : "+fileName+ " , File extension : " + fileExtension);
        int randomValue = rand.nextInt();
        log.info("Random number : " + randomValue);
        if(randomValue<0)  randomValue *= -1;
        String tmpInputFilePath = TMP_FILE_PATH + randomValue + file.getOriginalFilename();
        String tmpOutputFilePath = TMP_FILE_PATH + randomValue + fileName + ".pdf";

        log.info("Input file path : " + tmpInputFilePath);
        log.info("Output file path : " + tmpOutputFilePath);

        //this.getClass().

        try {
            Path filepath = Paths.get(tmpInputFilePath);
            file.transferTo(filepath);
            log.info("File is stored in hard disk");
        }catch (Exception e){
            log.error("Can not store file", e);
        }

        try {
            log.info("Calling third party API to convert the file");
            Config.setDefaultSecret("xPLyXXbmUFSmteWY");
            CompletableFuture<ConversionResult> result = ConvertApi.convert(fileExtension, "pdf", new Param("file", Paths.get(tmpInputFilePath)));
            result.get().saveFile(Paths.get(tmpOutputFilePath)).get();
            log.info("Third party has converted the file successfully !");
        }catch (Exception e){
            log.error("Can not convert file " , e);
        }

        byte[] fileContent = new byte[0];
        try {
            fileContent = Files.readAllBytes(Paths.get(tmpOutputFilePath));
        }catch (Exception e){
            log.error("Can not retrieve file ", e);
        }
        log.info("Deleting file from Hard dist");
        File tmpFile = new File(tmpInputFilePath);
        tmpFile.delete();
        tmpFile = new File(tmpOutputFilePath);
        tmpFile.delete();
        log.info("Deleted file from hard disk");

        log.info("Finished +++");


        return fileContent;

    }


}
