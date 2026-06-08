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

-- 3. הכנסת סרטוני אימון (טבלת workouts)
-- אימון 1: מתחילים, בבית
INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (1, 'אימון בטן קורע ב-15 דקות', 'אימון קצר וממוקד לשרירי הליבה ללא צורך בציוד מורכב', 'dQw4w9WgXcQ', 15, 120, 'BEGINNER', 'HOME');

-- אימון 2: אלוף (מתקדם), חדר כושר
INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (2, 'אימון רגליים עצים עם משקולות', 'בניית כוח וסיבולת לשרירי הרגליים והישבן באמצעות משקולות יד', 'X_9V_e4A880', 45, 350, 'ADVANCED', 'GYM');

-- אימון 3: בינוני, בבית
INSERT INTO workouts (id, title, description, youtube_url, duration_minutes, calories_burned, difficulty_level, location) 
VALUES (3, 'אימון פלג גוף עליון משקל גוף', 'אימון זורם לעיצוב וחיזוק החזה, הידיים והגב ללא משקולות', 'w86S_GgWfAI', 30, 220, 'INTERMEDIATE', 'HOME');

-- 4. חיבור הציוד והשרירים לאימונים בטבלאות הקישור (Many-to-Many)

-- חיבורים לאימון 1 (אימון בטן צריך מזרן יוגה ומכוון לבטן וליבה)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (1, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (1, 1);

-- חיבורים לאימון 2 (אימון רגליים צריך משקולות ומזרן, ומכוון לרגליים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (2, 1);
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (2, 2);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (2, 2);

-- חיבורים לאימון 3 (אימון פלג גוף עליון הוא ללא ציוד, ומכוון לחזה וידיים + גב וכתפיים)
INSERT INTO workout_equipment (workout_id, equipment_id) VALUES (3, 4);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (3, 3);
INSERT INTO workout_body_parts (workout_id, body_part_id) VALUES (3, 4);

-- 5. הכנסת חוקי הטיפים התזונתיים (טבלת nutrition_tips)
-- חוק 1: לטווח קלוריות של אימון קל (0 עד 150 קלוריות)
INSERT INTO nutrition_tips (id, min_calories_threshold, max_calories_threshold, food_recommendation, water_recommendation)
VALUES (1, 0, 150, 'אימון קל וקצר! תמר אחד או חופן שקדים קטן יספיקו כדי להחזיר אנרגיה זמינה לגוף מבלי להכביד על מערכת העיכול.', 'זכור לשתות כוס מים אחת מיד בתום התרגיל.');

-- חוק 2: לטווח קלוריות של אימון בינוני/כבד (151 עד 400 קלוריות)
INSERT INTO nutrition_tips (id, min_calories_threshold, max_calories_threshold, food_recommendation, water_recommendation)
VALUES (2, 151, 400, 'עבודה מעולה! הגוף שרף לא מעט אנרגיה. מומלץ לשלב חלבון ופחמימה מורכבת בטווח של שעה מהאימון: למשל גביע יוגורט חלבון עם גרנולה או כריך טונה מלחם מלא.', 'הזעת כהלכה! משימה לשעה הקרובה: לשתות לפחות 2-3 כוסות מים מלאות.');דדד