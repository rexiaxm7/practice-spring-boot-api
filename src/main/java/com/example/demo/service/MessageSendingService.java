package com.example.demo.service;


import com.example.demo.bean.Team;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import org.springframework.context.MessageSource;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.util.List;
import java.util.Locale;

@Service
public class MessageSendingService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final MessageSource msg;
    private final SubmissionService submissionService;

    public MessageSendingService(TeamRepository teamRepository, UserRepository userRepository, MessageSource msg, SubmissionService submissionService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.msg = msg;
        this.submissionService = submissionService;
    }

    /**
     * 入力開始日にメッセージを送信する
     *
     * @throws IOException
     */
    public void sendInputMessages() throws IOException {
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
        MessageSendingService.Message incoming = new MessageSendingService.Message();
        incoming.title = msg.getMessage("incoming.title.input", null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.input", null, Locale.JAPAN);

        sendMessage(incoming, sending_message_url);
    }

    /**
     * 今日がteamテーブルに登録されている入力開始日かどうか確認する
     *
     * @param date
     * @param team
     * @return 入力開始日だった場合にtrue、異なる場合にはfalse
     */
    private boolean shouldSendInputMessage(LocalDate date, Team team) {
        int today = date.getDayOfMonth();
        return team.getInput_start_date() == today;
    }

    /**
     * 通知開始日以降、月末までメッセージを送り続ける
     *
     * @throws IOException
     */
    public void sendWarningMessages() throws IOException {
        // 日付の取得
        LocalDate date = LocalDate.now();

        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        // Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            if (!submissionService.checkSubmission(date.getYear(), date.getMonthValue(), team)) {
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
        MessageSendingService.Message incoming = new MessageSendingService.Message();
        incoming.title = msg.getMessage("incoming.title.alert", null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.alert", null, Locale.JAPAN);

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
     * ファイル作成時用のメッセージをセットしメッセージを送信するメソッドを呼び出す
     *
     * @param sending_message_url
     * @throws JsonProcessingException
     */
    public void createFileMessage(String sending_message_url) throws JsonProcessingException {
        MessageSendingService.Message incoming = new MessageSendingService.Message();
        incoming.title = msg.getMessage("incoming.title.done", null, Locale.JAPAN);
        incoming.text = msg.getMessage("incoming.text.done", null, Locale.JAPAN);

        sendMessage(incoming, sending_message_url);
    }

    /**
     * Teamテーブルに登録されている送り先にメッセージを送信する
     *
     * @param incoming            - 送信内容
     * @param sending_message_url - 送り先
     * @throws JsonProcessingException
     */
    private void sendMessage(MessageSendingService.Message incoming, String sending_message_url) throws JsonProcessingException {
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
     * Webhook データ構造
     */
    @JsonSerialize
    private class Message {
        public String title;
        public String text;
    }

}
