package com.fitway.fitmatch.scheduler;

import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.entity.enums.ProgramStatus;
import com.fitway.fitmatch.repository.UserRepository;
import com.fitway.fitmatch.repository.UserProgramRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.util.List;

@Component
@RequiredArgsConstructor
public class EmailReminderScheduler {

    private final UserRepository userRepository;
    private final UserProgramRepository userProgramRepository;
    private final JavaMailSender mailSender;

    // רץ אוטומטית בכל מוצאי שבת בשעה 21:00
    @Scheduled(cron = "0 0 21 ? * SAT")
    public void sendWeeklyReminders() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            // בדיקה פשוטה: האם למשתמש הזה אין תוכנית אימונים פעילה כרגע?
            boolean hasNoActiveProgram = userProgramRepository
                    .findByUserIdAndStatus(user.getId(), ProgramStatus.ACTIVE)
                    .isEmpty();

            if (hasNoActiveProgram) {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(user.getEmail());
                message.setSubject("היי " + user.getFullName() + ", מחכים לך ב-FitMatch! 💪");
                message.setText("ראינו שעדיין לא בנית לעצמך תוכנית אימונים השבוע. " +
                        "היכנסי לאתר, מלאי את השאלון הקצר, ונתאים לך את סרטוני הכושר המושלמים עבורך! " +
                        "אנחנו כאן כדי לעזור לך להגיע ליעדים שלך כמו אלופה. נתראה? 🔥");

                try {
                    mailSender.send(message);
                } catch (Exception e) {
                    System.out.println("שגיאה בשליחת מייל ל- " + user.getEmail());
                }
            }
        }
    }
}