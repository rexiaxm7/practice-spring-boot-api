package com.example.demo.service;

import com.example.demo.bean.Submission;
import com.example.demo.bean.Team;
import com.example.demo.repository.SubmissionRepository;
import org.springframework.stereotype.Service;

@Service
public class SubmissionService {

    private final SubmissionRepository submissionRepository;

    public SubmissionService(SubmissionRepository submissionRepository) {
        this.submissionRepository = submissionRepository;
    }

    /**
     * @param year  対象の月
     * @param month 対象の年
     * @param team  対象のチーム
     * @return
     */
    public boolean checkSubmission(int year, int month, Team team) {

        return submissionRepository.countBySubmission(team.getId(), year, month) == 0;
    }

    /**
     * @param year  対象の年
     * @param month 対象の月
     * @param team  対象のチーム
     */
    public void saveSubmission(int year, int month, Team team) {

        Submission submission = new Submission();
        submission.setTeam_id(team.getId());
        submission.setYear(year);
        submission.setMonth(month);

        submissionRepository.save(submission);
    }
}
