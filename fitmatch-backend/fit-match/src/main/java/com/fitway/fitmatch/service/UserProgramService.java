package com.fitway.fitmatch.service;

import com.fitway.fitmatch.dto.QuestionnaireDTO;
import com.fitway.fitmatch.dto.UserProgramDTO;

public interface UserProgramService {
    UserProgramDTO createProgramFromQuestionnaire(QuestionnaireDTO questionnaire);
    UserProgramDTO getActiveProgramByUserId(Long userId);
}