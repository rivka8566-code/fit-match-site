import os
import json
from bot.config import DATA_DIR

def calculate_body_metrics(weight: float, height_cm: float, age: int, gender: str) -> dict:
    """
    Calculates basic body metrics including Body Mass Index (BMI) and Basal Metabolic Rate (BMR)
    based on the user's weight, height, age, and gender.
    
    Args:
        weight: The user's weight in kilograms (kg).
        height_cm: The user's height in centimeters (cm).
        age: The user's age in years.
        gender: The user's gender ('male' or 'female').
    """
    height_m = height_cm / 100.0
    bmi = weight / (height_m ** 2)
    
    if gender.lower() in ['male', 'm', 'גבר', 'זכר']:
        bmr = (10 * weight) + (6.25 * height_cm) - (5 * age) + 5
    elif gender.lower() in ['female', 'f', 'אישה', 'נקבה']:
        bmr = (10 * weight) + (6.25 * height_cm) - (5 * age) - 161
    else:
        bmr = (10 * weight) + (6.25 * height_cm) - (5 * age) - 161
        
    tdee = bmr * 1.375
    
    if bmi < 18.5: status = "תת-משקל"
    elif bmi < 25: status = "משקל תקין ומאוזן"
    elif bmi < 30: status = "עודף משקל קל"
    else: status = "השמנת יתר"
        
    return {
        "bmi": round(bmi, 1),
        "bmi_status": status,
        "recommended_daily_calories": int(tdee)
    }



def search_workouts(difficulty_level: str = None, duration_minutes: int = None, location: str = None) -> list:
    """
    Searches and filters official workouts from the database based on strict parameters.
    
    Args:
        difficulty_level: The required difficulty level ('BEGINNER', 'INTERMEDIATE', 'ADVANCED').
        duration_minutes: The exact duration required in minutes (e.g., 15, 20, 30, 45).
        location: The training location ('HOME', 'GYM', 'OUTDOOR').
    """
    print(f"🔍 Executing strict workout filter: difficulty={difficulty_level}, duration={duration_minutes}, location={location}")
    
    json_path = os.path.join(DATA_DIR, "workouts_raw.json")
    if not os.path.exists(json_path):
        return []
        
    with open(json_path, "r", encoding="utf-8") as f:
        workouts = json.load(f)
        
    filtered_results = []
    
    for w in workouts:
        if difficulty_level and w.get('difficultyLevel', '').upper() != difficulty_level.upper():
            continue
            
        if duration_minutes:
            w_duration = w.get('durationMinutes') or w.get('duration', 0)
            if w_duration != duration_minutes:
                continue
                
        if location and w.get('location', '').upper() != location.upper():
            continue
            
        filtered_results.append({
            "name": w.get('name') or w.get('title'),
            "description": w.get('description'),
            "difficultyLevel": w.get('difficultyLevel'),
            "durationMinutes": w.get('durationMinutes') or w.get('duration'),
            "caloriesBurned": w.get('caloriesBurned'),
            "location": w.get('location'),
            "foodRecommendation": w.get('foodRecommendation'),
            "waterRecommendation": w.get('waterRecommendation')
        })
        
    return filtered_results