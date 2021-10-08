package com.example.demo.service;


import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Attachments;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Base64;

@Service
public class SendGridService {

    public void sendMail(byte[] fileBytes, String fileName) throws IOException {

        Email from = new Email(System.getenv("From"));
        String subject = "ファイル化完了";
        Email to = new Email(System.getenv("To"));
        Content content = new Content("text/plain", "ファイルが作成されました。");
        Attachments attachment = new Attachments();
        attachment.setContent(Base64.getEncoder().encodeToString(fileBytes));
        attachment.setFilename(fileName);
        attachment.setType("zip");
        attachment.setDisposition("attachment");
        Mail mail = new Mail(from, subject, to, content);
        mail.addAttachments(attachment);
        SendGrid sg = new SendGrid(System.getenv("SENDGRID_API_KEY"));
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);
            System.out.println(response.getStatusCode());
            System.out.println(response.getBody());
            System.out.println(response.getHeaders());
        } catch (IOException ex) {
            throw ex;
        }
    }
}
