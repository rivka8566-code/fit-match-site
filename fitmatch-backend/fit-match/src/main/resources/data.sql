-- 1. הכנסת ציוד כושר (טבלת equipment)
INSERT INTO equipment (id, name) VALUES (1, 'משקולות יד');
INSERT INTO equipment (id, name) VALUES (2, 'מזרן יוגה');
INSERT INTO equipment (id, name) VALUES (3, 'גומיות התנגדות');
INSERT INTO equipment (id, name) VALUES (4, 'ללא ציוד (משקל גוף)');

-- 2. הכנסת חלקי גוף / קבוצות שרירים (טבלת body_parts)
INSERT INTO body_parts (id, name) VALUES (1, 'בטן וליבה');
INSERT INTO body_parts (id, name) VALUES (2, 'רגליים וישבן');
INSERT INTO body_parts (id, name) VALUES (3, 'חזה וידיים');
INSERT INTO body_parts (id, name) VALUES (4, 'גב וכתפיים');
INSERT INTO body_parts (id, name) VALUES (5, 'אימון גוף מלא');

-- 3. הכנסת חוקי הטיפים התזונתיים (טבלת nutrition_tips)
INSERT INTO nutrition_tips (id, min_calories_threshold, max_calories_threshold, food_recommendation, water_recommendation)
VALUES (1, 0, 150, 'אימון קל וקצר! תמר אחד או חופן שקדים קטן יספיקו כדי להחזיר אנרגיה זמינה לגוף מבלי להכביד על מערכת העיכול.', 'זכור לשתות כוס מים אחת מיד בתום התרגיל.');

INSERT INTO nutrition_tips (id, min_calories_threshold, max_calories_threshold, food_recommendation, water_recommendation)
VALUES (2, 151, 300, 'עבודה מעולה! הגוף שרף לא מעט אנרגיה. מומלץ לשלב חלבון ופחמימה מורכבת בטווח של שעה מהאימון: למשל גביע יוגורט חלבון עם גרנולה או כריך טונה מלחם מלא.', 'הזעת כהלכה! משימה לשעה הקרובה: לשתות לפחות 2-3 כוסות מים מלאות.');

INSERT INTO nutrition_tips (id, min_calories_threshold, max_calories_threshold, food_recommendation, water_recommendation)
VALUES (3, 301, 600, 'אימון עצים בטירוף, כל הכבוד! הגוף זקוק לארוחה מלאה ומאוששת תוך 90 דקות. שלבי מנת חלבון איכותית יחד עם פחמימה מורכבת מבושלת והרבה ירקות.', 'איבוד הנוזלים היה גבוה במיוחד. שתי חצי ליטר מים באופן מיידי, והמשיכי לשתות בלגימות קטנות לאורך כל הערב.');

-- -- 4. הכנסת משתמשי בדיקה ראשוניים (טבלת users)
-- INSERT INTO users (id, email, password, full_name, total_calories_burned, role) 
-- VALUES (1, 'admin@fitmatch.com', 'admin123', 'מנהל המערכת', 0, 'ADMIN');

-- INSERT INTO users (id, email, password, full_name, total_calories_burned, role) 
-- VALUES (2, 'user@fitmatch.com', 'user123', 'ישראל ישראלי', 0, 'USER');

-- 5. מאגר ענק של 25 אימונים מבוסס על הקישורים המדויקים שלך
INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (1, 'אימון כוח פלג גוף עליון', 'חיזוק שרירי החזה, הגב והידיים בעזרת תרגילים ממוקדים לפיתוח כוח.', 'Zkq3gG2MdOc', 25, 210, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (2, 'אימון אירובי עצים ושריפת שומן', 'אימון קצבי ומאתגר להעלאת הדופק ושריפת קלוריות מוגברת בכל הגוף.', 'ffAZbDtchaA', 20, 240, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (3, 'חיזוק פלג גוף תחתון ורגליים', 'עבודה עמוקה על שרירי הארבע-ראשי, הירך האחורית והישבן לעיצוב מקסימלי.', 'Ga9F-tSNGHA', 30, 260, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (4, 'אימון בטן וליבה דינמי', 'תרגילים מתקדמים לבניית קוביות, חיזוק חגורת הבטן העמוקה ויציבה נכונה.', 'yPebKz8Kgyk', 15, 130, 'ADVANCED', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (5, 'אימון גוף מלא פונקציונלי', 'שילוב של כוח וסיבולת לב-ריאה באימון אחד שעובד על כל שריר ושריר.', '8RTvLH1SUrI', 35, 310, 'INTERMEDIATE', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (6, 'אימון היפרטרופיה חזה ויד קדמית', 'אימון משקולות קלאסי בחדר הכושר ליצירת נפח, גירוי שריר מוגבר וחיזוק.', '8EoMGOykUs8', 40, 340, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (7, 'אימון גב מורחב ויד אחורית', 'עבודה ממוקדת על שריר הרחב-גבי והטרפזים בשילוב פשיטות מרפקים.', 'uWmIwcombok', 45, 360, 'ADVANCED', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (8, 'אימון כתפיים פגז וסימטריה', 'תרגילי לחיצה והרחקה לפיתוח כתפיים עגולות, חזקות ויציבות.', 'FzZrB14tGMs', 30, 250, 'BEGINNER', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (9, 'אימון רגליים כבד לאלופים', 'סקוואטים ומכרעים בעומס גבוה לבניית מסת שריר וכוח מתפרץ ברגליים.', '0UBV6PVJBu4', 50, 420, 'ADVANCED', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (10, 'אימון HIIT ביתי ללא ציוד', 'אימון הפוגות מהיר ושורף במיוחד לשיפור סיבולת ושריפת שומנים.', 'vs3S4Ql0R-o', 15, 180, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (11, 'אימון פלג גוף עליון עם משקולות יד', 'עיצוב וחיזוק החזה, הכתפיים והזרועות מהנוחות של הסלון בבית.', 'c3947cqoE3g', 25, 200, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (12, 'אימון גוף מלא אקטיבי', 'אימון דינמי מבוסס משקל גוף המשלב תנועה, כוח וקואורדינציה.', 'ee_747GMSEo', 20, 160, 'BEGINNER', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (13, 'אימון כוח פונקציונלי בחוץ', 'עבודה פתוחה בטבע המבוססת על תרגילי קליסטניקס בסיסיים ומורכבים.', 'u5M011lRLak', 30, 260, 'INTERMEDIATE', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (14, 'אימון מתיחות וגמישות עמוק', 'הארכת שרירים, שיפור טווחי תנועה ושחרור מתחים מהגוף לאחר שבוע אינטנסיבי.', 'WTp7x1yGuuY', 15, 70, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (15, 'אימון בטן אקספרס קריעה', '10 דקות מרוכזות של עבודה רציפה ללא הפסקות על כל שרירי הבטן.', 'ijOjtOPb3J4', 10, 95, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (16, 'אימון סיבולת שריר גוף מלא', 'סטים ארוכים עם משקל נמוך לעבודה על סיבולת שרירית מוגברת.', 'Sa7SWT3j6Yg', 40, 320, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (17, 'אימון ישבן וירכיים ממוקד', 'תרגילים מבודדים וקשים המיועדים לעיצוב והרמת פלג הגוף התחתון.', 'dUpVYCXzn3I', 20, 170, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (18, 'אימון כוח מתפרץ וקפיצות', 'שיפור הניתור והמהירות בעזרת תרגילי כוח מתפרצים מתקדמים.', 'prhssjQZIQY', 30, 290, 'ADVANCED', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (19, 'אימון ידיים נפח מטורף', 'אימון המוקדש כולו לבייספס וטרייספס בשיטות דרופ-סט מתקדמות.', '336MnBUju94', 35, 280, 'ADVANCED', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (20, 'אימון התאוששות ומוביליטי', 'אימון תנועתיות קל לשחרור מפרקים ושיקום השרירים בין אימונים עצימים.', 'cDcNoFb0VBQ', 20, 80, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (21, 'אימון גב וכתפיים עם גומיות התנגדות', 'שימוש ברצועות וגומיות ליצירת מתח שרירי קבוע פנטסטי לפלג הגוף העליון.', 'JQFL_09icI8', 25, 190, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (22, 'אימון Core ויציבה מתקדם', 'תרגילים סטטיים ודינמיים מורכבים המאתגרים את שיווי המשקל והעומק.', 'sy1QW-s-z4Y', 15, 110, 'ADVANCED', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (23, 'אימון ריצה ואינטרוולים דופק שיא', 'שילוב של ספרינטים ותנועה פונקציונלית באוויר הפתוח לשריפת שומן מקסימלית.', 'kJ2DrnfLS2Y', 30, 410, 'INTERMEDIATE', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (24, 'אימון חזה עצים WITH דאמבלס', 'לחיצות חזה ופרפר בזוויות משתנות לעיצוב חזה רחב וחזק.', 'hMGfDKwUcPI', 35, 290, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (25, 'אימון מתיחות ושחרור בוקר', 'פתיחת טווחי תנועה, הזרמת דם והכנת הגוף בצורה אופטימלית להמשך היום.', 'asxhMPbPCok', 12, 50, 'BEGINNER', 'HOME');


-- 6. טבלאות קישור (Many-to-Many) - התאמות מורחבות ועשירות ביותר!
-- אימון 1: פלג גוף עליון (חזה וידיים + גב וכתפיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (1, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (1, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (1, 4);

-- אימון 2: אירובי ושריפת שומן (משפיע על כל הגוף וליבה)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (2, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 5);

-- אימון 3: רגליים וישבן (+ מזרן יוגה)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (3, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (3, 2);

-- אימון 4: בטן וליבה דינמי (+ מזרן יוגה)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (4, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (4, 1);

-- אימון 5: גוף מלא פונקציונלי (מכסה הכל)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (5, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 5);

-- אימון 6: חזה וידיים בחד"כ
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (6, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (6, 3);

-- אימון 7: גב ויד אחורית בחד"כ
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (7, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (7, 4);

-- אימון 8: כתפיים (גב וכתפיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (8, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (8, 4);

-- אימון 9: רגליים כבד בחד"כ
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (9, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (9, 2);

-- אימון 10: HIIT ביתי (עובד על כל קבוצות השרירים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (10, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 5);

-- אימון 11: פלג גוף עליון בבית (חזה, ידיים, גב וכתפיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (11, 1);
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (11, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (11, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (11, 4);

-- אימון 12: גוף מלא אקטיבי בחוץ
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (12, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 5);

-- אימון 13: כוח פונקציונלי בחוץ (רגליים, בטן, ידיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (13, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (13, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (13, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (13, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (13, 5);

-- אימון 14: מתיחות וגמישות (משפיע על כל השרירים ומרגיע)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (14, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (14, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (14, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (14, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (14, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (14, 5);

-- אימון 15: בטן אקספרס קריעה
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (15, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (15, 1);

-- אימון 16: סיבולת שריר גוף מלא בחד"כ
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (16, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (16, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (16, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (16, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (16, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (16, 5);

-- אימון 17: ישבן וירכיים (רגליים וישבן)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (17, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (17, 2);

-- אימון 18: כוח מתפרץ (רגליים וליבה בחוץ)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (18, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (18, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (18, 2);

-- אימון 19: ידיים (חזה וידיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (19, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (19, 3);

-- אימון 20: התאוששות ומוביליטי (משפיע על כל הגוף אקטיבית)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (20, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (20, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (20, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (20, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (20, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (20, 5);

-- אימון 21: גב וכתפיים עם גומיות התנגדות
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (21, 2);
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (21, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (21, 4);

-- אימון 22: Core ויציבה (בטן וליבה ברמה גבוהה)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (22, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (22, 1);

-- אימון 23: ריצה ואינטרוולים (עובד חזק על רגליים וגוף מלא)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (23, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (23, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (23, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (23, 5);

-- אימון 24: חזה עצים עם דאמבלס
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (24, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (24, 3);

-- אימון 25: מתיחות בוקר (עדין לכל הגוף)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (25, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (25, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (25, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (25, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (25, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (25, 5);


-- 7. איתחול סדרת ה-IDENTITY למניעת התנגשויות מפתחות (חובה!)
ALTER TABLE equipment ALTER COLUMN id RESTART WITH 5;
ALTER TABLE body_parts ALTER COLUMN id RESTART WITH 6;
ALTER TABLE nutrition_tips ALTER COLUMN id RESTART WITH 4;
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
ALTER TABLE workouts ALTER COLUMN id RESTART WITH 26;