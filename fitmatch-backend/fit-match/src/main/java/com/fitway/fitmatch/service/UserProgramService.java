package com.fitway.fitmatch.service;

import java.util.List;

import com.fitway.fitmatch.dto.QuestionnaireDTO;
import com.fitway.fitmatch.dto.UserProgramDTO;

public interface UserProgramService {
    UserProgramDTO createProgramFromQuestionnaire(QuestionnaireDTO questionnaire);
    UserProgramDTO getActiveProgramByUserId(Long userId);
    List<UserProgramDTO> getAllProgramsByUserId(Long userId); // חדש: היסטוריית תוכניות
}