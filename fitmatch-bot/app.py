import os
import json
from flask import Flask, request, jsonify
from openai import OpenAI

from bot.config import OPENAI_API_KEY, SYSTEM_PROMPT
from bot.rag_manager import update_rag_files_from_db, load_rag_context
from bot.skills import calculate_body_metrics, search_workouts

app = Flask(__name__)
client = OpenAI(api_key=OPENAI_API_KEY)

# OpenAI tools configuration translated entirely to English for maximum accuracy
tools = [
    {
        "type": "function",
        "function": {
            "name": "calculate_body_metrics",
            "description": "Calculates basic body metrics such as BMI and recommended daily calorie intake based on weight, height, age, and gender.",
            "parameters": {
                "type": "object",
                "properties": {
                    "weight": {"type": "number", "description": "Weight in kilograms (e.g., 72.5)"},
                    "height_cm": {"type": "number", "description": "Height in centimeters (e.g., 175)"},
                    "age": {"type": "integer", "description": "Age in years (e.g., 24)"},
                    "gender": {"type": "string", "description": "Gender of the user, strictly 'male' or 'female'"}
                },
                "required": ["weight", "height_cm", "age", "gender"]
            }
        }
    },
    {
        "type": "function",
        "function": {
            "name": "search_workouts",
            "description": "Searches and filters fitness workouts from the database by strict attributes like difficulty level, exact duration in minutes, or location.",
            "parameters": {
                "type": "object",
                "properties": {
                    "difficulty_level": {
                        "type": "string", 
                        "enum": ["BEGINNER", "INTERMEDIATE", "ADVANCED"],
                        "description": "The exact difficulty tier required."
                    },
                    "duration_minutes": {
                        "type": "integer", 
                        "description": "The exact workout duration in minutes (e.g., 15, 20, 30, 45, 50)."
                    },
                    "location": {
                        "type": "string", 
                        "enum": ["HOME", "GYM", "OUTDOOR"],
                        "description": "Where the workout takes place."
                    }
                },
                "required": []
            }
        }
    }
]

@app.route('/api/chat', methods=['POST'])
def chat_endpoint():
    try:
        data = request.get_json()
        user_message = data.get("message", "")
        client_history = data.get("history", [])
        
        if "workoutsDB" in data and "tipsDB" in data:
            update_rag_files_from_db(data["workoutsDB"], data["tipsDB"])
            
        rag_context = load_rag_context()
        
        full_user_content = (
            f"=== Updated Context from Database (RAG) ===\n"
            f"{rag_context}\n"
            f"============================================\n\n"
            f"User Message: {user_message}"
        )
        
        messages = [
            {"role": "system", "content": SYSTEM_PROMPT}
        ]
        
        for msg in client_history:
            messages.append({
                "role": msg.get("role", "user"),
                "content": msg.get("content", "")
            })
            
        messages.append({"role": "user", "content": full_user_content})
        
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
            
            if result is not None:
                messages.append(response_message)
                messages.append({
                    "role": "tool",
                    "tool_call_id": tool_call.id,
                    "name": function_name,
                    "content": json.dumps(result, ensure_ascii=False)
                })
                
                second_response = client.chat.completions.create(
                    model="gpt-4o-mini",
                    messages=messages
                )
                return jsonify({"response": second_response.choices[0].message.content})
        
        return jsonify({"response": response_message.content})
        
    except Exception as e:
        print(f"❌ Error in Python server: {str(e)}")
        return jsonify({"error": f"Internal Bot Server Error: {str(e)}"}), 500

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000, debug=True)