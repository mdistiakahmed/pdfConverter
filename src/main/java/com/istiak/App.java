package com.istiak;

import fr.opensagres.poi.xwpf.converter.pdf.PdfConverter;
import fr.opensagres.poi.xwpf.converter.pdf.PdfOptions;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.io.*;

/**
 *  Build by istiak
 *  26-06-2021
 */

@SpringBootApplication
public class App 
{
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
