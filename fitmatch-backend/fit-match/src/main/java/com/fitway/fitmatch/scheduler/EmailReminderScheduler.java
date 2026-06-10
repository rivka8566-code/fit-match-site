package com.fitway.fitmatch.scheduler;

import com.fitway.fitmatch.entity.ProgramWorkoutStatus;
import com.fitway.fitmatch.entity.User;
import com.fitway.fitmatch.entity.UserProgram;
import com.fitway.fitmatch.entity.enums.ProgramStatus;
import com.fitway.fitmatch.repository.ProgramWorkoutStatusRepository;
import com.fitway.fitmatch.repository.UserProgramRepository;
import com.fitway.fitmatch.repository.UserRepository;

import jakarta.mail.internet.MimeMessage;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
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

    // מריץ את הבדיקה זמנית בכל יום שלישי בשעה 17:01 (שני את ה-Cron לפי הצורך
    // לבדיקה)
    @Scheduled(cron = "0 0 21 ? * SAT")
    @Transactional
    public void sendWeeklyReminders() {
        // הדפסת בדיקה כדי לראות דרך איזה Host השרת פועל באמת
        System.out.println("--- השרת מנסה כרגע לשלוח מייל דרך ה-Host הבא: "
                + ((org.springframework.mail.javamail.JavaMailSenderImpl) mailSender).getHost());

        List<User> users = userRepository.findAll();

        for (User user : users) {
            Optional<UserProgram> activeProgramOpt = userProgramRepository
                    .findByUserIdAndStatus(user.getId(), ProgramStatus.ACTIVE);

            // -------------------------------------------------------------------------
            // מקרה 1: למשתמש אין תוכנית אימונים פעילה
            // -------------------------------------------------------------------------
            if (activeProgramOpt.isEmpty()) {
                String noProgramHtml = "" +
                        "<div style='font-family: Arial, sans-serif; direction: rtl; text-align: right; max-width: 600px; margin: 0 auto; padding: 25px; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05);'>"
                        +
                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<span style='font-size: 40px;'>💪</span>" +
                        "</div>" +
                        "<h2 style='color: #2d3748; border-bottom: 2px solid #e2e8f0; padding-bottom: 12px; margin-top: 0;'>הגיע הזמן לחזור לשגרה עם FitMatch!</h2>"
                        +
                        "<p style='font-size: 16px; color: #4a5568;'>שלום <strong>" + user.getFullName()
                        + "</strong>,</p>" +
                        "<p style='font-size: 15px; line-height: 1.6; color: #4a5568;'>" +
                        "שמנו לב שאין לך כרגע תוכנית אימונים פעילה במערכת. חבל לפספס את המומנטום וההתקדמות שלך!" +
                        "</p>" +
                        "<p style='font-size: 15px; line-height: 1.6; color: #4a5568;'>" +
                        "הכנסו עכשיו לאתר, מלאו את השאלון הקצר והתאימו לעצמכם תוכנית אימונים חדשה ומגוונת המותאמת בדיוק עבורכם."
                        +
                        "</p>" +
                        "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='http://localhost:5173/dashboard' style='background-color: #3182ce; color: white; padding: 12px 30px; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; display: inline-block; box-shadow: 0 4px 6px rgba(49,130,206,0.3);'>התחל תוכנית חדשה בקליק</a>"
                        +
                        "</div>" +
                        "<hr style='border: 0; border-top: 1px solid #edf2f7; margin-top: 25px;'>" +
                        "<p style='font-size: 12px; color: #a0aec0; text-align: center; margin-bottom: 0;'>FitMatch — מלווים אותך אל עבר היעדים שלך.</p>"
                        +
                        "</div>";

                sendMail(user.getEmail(), "הגיע הזמן לחזור לשגרה ב-FitMatch!", noProgramHtml);
                continue;
            }

            // -------------------------------------------------------------------------
            // מקרה 2: יש תוכנית פעילה - חישוב התקדמות שבועית
            // -------------------------------------------------------------------------
            UserProgram program = activeProgramOpt.get();
            long daysSinceStart = ChronoUnit.DAYS.between(program.getStartDate(), LocalDate.now());
            int currentWeek = (int) (daysSinceStart / 7);

            if (currentWeek >= program.getDurationWeeks()) {
                continue; // התוכנית הסתיימה
            }

            boolean isPartialWeek = (daysSinceStart % 7) < 7 && currentWeek == 0
                    && LocalDate.now().getDayOfWeek().getValue() < 6;

            if (isPartialWeek) {
                continue;
            }

            int daysPerWeek = program.getDaysPerWeekTarget();
            List<ProgramWorkoutStatus> statuses = statusRepository.findByProgramId(program.getId());
            List<Long> completedIds = statuses.stream()
                    .filter(ProgramWorkoutStatus::isCompleted)
                    .map(ProgramWorkoutStatus::getWorkoutId)
                    .toList();

            var workouts = program.getWorkouts();
            int weekStart = currentWeek * daysPerWeek;
            int weekEnd = Math.min(weekStart + daysPerWeek, workouts.size());

            if (weekStart >= workouts.size())
                continue;

            long completedThisWeek = 0;
            for (int i = weekStart; i < weekEnd; i++) {
                if (completedIds.contains(workouts.get(i).getId())) {
                    completedThisWeek++;
                }
            }

            if (completedThisWeek < daysPerWeek) {
                int missing = daysPerWeek - (int) completedThisWeek;

                String reminderHtml = "" +
                        "<div style='font-family: Arial, sans-serif; direction: rtl; text-align: right; max-width: 600px; margin: 0 auto; padding: 25px; border: 1px solid #e2e8f0; border-radius: 12px; box-shadow: 0 4px 6px rgba(0,0,0,0.05);'>"
                        +
                        "<div style='text-align: center; margin-bottom: 20px;'>" +
                        "<span style='font-size: 40px;'>🔥</span>" +
                        "</div>" +
                        "<h2 style='color: #2d3748; border-bottom: 2px solid #e2e8f0; padding-bottom: 12px; margin-top: 0; text-align: center;'>FitMatch — נותרו עוד "
                        + missing + " אימונים לשבוע זה!</h2>" +
                        "<p style='font-size: 16px; color: #4a5568;'>שלום <strong>" + user.getFullName()
                        + "</strong>,</p>" +
                        "<p style='font-size: 15px; line-height: 1.6; color: #4a5568;'>" +
                        "השבוע ביצעת <span style='color: #38a169; font-weight: bold; font-size: 18px;'>"
                        + completedThisWeek + "</span> מתוך יעד של " + daysPerWeek + " אימונים." +
                        "</p>" +
                        "<p style='font-size: 15px; line-height: 1.6; color: #e53e3e; font-weight: bold;'>" +
                        "נותרו לך עוד " + missing + " אימונים כדי להשלים את היעד השבועי שלך ולשמור על הרצף!" +
                        "</p>" +
                        "<p style='font-size: 15px; line-height: 1.6; color: #4a5568;'>" +
                        "אל תוותר לעצמך, כל אימון מקרב אותך אל המטרה שלך. כנס עכשיו לתוכנית וסמן וי על האימון הבא." +
                        "</p>" +
                        "<div style='text-align: center; margin: 30px 0;'>" +
                        "<a href='http://localhost:5173/dashboard' style='background-color: #dd6b20; color: white; padding: 12px 30px; text-decoration: none; font-weight: bold; font-size: 16px; border-radius: 8px; display: inline-block; box-shadow: 0 4px 6px rgba(221,107,32,0.3);'>כנס לאתר ותשלים — אתה יכול! 💪</a>"
                        +
                        "</div>" +
                        "<hr style='border: 0; border-top: 1px solid #edf2f7; margin-top: 25px;'>" +
                        "<p style='font-size: 12px; color: #a0aec0; text-align: center; margin-bottom: 0;'>FitMatch — אנחנו כאן בשבילך בכל אימון מחדש.</p>"
                        +
                        "</div>";

                sendMail(user.getEmail(), "FitMatch — נותרו עוד " + missing + " אימונים השבוע", reminderHtml);
            }
        }
    }

    private void sendMail(String to, String subject, String bodyHtml) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            helper.setFrom("info@fitmatch.com");
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(bodyHtml, true);

            mailSender.send(mimeMessage);
            System.out.println("🎯 מייל מעוצב נשלח בהצלחה אל: " + to);
        } catch (Exception e) {
            System.out.println("❌ שגיאה בשליחת מייל ל-" + to + ": " + e.getMessage());
        }
    }
}