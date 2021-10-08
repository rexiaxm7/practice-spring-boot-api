package com.example.demo.service;

import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ZipFileCreationService {

    private final SendGridService sendGridService;

    public ZipFileCreationService(SendGridService sendGridService) {
        this.sendGridService = sendGridService;
    }

    public void createZipFile(String filename, byte[] input) throws IOException {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ZipOutputStream zos = new ZipOutputStream(baos);
        ZipEntry entry = new ZipEntry(filename);
        entry.setSize(input.length);
        zos.putNextEntry(entry);
        zos.write(input);
        zos.closeEntry();
        zos.close();
        int index1 = filename.indexOf(".");
        String zipFileName = filename.substring(0, index1) + ".zip";
        sendGridService.sendMail(baos.toByteArray(),zipFileName);
    }
}
