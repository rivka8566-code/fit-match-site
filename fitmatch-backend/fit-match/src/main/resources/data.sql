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

-- 4. הכנסת משתמשי בדיקה ראשוניים (טבלת users) כולל עמודת ה-role ה-Enumית
INSERT INTO users (id, email, password, full_name, total_calories_burned, role) 
VALUES (1, 'admin@fitmatch.com', 'admin123', 'מנהל המערכת', 0, 'ADMIN');

INSERT INTO users (id, email, password, full_name, total_calories_burned, role) 
VALUES (2, 'user@fitmatch.com', 'user123', 'ישראל ישראלי', 0, 'USER');

-- 5. מאגר גדול של 12 אימונים (סרטונים פתוחים בנטפרי)
INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (1, 'אימון בטן קורע ב-15 דקות', 'אימון קצר, עצים וממוקד לשרירי הליבה ללא צורך בציוד. מתאים לכל רמה.', 'dQw4w9WgXcQ', 15, 120, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (2, 'אימון פלג גוף עליון משקל גוף', 'חיזוק ועיצוב החזה, הגב והידיים בעזרת תרגילי קליסטניקס בסיסיים.', 'w86S_GgWfAI', 20, 160, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (3, 'אימון אירובי ושריפת שומן ביתי', 'אימון HIIT קצבי להגברת קצב הלב ושריפת קלוריות מקסימלית ללא ציוד.', 'Mvo2V8X3SBY', 30, 280, 'INTERMEDIATE', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (4, 'אימון בטן מתקדם לאלופים', 'תרגילי ליבה מורכבים בטכניקות מתקדמות להשגת קוביות ובטן חזקה.', '4pLUZsP8XgM', 10, 110, 'ADVANCED', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (5, 'אימון גוף מלא אקטיבי עם גומיות', 'שימוש בגומיות התנגדות לעבודה על כל קבוצות השרירים הגדולות בבית.', 'vH0vK8oOqLg', 25, 190, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (6, 'אימון רגליים ביתי ללא משקולות', 'אימון שורף וממוקד לישבן ולירכיים המבוסס על סקוואטים ומכרעים.', 'X_9V_e4A880', 20, 150, 'BEGINNER', 'HOME');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (7, 'אימון רגליים עצים עם משקולות יד', 'בניית כוח וסיבולת לשרירי הרגליים בחדר הכושר באמצעות דאמבלס.', 'f6578XgYhUo', 45, 350, 'ADVANCED', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (8, 'אימון היפרטרופיה חזה וידיים', 'תוכנית עבודה ממוקדת להיפרטרופיה ועיצוב פלג גוף עליון עם משקולות.', 'J21XyP89mKk', 40, 310, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (9, 'אימון גב וכתפיים קלאסי', 'בניית גב רחב וכתפיים חזקות בעזרת עבודה ממוקדת משקולות יד.', 'uNlU8Xy21Oo', 35, 260, 'INTERMEDIATE', 'GYM');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (10, 'אימון קליסטניקס בפארק', 'אימון כוח פונקציונלי בחוץ המבוסס על מתח, מקבילים ושכיבות סמיכה.', 'Z8vK9oP11mX', 30, 240, 'ADVANCED', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (11, 'אימון ריצה ואינטרוולים בחוץ', 'שיפור סיבולת לב ריאה ושריפת קלוריות מוגברת באמצעות ריצה משתנה.', 'N8mU9XxP76a', 40, 420, 'INTERMEDIATE', 'OUTDOOR');

INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (12, 'אימון מתיחות וגמישות בטבע', 'אימון שחרור, הארכת שרירים וגמישות מרגיע לביצוע בפארק או בחוץ.', 'u1X8yPm9KoQ', 15, 70, 'BEGINNER', 'OUTDOOR');


INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (1, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (1, 1);
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (2, 4);
 
-- לאחר הכנסת שורות עם id מפורש עבור טבלאות אלו, יש לאתחל את ערכי ה-IDENTITY
-- כדי שמזהה חדש יתחיל אחרי ה-max הקיים ונמנעו כפילויות בעת INSERT אוטומטי
ALTER TABLE equipment ALTER COLUMN id RESTART WITH 5;
ALTER TABLE body_parts ALTER COLUMN id RESTART WITH 6;
ALTER TABLE nutrition_tips ALTER COLUMN id RESTART WITH 4;
ALTER TABLE users ALTER COLUMN id RESTART WITH 3;
ALTER TABLE workouts ALTER COLUMN id RESTART WITH 13;

-- אימון 3: גוף מלא (5), מזרן (2)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (3, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (3, 5);

-- אימון 4: בטן (1), ללא ציוד (4)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (4, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (4, 1);

-- אימון 5: גוף מלא (5), גומיות (3)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (5, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (5, 5);

-- אימון 6: רגליים (2), ללא ציוד (4)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (6, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (6, 2);

-- אימון 7: רגליים (2), משקולות (1) ומזרן (2)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (7, 1);
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (7, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (7, 2);

-- אימון 8: חזה וידיים (3), משקולות (1)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (8, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (8, 3);

-- אימון 9: גב וכתפיים (4), משקולות (1)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (9, 1);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (9, 4);

-- אימון 10: גב וכתפיים (4) וחזה וידיים (3), ללא ציוד (4)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (10, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (10, 4);

-- אימון 11: גוף מלא (5), ללא ציוד (4)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (11, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (11, 5);

-- אימון 12: גוף מלא (5), מזרן (2)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (12, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (12, 5);