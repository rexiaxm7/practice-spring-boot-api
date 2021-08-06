package com.example.demo.batch;

import com.example.demo.bean.Report;
import com.example.demo.bean.Submission;
import com.example.demo.bean.Team;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
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
    public final String TEAMS_INCOMING_WEBHOOK = System.getenv("TEAMS_INCOMING_WEBHOOK");
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SubmissionRepository submissionRepository;

    public ReportBatch(TeamRepository teamRepository, UserRepository userRepository, ReportRepository reportRepository, SubmissionRepository submissionRepository) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.submissionRepository = submissionRepository;
    }

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
        sendWarningMessages();
        createFiles();
        createFilesTemporarily();
    }

    /**
     * 入力開始日にメッセージを送信する
     * @throws IOException
     */
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
                sendInputMessage(team.getSending_message_url());
            }
        }
    }

    /**
     * 入力開始日用のメッセージをセットしメッセージを送信するメソッドを呼び出す
     *
     * @param sending_message_url
     * @throws JsonProcessingException
     */
    private void sendInputMessage(String sending_message_url) throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "入力開始日";
        incoming.text = "月報を提出してください。";

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 今日がteamテーブルに登録されている入力開始日かどうか確認する
     *
     * @param calendar
     * @param team
     * @return 入力開始日だった場合にtrue、異なる場合にはfalse
     */
    private boolean shouldSendInputMessage(Calendar calendar, Team team) {
        int today = calendar.get(Calendar.DATE);
        return team.getInput_start_date() == today;
    }

    /**
     * 通知開始日以降、月末までメッセージを送り続ける
     *
     * @throws IOException
     */
    private void sendWarningMessages() throws IOException {
        // 日付の取得
        Calendar calendar = Calendar.getInstance();

        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        // Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            if (!checkSubmission(calendar, team)) {
                continue;
            }
            //全員分の月報が登録されているか確認
            if (shouldSendWarningMessage(calendar, team)) {
                //全員分の月報が未登録の場合、メッセージを通知
                sendWarningMessage(team.getSending_message_url());
            }
        }
    }

    /**
     *
     * 通知開始日用のメッセージをセットしメッセージを送信するメソッドを呼び出す
     *
     * @param sending_message_url
     * @throws JsonProcessingException
     */
    private void sendWarningMessage(String sending_message_url) throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "通知開始日";
        incoming.text = "月報が提出されていません";

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 本日が通知開始日以降でチームメンバーの月報が全員分登録されていないか確認する
     *
     * @param calendar
     * @param team
     * @return 通知開始日以降でチームメンバーの月報が全員分登録されていなかった場合にtrue
     *         通知開始日前もしくはチームメンバーの月報が全員分登録されていた場合にfalse
     */
    private boolean shouldSendWarningMessage(Calendar calendar, Team team) {
        int today = calendar.get(Calendar.DATE);
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;

        if (today < team.getAlert_start_days()) {
            return false;
        }

        return userRepository.checkSubmission(thisYear,thisMonth, team.getId()).size() != 0;

    }

    /**
     * チームメンバー全員分の対象月のレポートが提出されていた場合にファイル化する
     *
     * @throws IOException
     */
    private void createFiles() throws IOException {
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;
        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!checkSubmission(calendar, team)) {
                continue;
            }

            //全員分の月報が登録済みの場合、ファイル化
            if (userRepository.checkSubmission(thisYear,thisMonth, team.getId()).size() == 0) {
                List<Report> reports = reportRepository.findByTeamId(team.getId(), thisYear, thisMonth);
                Workbook outputWorkbook = new XSSFWorkbook();
                Sheet sheet = outputWorkbook.createSheet(thisMonth + "月");

                Cell outputCell_content;
                for (int i = 0; i < reports.size(); i++) {
                    Row row = sheet.createRow(i);
                    // 0番目のセルの値を取得
                    outputCell_content = row.createCell(0);
                    // セルに値を設定
                    outputCell_content.setCellValue(reports.get(i).getContent());
                }

                String fileName = "src/main/resources/file/" + team.getName() + thisYear + "年" + thisMonth + "月.xlsx";
                // 出力用のストリームを用意
                FileOutputStream out = new FileOutputStream(fileName);

                // ファイルへ出力
                outputWorkbook.write(out);
                createFileMessage(team.getSending_message_url());
                saveSubmission(calendar, team);
            }
        }
    }

    /**
     * ファイル作成時用のメッセージをセットしメッセージを送信するメソッドを呼び出す
     *
     * @param sending_message_url
     * @throws JsonProcessingException
     */
    private void createFileMessage(String sending_message_url) throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = "ファイル化完了";
        incoming.text = "ファイルが作成されました";

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 翌月1日になった時点で未提出者がいても強制的にファイル化
     *
     * @throws IOException
     */
    private void createFilesTemporarily() throws IOException {
        Calendar calendar = Calendar.getInstance();
        int thisYear = calendar.get(Calendar.YEAR);
        // 前月を取得
        int thisMonth = calendar.get(Calendar.MONTH);
        int today = calendar.get(Calendar.DATE);

        // 前月が12月の場合、年にずれが生じるので調整する
        if (thisMonth == 12) {
            thisYear = thisYear - 1;
        }
        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();


        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!checkSubmission(calendar, team)) {
                continue;
            }

            // 1日の場合
            if (today == 1) {
                List<Report> reports = reportRepository.findByTeamId(team.getId(), thisYear, thisMonth);
                Workbook outputWorkbook = new XSSFWorkbook();
                Sheet sheet = outputWorkbook.createSheet(thisMonth + "月");

                Cell outputCell_content;
                for (int i = 0; i < reports.size(); i++) {
                    Row row = sheet.createRow(i);
                    // 0番目のセルの値を取得
                    outputCell_content = row.createCell(0);
                    // セルに値を設定
                    outputCell_content.setCellValue(reports.get(i).getContent());
                }

                String fileName = "src/main/resources/file/" + team.getName() + thisYear + "年" + thisMonth + "月.xlsx";
                // 出力用のストリームを用意
                FileOutputStream out = new FileOutputStream(fileName);

                // ファイルへ出力
                outputWorkbook.write(out);
                createFileMessage(team.getSending_message_url());
                saveSubmission(calendar, team);
            }
        }
    }

    /**
     * Teamテーブルに登録されている送り先にメッセージを送信する
     *
     * @param incoming            - 送信内容
     * @param sending_message_url - 送り先
     * @throws JsonProcessingException
     */
    private void sendMessage(ReportBatch.Message incoming, String sending_message_url) throws JsonProcessingException {
        // 送信データを JSONテキスト化
        final ObjectMapper mapper = new ObjectMapper();
        final String incomingJson = mapper.writeValueAsString(incoming);

        // API 呼び出し
        RequestEntity<?> req = RequestEntity //
                .post(URI.create(sending_message_url)) //
                .contentType(MediaType.APPLICATION_JSON) //
                .body(incomingJson);
        new RestTemplate().exchange(req, String.class);
    }

    /**
     * 対象チームの当年、月のレポートがファイル化されているか確認する
     *
     * @param calendar
     * @param team     - 対象チーム
     * @return 登録されていなかった場合はtrue、されていた場合はfalseを返す
     */
    private boolean checkSubmission(Calendar calendar, Team team) {
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;
        return submissionRepository.countBySubmission(team.getId(), thisYear, thisMonth) == 0;
    }

    /**
     * submissionテーブルにファイルが作成されたことを登録する
     *
     * @param calendar
     * @param team
     */
    private void saveSubmission(Calendar calendar, Team team) {
        int thisYear = calendar.get(Calendar.YEAR);
        int thisMonth = calendar.get(Calendar.MONTH) + 1;

        Submission submission = new Submission();
        submission.setTeam_id(team.getId());
        submission.setYear(thisYear);
        submission.setMonth(thisMonth);

        submissionRepository.save(submission);
    }


}
