import { useState } from 'react';
import { swapWorkout, addCaloriesToUser, completeWorkoutInProgram } from '../services/api';

const DIFFICULTY_LABELS = { BEGINNER: 'מתחיל', INTERMEDIATE: 'בינוני', ADVANCED: 'מתקדם' };
const LOCATION_LABELS = { HOME: 'בית', GYM: 'חדר כושר', OUTDOOR: 'בחוץ' };
const DIFFICULTY_COLORS = { BEGINNER: 'badge-green', INTERMEDIATE: 'badge-yellow', ADVANCED: 'badge-red' };

export default function WorkoutCard({ workout, programId, userId, onComplete, onSwapped, showActions = true }) {
  const [loading, setLoading] = useState(false);

  const getYouTubeId = (url) => {
    if (!url) return null;
    const match = url.match(/(?:v=|youtu\.be\/)([^&?/]+)/);
    return match ? match[1] : url;
  };

  const handleComplete = async () => {
    setLoading(true);
    try {
      const res = await completeWorkoutInProgram(programId, workout.id, workout.sequence);
      onComplete(res.data);
    } catch (e) {
      alert(e.response?.data || 'שגיאה בסיום אימון');
    } finally {
      setLoading(false);
    }
  };

  const handleSwap = async () => {
    setLoading(true);
    try {
      const res = await swapWorkout(programId, workout.id);
      onSwapped(workout.id, res.data);
    } catch (e) {
      alert(e.response?.data || 'לא נמצא אימון חלופי');
    } finally {
      setLoading(false);
    }
  };

  const videoId = getYouTubeId(workout.youtubeUrl);

  return (
    <div className="workout-card">
      <div className="workout-card-header">
        <h3>{workout.title}</h3>
        <div className="workout-badges">
          <span className={`badge ${DIFFICULTY_COLORS[workout.difficultyLevel]}`}>
            {DIFFICULTY_LABELS[workout.difficultyLevel]}
          </span>
          <span className="badge badge-blue">{LOCATION_LABELS[workout.location]}</span>
        </div>
      </div>

      {videoId && (
        <div className="youtube-embed">
          <iframe
            src={`https://www.youtube.com/embed/${videoId}`}
            title={workout.title}
            allowFullScreen
          />
        </div>
      )}

      <div className="workout-meta">
        <span>⏱ {workout.durationMinutes} דק'</span>
        <span>🔥 {workout.caloriesBurned} קל'</span>
      </div>

      {workout.foodRecommendation && (
        <div className="nutrition-tip">
          <p>🥗 {workout.foodRecommendation}</p>
          <p>💧 {workout.waterRecommendation}</p>
        </div>
      )}

      {showActions && (
        <div className="workout-actions">
          <button className="btn-complete" onClick={handleComplete} disabled={loading}>
            ✅ סיימתי!
          </button>
          <button className="btn-swap" onClick={handleSwap} disabled={loading}>
            🔄 החלף
          </button>
        </div>
      )}
    </div>
  );
}
