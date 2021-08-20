package com.example.demo.service;

import com.example.demo.bean.Report;
import com.example.demo.bean.Team;
import com.example.demo.bean.User;
import com.example.demo.repository.ReportRepository;
import com.example.demo.repository.TeamRepository;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class FileCreationService {

    private final TeamRepository teamRepository;
    private final UserRepository userRepository;
    private final ReportRepository reportRepository;
    private final SubmissionService submissionService;
    private final MessageSendingService messageSendingService;

    @Value("${file.name}")
    private String fileName;

    public FileCreationService(TeamRepository teamRepository, UserRepository userRepository, ReportRepository reportRepository, SubmissionService submissionService, MessageSendingService messageSendingService) {
        this.teamRepository = teamRepository;
        this.userRepository = userRepository;
        this.reportRepository = reportRepository;
        this.submissionService = submissionService;
        this.messageSendingService = messageSendingService;
    }


    /**
     * チームメンバー全員分の対象月のレポートが提出されていた場合にファイル化する
     *
     * @throws IOException
     */
    public void createFiles() throws IOException {
        LocalDate date = LocalDate.now();
        int thisYear = date.getYear();
        int thisMonth = date.getMonthValue();
        // Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!submissionService.checkSubmission(thisYear, thisMonth, team)) {
                continue;
            }

            if (userRepository.checkSubmission(thisYear, thisMonth, team.getId()).size() == 0) {
                createFile(team, thisYear, thisMonth);
                messageSendingService.createFileMessage(team.getSending_message_url());
                submissionService.saveSubmission(thisYear, thisMonth, team);

            }
        }
    }

    /**
     * 翌月1日になった時点で未提出者がいても強制的にファイル化
     *
     * @throws IOException
     */
    public void createFilesTemporarily() throws IOException {
        // 前月を取得
        LocalDate targetDate = LocalDate.now().minusMonths(1);
        int targetYear = targetDate.getYear();
        int targetMonth = targetDate.getMonthValue();
        int targetDay = targetDate.getDayOfMonth();

        //Teamテーブルから全Teamの情報を取得する
        List<Team> teams = teamRepository.findAll();

        //Team情報毎に繰り返し処理を行う
        for (Team team : teams) {

            // 既にファイル化されていた場合、continue
            if (!submissionService.checkSubmission(targetYear, targetMonth, team)) {
                continue;
            }

            // 1日の場合
            if (targetDay == 1) {
                createFile(team, targetYear, targetMonth);
                messageSendingService.createFileMessage(team.getSending_message_url());
                submissionService.saveSubmission(targetYear, targetMonth, team);
            }
        }
    }


    /**
     * @param teamName
     * @param year
     * @param month
     * @return
     */
    private String getFileName(String teamName, String year, String month) {
        return "src/main/resources/file/" + fileName.replace("{team_name}", teamName)
                .replace("{yyyy}", year)
                .replace("{mm}", month);
    }

    /**
     * @param team
     * @param year
     * @param month
     * @throws IOException
     */
    private void createFile(Team team, int year, int month) throws IOException {
        try {
            File file = new File(getFileName(team.getName(), String.valueOf(year), String.valueOf(month)));
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
}
