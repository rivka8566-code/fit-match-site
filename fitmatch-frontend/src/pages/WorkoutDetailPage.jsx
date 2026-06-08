import { useState, useEffect, useRef } from 'react';
import { useParams, useNavigate, useLocation } from 'react-router-dom';
import { completeWorkoutInProgram, swapWorkout, addCaloriesToUser } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import Confetti from '../components/Confetti';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const DIFFICULTY_LABELS = { BEGINNER: 'מתחיל', INTERMEDIATE: 'בינוני', ADVANCED: 'מתקדם' };
const LOCATION_LABELS = { HOME: 'בית', GYM: 'חדר כושר', OUTDOOR: 'בחוץ' };
const DIFF_COLORS = { BEGINNER: 'badge-green', INTERMEDIATE: 'badge-yellow', ADVANCED: 'badge-red' };

// מפתח יחיד ל-localStorage לשמירת cooldowns כמפה { workoutId: endTime }
const cooldownMapKey = (userId) => `fitmatch_cooldowns_user_${userId}`;

const readCooldowns = (userId) => {
  try {
    // first check sessionStorage (ephemeral), then fallback to localStorage
    const sRaw = sessionStorage.getItem(cooldownMapKey(userId));
    if (sRaw) return JSON.parse(sRaw);
    const raw = localStorage.getItem(cooldownMapKey(userId));
    return raw ? JSON.parse(raw) : {};
  } catch (e) {
    return {};
  }
};

const writeCooldown = (userId, workoutId, endTime, { persistent = false } = {}) => {
  try {
    // by default write to sessionStorage to avoid polluting localStorage
    const map = readCooldowns(userId);
    map[workoutId] = endTime;
    const dest = persistent ? localStorage : sessionStorage;
    dest.setItem(cooldownMapKey(userId), JSON.stringify(map));
  } catch (e) { /* ignore storage errors */ }
};

const getCooldownFor = (userId, workoutId) => {
  const map = readCooldowns(userId);
  return map[workoutId] ? parseInt(map[workoutId]) : null;
};

export default function WorkoutDetailPage() {
  const { workoutId } = useParams();
  const navigate = useNavigate();
  const location = useLocation();
  const { user, login } = useAuth();
  const { toast, showToast, hideToast } = useToast();

  const { workout: initialWorkout, programId, workoutIndex, fromExplore } = location.state || {};
  const [workout, setWorkout] = useState(initialWorkout);
  const [showConfetti, setShowConfetti] = useState(false);
  const [loading, setLoading] = useState(false);
  const [isDone, setIsDone] = useState(initialWorkout?.completed || false);

  const cooldownMs = (workout?.durationMinutes || 0) * 60 * 1000;
  const [cooldownLeft, setCooldownLeft] = useState(0);
  const timerRef = useRef(null);

  useEffect(() => {
    if (!initialWorkout) { navigate(-1); return; }
  }, [initialWorkout, navigate]);

  useEffect(() => {
    if (!fromExplore || !workout) return;
    const storedEnd = getCooldownFor(user.id, workout.id);
    if (storedEnd) {
      const remaining = parseInt(storedEnd) - Date.now();
      if (remaining > 0) setCooldownLeft(remaining);
    }
  }, [fromExplore, workout, user.id]);

  // טיימר countdown
  useEffect(() => {
    if (cooldownLeft <= 0) { clearInterval(timerRef.current); return; }
    timerRef.current = setInterval(() => {
      setCooldownLeft(prev => {
        if (prev <= 1000) { clearInterval(timerRef.current); return 0; }
        return prev - 1000;
      });
    }, 1000);
    return () => clearInterval(timerRef.current);
  }, [cooldownLeft]);

  if (!workout) return null;

  const getYouTubeId = (url) => {
    if (!url) return null;
    const match = url.match(/(?:v=|youtu\.be\/)([^&?/]+)/);
    return match ? match[1] : url;
  };

  const formatTime = (ms) => {
    const mins = Math.floor(ms / 60000);
    const secs = Math.floor((ms % 60000) / 1000);
    return `${mins}:${secs.toString().padStart(2, '0')}`;
  };

  const handleComplete = async () => {
    setLoading(true);
    try {
      if (fromExplore) {
        const res = await addCaloriesToUser(user.id, workout.caloriesBurned);
        login(res.data);
        const endTime = Date.now() + cooldownMs;
        writeCooldown(user.id, workout.id, endTime);
        setCooldownLeft(cooldownMs);
      } else {
        const res = await completeWorkoutInProgram(programId, workout.id, workoutIndex);
        const updatedWorkout = typeof workoutIndex === 'number'
          ? res.data.workouts?.[workoutIndex] || { ...workout, completed: true }
          : res.data.workouts?.find(w => w.id === workout.id) || { ...workout, completed: true };
        setWorkout(updatedWorkout);
        setIsDone(true);
        setShowConfetti(true);
      }
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בסיום האימון');
    } finally {
      setLoading(false);
    }
  };

  const handleSwap = async () => {
    if (!programId) return;
    setLoading(true);
    try {
      const res = await swapWorkout(programId, workout.id);
      setWorkout(res.data);
      showToast('האימון הוחלף בהצלחה', 'success');
    } catch (err) {
      showToast(err.response?.data || 'לא נמצא אימון חלופי מתאים');
    } finally {
      setLoading(false);
    }
  };

  const videoId = getYouTubeId(workout.youtubeUrl);
  const canCompleteFromExplore = fromExplore && cooldownLeft <= 0;
  const returnPath = fromExplore ? '/explore' : '/dashboard';

  return (
    <>
      <Navbar />
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}
      {showConfetti && <Confetti onDone={() => setShowConfetti(false)} />}

      <div className="workout-detail-page">
        <button className="back-btn" onClick={() => navigate(-1)}>← חזור</button>

        <div className="workout-detail-card">
          <div className="workout-detail-header">
            <h1>{workout.title}</h1>
            <div className="workout-badges">
              <span className={`badge ${DIFF_COLORS[workout.difficultyLevel]}`}>
                {DIFFICULTY_LABELS[workout.difficultyLevel]}
              </span>
              <span className="badge badge-blue">{LOCATION_LABELS[workout.location]}</span>
            </div>
          </div>

          <div className="workout-meta-row">
            <div className="meta-item">
              <span className="meta-label">משך האימון</span>
              <span className="meta-value">{workout.durationMinutes} דקות</span>
            </div>
            <div className="meta-item">
              <span className="meta-label">קלוריות לשריפה</span>
              <span className="meta-value">{workout.caloriesBurned} קל'</span>
            </div>
            {fromExplore && (
              <div className="meta-item">
                <span className="meta-label">מקור</span>
                <span className="meta-value">חופשי</span>
              </div>
            )}
          </div>

          {!fromExplore && programId && (
            <div className="swap-bar">
              <span>לא מתאים לך האימון הזה?</span>
              <button className="btn-swap-detail" onClick={handleSwap} disabled={loading || isDone}>
                החלף אימון
              </button>
            </div>
          )}

          {videoId && (
            <div className="youtube-embed-large">
              <iframe
                src={`https://www.youtube.com/embed/${videoId}`}
                title={workout.title}
                allowFullScreen
              />
            </div>
          )}

          {(workout.foodRecommendation || workout.waterRecommendation) && (
            <div className="nutrition-section">
              <h3>המלצות תזונה לאחר האימון</h3>
              <div className="nutrition-cards">
                {workout.foodRecommendation && (
                  <div className="nutrition-card">
                    <div className="nutrition-icon">🥗</div>
                    <div>
                      <div className="nutrition-title">המלצת ארוחה</div>
                      <p>{workout.foodRecommendation}</p>
                    </div>
                  </div>
                )}
                {workout.waterRecommendation && (
                  <div className="nutrition-card">
                    <div className="nutrition-icon">💧</div>
                    <div>
                      <div className="nutrition-title">שתייה</div>
                      <p>{workout.waterRecommendation}</p>
                    </div>
                  </div>
                )}
              </div>
            </div>
          )}

          {/* כפתור סיום - התנהגות שונה לפי מקור */}
          {!fromExplore ? (
            // מגיע מ-Dashboard / תוכנית
            isDone ? (
              <>
                <div className="completed-banner">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" width="18" height="18">
                    <polyline points="20 6 9 17 4 12" />
                  </svg>
                  <span>הושלם — כל הכבוד!</span>
                </div>
              </>
            ) : (
              <button className="btn-complete-large" onClick={handleComplete} disabled={loading}>
                {loading ? 'שומר...' : 'סיימתי את האימון'}
              </button>
            )
          ) : (
            // מגיע מ-Explore - cooldown
            cooldownLeft > 0 ? (
              <div className="cooldown-bar">
                <div className="cooldown-info">
                  <span>שרפת {workout.caloriesBurned} קל' — ניתן לעשות שוב בעוד:</span>
                  <span className="cooldown-timer">{formatTime(cooldownLeft)}</span>
                </div>
                <div className="cooldown-track">
                  <div
                    className="cooldown-fill"
                    style={{ width: `${100 - (cooldownLeft / cooldownMs) * 100}%` }}
                  />
                </div>
              </div>
            ) : (
              <button className="btn-complete-large" onClick={handleComplete} disabled={loading}>
                {loading ? 'שומר...' : 'סיימתי את האימון'}
              </button>
            )
          )}
        </div>
      </div>
    </>
  );
}
