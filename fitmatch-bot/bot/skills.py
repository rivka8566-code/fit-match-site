import os
import requests
from bot.config import JAVA_BACKEND_URL

def calculate_body_metrics(weight: float, height_cm: float, age: int, gender: str) -> dict:
    """
    Calculates basic body metrics including Body Mass Index (BMI) and Basal Metabolic Rate (BMR).
    """
    height_m = height_cm / 100.0
    bmi = weight / (height_m ** 2)
    
    if gender.lower() in ['male', 'm', 'גבר', 'זכר']:
        bmr = (10 * weight) + (6.25 * height_cm) - (5 * age) + 5
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
    Fetches and filters fitness workouts dynamically from the Java Spring Boot Backend.
    """
    print(f"🔍 [Skill] Filtering workouts directly from DB via Java API... params: diff={difficulty_level}, dur={duration_minutes}, loc={location}")
    try:
        response = requests.get(f"{JAVA_BACKEND_URL}/workouts", timeout=5)
        if response.status_code != 200:
            return []
        workouts = response.json()
    except Exception as e:
        print(f"❌ Error communicating with Java Backend: {str(e)}")
        return []
        
    filtered_results = []
    for w in workouts:
        if difficulty_level and w.get('difficultyLevel', '').upper() != difficulty_level.upper():
            continue
            
        if duration_minutes:
            w_duration = w.get('durationMinutes') or w.get('duration', 0)
            if int(w_duration) != int(duration_minutes):
                continue
                
        if location and w.get('location', '').upper() != location.upper():
            continue
            
        filtered_results.append({
            "name": w.get('name') or w.get('title'),
            "description": w.get('description'),
            "difficultyLevel": w.get('difficultyLevel'),
            "durationMinutes": w.get('durationMinutes') or w.get('duration'),
            "caloriesBurned": w.get('caloriesBurned'),
            "location": w.get('location')
        })
        
    return filtered_results

def get_nutrition_tips() -> list:
    """
    Fetches official nutrition tips, thresholds, and water recommendations from the Java database.
    """
    print("🔍 [Skill] Fetching nutrition guidelines directly from DB via Java API...")
    try:
        response = requests.get(f"{JAVA_BACKEND_URL}/nutrition-tips", timeout=5)
        if response.status_code == 200:
            return response.json()
        return []
    except Exception as e:
        print(f"❌ Error fetching nutrition tips: {str(e)}")
        return []