package com.example.demo.batch;

import com.example.demo.bean.Report;
import com.example.demo.bean.Team;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Calendar;
import java.util.List;

@Component
public class ReportBatch {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;

    public ReportBatch(TeamRepository teamRepository, UserRepository userRepository, ReportRepository reportRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
    }

    public final String TEAMS_INCOMING_WEBHOOK = System.getenv("TEAMS_INCOMING_WEBHOOK");

    /**
     * Webhook データ構造
     */
    @JsonSerialize
    private class Message {
        public String title;
        public String text;
    }

    @Scheduled(cron = "0 * * * * *", zone = "Asia/Tokyo")
    public void batchMain() throws IOException {

        sendInputMessages();

        if(!createFiles()){
            sendWarningMessages();
        }
    }

    private void sendInputMessages() throws IOException {
        //日付の取得
        Calendar calendar = Calendar.getInstance();

        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {
            //本日が入力開始日か確認
            if (shouldSendInputMessage(calendar, team)) {
                //入力開始日の場合、メッセージを送信する。
                sendInputMessage();
            }
        }
    }

    private void sendInputMessage() throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "入力開始日";
        incoming.text = "月報を提出してください。";

        // 送信データを JSONテキスト化
        final ObjectMapper mapper = new ObjectMapper();
        final String incomingJson = mapper.writeValueAsString(incoming);

        // API 呼び出し
        RequestEntity<?> req = RequestEntity //
                .post(URI.create(TEAMS_INCOMING_WEBHOOK)) //
                .contentType(MediaType.APPLICATION_JSON_UTF8) //
                .body(incomingJson);
        new RestTemplate().exchange(req, String.class);
    }

    private boolean shouldSendInputMessage(Calendar calendar, Team team) {
        int today = calendar.get(Calendar.DATE);
        return team.getInput_start_date() == today;
    }

    private void sendWarningMessages() throws IOException {
        //日付の取得
        Calendar calendar = Calendar.getInstance();

        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {
            //本日が入力開始日か確認
            //全員分の月報が登録されているか確認
            if (shouldSendWarningMessage(calendar, team)) {
                //全員分の月報が未登録の場合、メッセージを通知
                sendWarningMessage();
            }
        }
    }

    private void sendWarningMessage() throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "通知開始日";
        incoming.text = "月報が提出されていません";

        // 送信データを JSONテキスト化
        final ObjectMapper mapper = new ObjectMapper();
        final String incomingJson = mapper.writeValueAsString(incoming);

        // API 呼び出し
        RequestEntity<?> req = RequestEntity //
                .post(URI.create(TEAMS_INCOMING_WEBHOOK)) //
                .contentType(MediaType.APPLICATION_JSON_UTF8) //
                .body(incomingJson);
        new RestTemplate().exchange(req, String.class);
    }

    private boolean shouldSendWarningMessage(Calendar calendar, Team team) {
        int today = calendar.get(Calendar.DATE);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;

        if (today < team.getAlert_start_days()) {
            return false;
        }

        return userRepository.checkSubmission(thisMonth, team.getId()).size() != 0;

    }


    private boolean createFiles() throws IOException {
        Calendar calendar = Calendar.getInstance();
        int day = calendar.get(Calendar.DATE);
        int month = calendar.get(Calendar.MONTH);
        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();


        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {
            if (userRepository.checkSubmission(month, team.getId()).size() == 0 || day == 1) {

                //全員分の月報が登録済みの場合、ファイル化
                List<Report> reports = reportRepository.findByTeamId(team.getId(), month);
                Workbook outputWorkbook = new XSSFWorkbook();
                Sheet sheet = outputWorkbook.createSheet(month + "月");

                Cell outputCell_content;
                for (int i = 0; i < reports.size(); i++) {
                    Row row = sheet.createRow(i);
                    // 0番目のセルの値を取得
                    outputCell_content = row.createCell(0);
                    // セルに値を設定
                    outputCell_content.setCellValue(reports.get(i).getContent());
                }

                String fileName = "src/main/resources/file/" + team.getName() + month + "月.xlsx";
                // 出力用のストリームを用意
                FileOutputStream out = new FileOutputStream(fileName);

                // ファイルへ出力
                outputWorkbook.write(out);
                createFileMessage();

                return true;
            }
        }
        return false;
    }

    private void createFileMessage() throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "ファイル化完了";
        incoming.text = "ファイルが作成されました";

        // 送信データを JSONテキスト化
        final ObjectMapper mapper = new ObjectMapper();
        final String incomingJson = mapper.writeValueAsString(incoming);

        // API 呼び出し
        RequestEntity<?> req = RequestEntity //
                .post(URI.create(TEAMS_INCOMING_WEBHOOK)) //
                .contentType(MediaType.APPLICATION_JSON_UTF8) //
                .body(incomingJson);
        new RestTemplate().exchange(req, String.class);
    }

}
