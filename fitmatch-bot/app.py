import os
import json
from flask import Flask, request, jsonify
from openai import OpenAI

from bot.config import OPENAI_API_KEY, SYSTEM_PROMPT
from bot.skills import calculate_body_metrics, search_workouts, get_nutrition_tips

app = Flask(__name__)
client = OpenAI(api_key=OPENAI_API_KEY)

tools = [
    {
        "type": "function",
        "function": {
            "name": "calculate_body_metrics",
            "description": "Calculates basic body metrics such as BMI and recommended daily calorie intake based on weight, height, age, and gender.",
            "parameters": {
                "type": "object",
                "properties": {
                    "weight": {"type": "number", "description": "Weight in kilograms"},
                    "height_cm": {"type": "number", "description": "Height in centimeters"},
                    "age": {"type": "integer", "description": "Age in years"},
                    "gender": {"type": "string", "description": "Gender, 'male' or 'female'"}
                },
                "required": ["weight", "height_cm", "age", "gender"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "search_workouts",
            "description": "Searches and filters fitness workouts from the database by attributes like difficulty level, exact duration in minutes, or location.",
            "parameters": {
                "type": "object",
                "properties": {
                    "difficulty_level": {"type": "string", "enum": ["BEGINNER", "INTERMEDIATE", "ADVANCED"]},
                    "duration_minutes": {"type": "integer", "description": "Workout duration in minutes"},
                    "location": {"type": "string", "enum": ["HOME", "GYM", "OUTDOOR"]}
                },
                "required": []
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "get_nutrition_tips",
            "description": "Retrieves the complete list of nutrition guidelines, calorie thresholds, food and water recommendations from the official database.",
            "parameters": {
                "type": "object",
                "properties": {},
                "required": []
            }
        }
    }
]

@app.route('/api/chat', methods=['POST'])
def chat_endpoint():
    try:
        data = request.get_json() or {}
        user_message = data.get("message", "")
        client_history = data.get("history", [])
        
        messages = [{"role": "system", "content": SYSTEM_PROMPT}]
        
        # בניית היסטוריית שיחה נקייה ותקנית
        for msg in client_history:
            messages.append({
                "role": msg.get("role", "user"),
                "content": msg.get("content", "")
            })
            
        messages.append({"role": "user", "content": user_message})
        
        # קריאה ראשונה ל-LLM לבדיקה האם נדרש כלי (Tool)
        response = client.chat.completions.create(
            model="gpt-4o-mini",
            messages=messages,
            tools=tools,
            temperature=0.2
        )
        
        response_message = response.choices[0].message
        
        if response_message.tool_calls:
            tool_call = response_message.tool_calls[0]
            function_name = tool_call.function.name
            args = json.loads(tool_call.function.arguments)
            
            result = None
            if function_name == "calculate_body_metrics":
                result = calculate_body_metrics(
                    weight=float(args.get("weight")),
                    height_cm=float(args.get("height_cm")),
                    age=int(args.get("age")),
                    gender=args.get("gender")
                )
            elif function_name == "search_workouts":
                result = search_workouts(
                    difficulty_level=args.get("difficulty_level"),
                    duration_minutes=args.get("duration_minutes"),
                    location=args.get("location")
                )
            elif function_name == "get_nutrition_tips":
                result = get_nutrition_tips()
            
            if result is not None:
                messages.append(response_message)
                messages.append({
                    "role": "tool",
                    "tool_call_id": tool_call.id,
                    "name": function_name,
                    "content": json.dumps(result, ensure_ascii=False)
                })
                
                # קריאה שנייה לקבלת התשובה המנוסחת היטב על בסיס נתוני האמת
                second_response = client.chat.completions.create(
                    model="gpt-4o-mini",
                    messages=messages,
                    temperature=0.3
                )
                return jsonify({"response": second_response.choices[0].message.content})
        
        return jsonify({"response": response_message.content})
        
    except Exception as e:
        print(f"❌ Error in Python server: {str(e)}")
        return jsonify({"error": f"Internal Bot Server Error: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)