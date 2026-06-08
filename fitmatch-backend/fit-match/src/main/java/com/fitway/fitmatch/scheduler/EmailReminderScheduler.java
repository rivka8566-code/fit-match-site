package com.fitway.fitmatch.scheduler;

import com.fitway.fitmatch.entity.ProgramWorkoutStatus;
import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.enums.ProgramStatus;
import com.fitway.fitmatch.repository.ProgramWorkoutStatusRepository;
import com.fitway.fitmatch.repository.UserProgramRepository;
import com.fitway.fitmatch.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class EmailReminderScheduler {

    private final UserRepository userRepository;
    private final UserProgramRepository userProgramRepository;
    private final ProgramWorkoutStatusRepository statusRepository;
    private final JavaMailSender mailSender;

    @Scheduled(cron = "0 0 21 ? * SAT")
    public void sendWeeklyReminders() {
        List<User> users = userRepository.findAll();

        for (User user : users) {
            Optional<UserProgram> activeProgramOpt = userProgramRepository
                    .findByUserIdAndStatus(user.getId(), ProgramStatus.ACTIVE);

            if (activeProgramOpt.isEmpty()) {
                // אין תוכנית פעילה - שלח תזכורת להתחיל
                sendMail(user.getEmail(),
                        "FitMatch — הגיע הזמן לחזור לשגרה",
                        "שלום " + user.getFullName() + ",\n\n" +
                        "עדיין אין לך תוכנית אימונים פעילה השבוע.\n" +
                        "היכנס לאתר, מלא את השאלון הקצר ונבנה עבורך תוכנית מותאמת אישית.\n\n" +
                        "FitMatch — אנחנו כאן בשבילך.");
                continue;
            }

            UserProgram program = activeProgramOpt.get();
            int daysPerWeek = program.getDaysPerWeekTarget();
            List<ProgramWorkoutStatus> statuses = statusRepository.findByProgramId(program.getId());
            List<Long> completedIds = statuses.stream()
                    .filter(ProgramWorkoutStatus::isCompleted)
                    .map(ProgramWorkoutStatus::getWorkoutId)
                    .toList();

            // חישוב השבוע הנוכחי לפי תאריך התחלה
            long daysSinceStart = program.getStartDate() != null
                    ? ChronoUnit.DAYS.between(program.getStartDate(), LocalDate.now())
                    : 0;
            int currentWeek = (int) (daysSinceStart / 7);

            // בדיקה: האם השבוע הראשון קצר מדי (פחות מ-daysPerWeek ימים עד סוף השבוע)?
            long daysRemainingInFirstWeek = 7 - (daysSinceStart % 7);
            boolean isPartialWeek = (daysSinceStart == 0 && daysRemainingInFirstWeek < daysPerWeek);

            if (isPartialWeek) {
                // שבוע חלקי - לא בודקים עמידה ביעד
                continue;
            }

            // כמה אימונים בוצעו בשבוע הנוכחי
            var workouts = program.getWorkouts();
            int weekStart = currentWeek * daysPerWeek;
            int weekEnd = Math.min(weekStart + daysPerWeek, workouts.size());

            if (weekStart >= workouts.size()) continue;

            long completedThisWeek = 0;
            for (int i = weekStart; i < weekEnd; i++) {
                if (completedIds.contains(workouts.get(i).getId())) completedThisWeek++;
            }

            if (completedThisWeek < daysPerWeek) {
                int missing = daysPerWeek - (int) completedThisWeek;
                sendMail(user.getEmail(),
                        "FitMatch — נותרו עוד " + missing + " אימונים השבוע",
                        "שלום " + user.getFullName() + ",\n\n" +
                        "השבוע ביצעת " + completedThisWeek + " מתוך " + daysPerWeek + " אימונים יעד שלך.\n" +
                        "נותרו " + missing + " אימונים להשלמת היעד השבועי.\n\n" +
                        "כנס לאתר ותשלים — אתה יכול!\n\n" +
                        "FitMatch");
            }
        }
    }

    private void sendMail(String to, String subject, String body) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(to);
            msg.setSubject(subject);
            msg.setText(body);
            mailSender.send(msg);
        } catch (Exception e) {
            System.out.println("שגיאה בשליחת מייל ל-" + to + ": " + e.getMessage());
        }
    }
}
