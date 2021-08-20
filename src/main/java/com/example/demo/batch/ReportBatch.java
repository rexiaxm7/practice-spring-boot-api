package com.example.demo.batch;

import com.example.demo.service.FileCreationService;
import com.example.demo.service.MessageSendingService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ReportBatch {

    private final MessageSendingService messageSendingService;
    private final FileCreationService fileCreationService;

    public ReportBatch(MessageSendingService messageSendingService, FileCreationService fileCreationService) {
        this.messageSendingService = messageSendingService;
        this.fileCreationService = fileCreationService;
    }

    @Scheduled(cron = "${batch.cron}", zone = "${batch.timezone}")
    public void batchMain() throws IOException {
        messageSendingService.sendInputMessages();
        messageSendingService.sendWarningMessages();
        fileCreationService.createFiles();
        fileCreationService.createFilesTemporarily();
    }


}
