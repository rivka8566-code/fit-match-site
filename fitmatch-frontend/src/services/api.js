import axios from 'axios';

const API = axios.create({ baseURL: 'http://localhost:8080/api' });

// --- Users ---
export const registerUser = (data) => API.post('/users/register', data);
// { email, password, fullName, role? }

export const loginUser = (data) => API.post('/users/login', data);
// { email, password }

export const getUserById = (id) => API.get(`/users/${id}`);

export const addCaloriesToUser = (userId, calories) =>
  API.put(`/users/${userId}/add-calories`, null, { params: { calories } });

export const getAllUsers = () => API.get('/users');

// --- Programs ---
export const createProgram = (questionnaireDTO) => API.post('/programs/create', questionnaireDTO);
// { userId, difficultyLevel, preferredLocations[], daysPerWeek, durationWeeks,
//   preferredStartDate, preferredBodyPartIds[], availableEquipmentIds[] }

export const getActiveProgram = (userId) => API.get(`/programs/active/${userId}`);

export const swapWorkout = (programId, workoutId) =>
  API.post(`/programs/${programId}/swap/${workoutId}`);

export const getAllUserPrograms = (userId) => API.get(`/programs/user/${userId}/all`);

export const activateProgram = (programId) => API.post(`/programs/${programId}/activate`);

// סיום אימון בתוכנית - מחזיר UserProgramDTO מעודכן עם completed flags
export const completeWorkoutInProgram = (programId, workoutId, sequence) =>
  API.post(`/programs/${programId}/complete/${workoutId}`, null, {
    params: sequence != null ? { sequence } : {},
  });

// --- Workouts ---
export const getAllWorkouts = () => API.get('/workouts');

export const searchWorkouts = (difficulty, location) =>
  API.get('/workouts/search', { params: { difficulty, location } });

export const addWorkout = (dto) => API.post('/workouts/add', dto);
// { title, description, youtubeUrl, durationMinutes, caloriesBurned,
//   difficultyLevel, location, equipmentIds[], bodyPartIds[] }

// --- Nutrition Tips ---
export const getAllNutritionTips = () => API.get('/nutrition-tips');

export const addNutritionTip = (tipDTO) => API.post('/nutrition-tips/add', tipDTO);
// { foodRecommendation, waterRecommendation, minCaloriesThreshold, maxCaloriesThreshold }

export const deleteNutritionTip = (id) => API.delete(`/nutrition-tips/${id}`);

export default API;
