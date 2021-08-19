package com.example.demo.batch;

import com.example.demo.bean.Report;
import com.example.demo.bean.Submission;
import com.example.demo.bean.Team;
import com.example.demo.bean.User;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.SubmissionRepository;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Component
public class ReportBatch {
    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SubmissionRepository submissionRepository;
    private final MessageSource msg;

    @Value("${file.name}")
    private String fileName;

    public ReportBatch(TeamRepository teamRepository, UserRepository userRepository, ReportRepository reportRepository, SubmissionRepository submissionRepository, MessageSource msg) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.submissionRepository = submissionRepository;
        this.msg = msg;

    }

    @Scheduled(cron = "${batch.cron}", zone = "${batch.timezone}")
    public void batchMain() throws IOException {
        sendInputMessages();
        sendWarningMessages();
        createFiles();
        createFilesTemporarily();
    }

    /**
     * 入力開始日にメッセージを送信する
     *
     * @throws IOException
     */
    private void sendInputMessages() throws IOException {
        //日付の取得
        LocalDate date = LocalDate.now();

        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            //本日が入力開始日か確認
            if (shouldSendInputMessage(date, team)) {
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
        incoming.title = msg.getMessage("incoming.title.input",null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.input",null, Locale.JAPAN);

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 今日がteamテーブルに登録されている入力開始日かどうか確認する
     *
     * @param date
     * @param team
     * @return 入力開始日だった場合にtrue、異なる場合にはfalse
     */
    private boolean shouldSendInputMessage(LocalDate date , Team team) {
        int today = date.getDayOfMonth();
        return team.getInput_start_date() == today;
    }

    /**
     * 通知開始日以降、月末までメッセージを送り続ける
     *
     * @throws IOException
     */
    private void sendWarningMessages() throws IOException {
        // 日付の取得
        LocalDate date = LocalDate.now();

        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        // Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            if (!checkSubmission(date.getYear(),date.getMonthValue(), team)) {
                continue;
            }
            //全員分の月報が登録されているか確認
            if (shouldSendWarningMessage(date, team)) {
                //全員分の月報が未登録の場合、メッセージを通知
                sendWarningMessage(team.getSending_message_url());
            }
        }
    }

    /**
     * 通知開始日用のメッセージをセットしメッセージを送信するメソッドを呼び出す
     *
     * @param sending_message_url
     * @throws JsonProcessingException
     */
    private void sendWarningMessage(String sending_message_url) throws JsonProcessingException {
        ReportBatch.Message incoming = new ReportBatch.Message();
        incoming.title = msg.getMessage("incoming.title.alert",null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.alert",null, Locale.JAPAN);

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 本日が通知開始日以降でチームメンバーの月報が全員分登録されていないか確認する
     *
     * @param date
     * @param team
     * @return 通知開始日以降でチームメンバーの月報が全員分登録されていなかった場合にtrue
     * 通知開始日前もしくはチームメンバーの月報が全員分登録されていた場合にfalse
     */
    private boolean shouldSendWarningMessage(LocalDate date, Team team) {
        int thisYear = date.getYear();
        int thisMonth = date.getMonthValue();
        int today = date.getDayOfMonth();

        if (today < team.getAlert_start_days()) {
            return false;
        }

        return userRepository.checkSubmission(thisYear, thisMonth, team.getId()).size() != 0;

    }

    /**
     * チームメンバー全員分の対象月のレポートが提出されていた場合にファイル化する
     *
     * @throws IOException
     */
    private void createFiles() throws IOException {
        LocalDate date = LocalDate.now();
        int thisYear = date.getYear();
        int thisMonth = date.getMonthValue();
        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!checkSubmission(thisYear, thisMonth, team)) {
                continue;
            }

            if (userRepository.checkSubmission(thisYear, thisMonth, team.getId()).size() == 0) {
                createFile(team, thisYear, thisMonth);
                createFileMessage(team.getSending_message_url());
                saveSubmission(thisYear, thisMonth, team);

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
        incoming.title = msg.getMessage("incoming.title.done",null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.done",null, Locale.JAPAN);

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 翌月1日になった時点で未提出者がいても強制的にファイル化
     *
     * @throws IOException
     */
    private void createFilesTemporarily() throws IOException  {
        // 前月を取得
        LocalDate targetDate = LocalDate.of(2021,8,1).minusMonths(1);
        int targetYear = targetDate.getYear();
        int targetMonth = targetDate.getMonthValue();
        int targetDay = targetDate.getDayOfMonth();

        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!checkSubmission(targetYear, targetMonth, team)) {
                continue;
            }

            // 1日の場合
            if (targetDay == 1) {
                createFile(team, targetYear, targetMonth);
                createFileMessage(team.getSending_message_url());
                saveSubmission(targetYear, targetMonth, team);
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
     *
     * @param year 対象の月
     * @param month 対象の年
     * @param team 対象のチーム
     * @return
     */
    private boolean checkSubmission(int year, int month, Team team) {

        return submissionRepository.countBySubmission(team.getId(), year, month) == 0;
    }

    /**
     *
     * @param year 対象の年
     * @param month 対象の月
     * @param team 対象のチーム
     */
    private void saveSubmission(int year,int month, Team team) {

        Submission submission = new Submission();
        submission.setTeam_id(team.getId());
        submission.setYear(year);
        submission.setMonth(month);

        submissionRepository.save(submission);
    }

    /**
     *
     * @param teamName
     * @param year
     * @param month
     * @return
     */
    public String getFileName(String teamName, String year, String month){
        return "src/main/resources/file/" + fileName.replace("{team_name}",teamName)
                .replace("{yyyy}",year)
                .replace("{mm}",month);
    }

    /**
     *
     * @param team
     * @param year
     * @param month
     * @throws IOException
     */
    public void createFile(Team team , int year, int month) throws IOException {
        try{
        File file = new File(getFileName( team.getName(),String.valueOf(year),String.valueOf(month)));
        BufferedWriter bw = new BufferedWriter(new FileWriter(file, true));
        List<Report> reports = reportRepository.findByTeamId(team.getId(), year, month);
        for (int i = 0; i < reports.size(); i++) {
            Optional<User> user = userRepository.findById(reports.get(i).getUser_id());
            bw.write(user.get().getName() + " : ");
            bw.write(reports.get(i).getContent());
            bw.newLine();
        }
        bw.close();
        } catch (IOException e) {
            System.out.println(e);
        }
    }

    /**
     * Webhook データ構造
     */
    @JsonSerialize
    private class Message {
        public String title;
        public String text;
        public File file;
    }


}
