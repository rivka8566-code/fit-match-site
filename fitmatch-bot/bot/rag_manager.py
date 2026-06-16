import os
import json
from bot.config import DATA_DIR

def update_rag_files_from_db(workouts, tips):
    print(f"📊 סה''ך אימונים שהתקבלו מג'אווה: {len(workouts)}")
    print(f"📊 סה''ך טיפים שהתקבלו מג'אווה: {len(tips)}")
    print("🔄 משחזר ומעדכן את קבצי המידע על בסיס הנתונים החי...")
    
    with open(os.path.join(DATA_DIR, "workouts_raw.json"), "w", encoding="utf-8") as f:
        json.dump(workouts, f, ensure_ascii=False, indent=4)
    
    workouts_text = "מאגר אימוני הכושר הרשמיים המעודכנים באתר Fit-Match:\n\n"
    for w in workouts:
        name = w.get('name') or w.get('title') or 'אימון ללא שם'
        workouts_text += f"- אימון '{name}':\n"
        workouts_text += f"  תיאור: {w.get('description', 'אין תיאור')}\n"
        workouts_text += f"  רמת קושי: {w.get('difficultyLevel', 'לא מוגדרת')}\n"
        workouts_text += f"  משך זמן: {w.get('durationMinutes') or w.get('duration', 0)} דקות\n"
        workouts_text += f"  קלוריות שנשרפות: {w.get('caloriesBurned', 0)}\n"
        workouts_text += f"  מיקום ביצוע: {w.get('location', 'לא מוגדר')}\n\n"
        
    tips_text = "מאגר טיפי תזונה, תפריטים והנחיות שתיית מים כלליות של אתר Fit-Match:\n\n"
    for t in tips:
        min_cal = t.get('minCaloriesThreshold', 0)
        max_cal = t.get('maxCaloriesThreshold', 0)
        tips_text += f"- חוק והמלצה לטווח שריפת קלוריות של {min_cal} עד {max_cal} קלוריות:\n"
        tips_text += f"  המלצת תזונה ואוכל: {t.get('foodRecommendation', 'אין המלצה מוגדרת')}\n"
        tips_text += f"  המלצת שתיית מים הידרציה: {t.get('waterRecommendation', 'אין המלצה מוגדרת')}\n\n"

    with open(os.path.join(DATA_DIR, "workouts_from_db.txt"), "w", encoding="utf-8") as f:
        f.write(workouts_text)
    with open(os.path.join(DATA_DIR, "tips_from_db.txt"), "w", encoding="utf-8") as f:
        f.write(tips_text)
    print("💾 כל הקבצים ומאגרי ה-JSON סונכרנו בהצלחה!")

def load_rag_context():
    context = ""
    workouts_path = os.path.join(DATA_DIR, "workouts_from_db.txt")
    tips_path = os.path.join(DATA_DIR, "tips_from_db.txt")
    
    if os.path.exists(workouts_path):
        with open(workouts_path, "r", encoding="utf-8") as f:
            context += f.read() + "\n"
    if os.path.exists(tips_path):
        with open(tips_path, "r", encoding="utf-8") as f:
            context += f.read() + "\n"
    return context