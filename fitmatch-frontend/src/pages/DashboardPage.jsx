import { useState, useEffect, useCallback } from 'react';
import { useNavigate, useLocation } from 'react-router-dom';
import { getActiveProgram, getUserById, getAllUserPrograms } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import ProgressBar from '../components/ProgressBar';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const DIFFICULTY_LABELS = { BEGINNER: 'מתחיל', INTERMEDIATE: 'בינוני', ADVANCED: 'מתקדם' };
const DIFF_COLORS = { BEGINNER: 'badge-green', INTERMEDIATE: 'badge-yellow', ADVANCED: 'badge-red' };

export default function DashboardPage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const { toast, showToast, hideToast } = useToast();
  const [program, setProgram] = useState(null);
  const [userProfile, setUserProfile] = useState(null);
  const [hasFuture, setHasFuture] = useState(false);
  const [programState, setProgramState] = useState('loading'); // 'loading' | 'none' | 'completed' | 'active'

  const loadData = useCallback(async () => {
    try {
      const [userRes, allProgsRes] = await Promise.all([
        getUserById(user.id),
        getAllUserPrograms(user.id),
      ]);
      setUserProfile(userRes.data);
      const allProgs = allProgsRes.data;
      const requestedId = location.state?.programId;
      if (requestedId) {
        const matched = allProgs.find(p => p.id === requestedId);
        if (matched) {
          setProgram(matched);
          setProgramState(matched.status === 'ACTIVE' ? 'active' : matched.status === 'COMPLETED' ? 'completed' : 'none');
          return;
        }
      }
      const active = allProgs.find(p => p.status === 'ACTIVE');
      const future = allProgs.find(p => p.status === 'FUTURE');
      setHasFuture(!!future);

      if (active) {
        const progRes = await getActiveProgram(user.id);
        setProgram(progRes.data);
        setProgramState('active');
      } else if (allProgs.some(p => p.status === 'COMPLETED')) {
        // יש תוכניות שהסתיימו אבל אין פעילה
        const last = allProgs.filter(p => p.status === 'COMPLETED').at(-1);
        setProgram(last);
        setProgramState('completed');
      } else {
        setProgramState('none');
      }
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בטעינת הנתונים');
      setProgramState('none');
    }
  }, [user.id, showToast]);

  useEffect(() => { loadData(); }, [loadData]);

  // Support opening dashboard showing a specific program (from Profile click)
  const { state } = location;
  useEffect(() => {
    if (state && state.programId) {
      // reload to pick that program from user's programs on next load
      loadData();
    }
  }, [state, loadData]);

  if (programState === 'loading') return <><Navbar /><div className="loading">טוען...</div></>;

  // מצב: אין תוכנית בכלל
  if (programState === 'none') {
    return (
      <>
        <Navbar />
        <div className="dashboard-page">
          <div className="dashboard-header">
            <div className="header-text">
              <h1>שלום, {userProfile?.fullName}</h1>
            </div>
          </div>
          <div className="empty-dashboard">
            <div className="empty-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <path d="M12 2C6.48 2 2 6.48 2 12s4.48 10 10 10 10-4.48 10-10S17.52 2 12 2z" />
                <path d="M12 8v4m0 4h.01" />
              </svg>
            </div>
            <h2>עדיין אין לך תוכנית אימונים</h2>
            <p>מלא את השאלון הקצר ונבנה עבורך תוכנית מותאמת אישית</p>
            <button className="btn-primary" style={{ width: 'auto', padding: '0.8rem 2rem' }}
              onClick={() => navigate('/questionnaire')}>
              בנה תוכנית עכשיו
            </button>
          </div>
        </div>
      </>
    );
  }

  // מצב: תוכנית הושלמה ואין פעילה
  if (programState === 'completed') {
    return (
      <>
        <Navbar />
        <div className="dashboard-page">
          <div className="dashboard-header">
            <div className="header-text">
              <h1>שלום, {userProfile?.fullName}</h1>
              <p>סיימת את המסלול — עבודה מדהימה!</p>
            </div>
          </div>
          <div className="completed-dashboard">
            <div className="completed-icon">
              <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="1.5">
                <circle cx="12" cy="12" r="10" />
                <polyline points="8 12 11 15 16 9" />
              </svg>
            </div>
            <h2>סיימת את התוכנית!</h2>
            <p>שרפת <strong>{userProfile?.totalCaloriesBurned?.toLocaleString()} קל'</strong> סה"כ. הגיע הזמן להמשיך קדימה.</p>
            {hasFuture ? (
              <div className="future-notice">
                <span>יש לך תוכנית עתידית שממתינה.</span>
                <a href="/profile" style={{color:'var(--blue)',marginRight:'0.5rem'}}>הפעל אותה בפרופיל &rarr;</a>
              </div>
            ) : (
              <button className="btn-primary" style={{ width: 'auto', padding: '0.8rem 2rem' }}
                onClick={() => navigate('/questionnaire')}>
                התחל תוכנית חדשה
              </button>
            )}
          </div>
        </div>
      </>
    );
  }

  // מצב רגיל: יש תוכנית פעילה
  const workouts = program?.workouts || [];
  const currentIndex = workouts.findIndex(w => !w.completed);
  const daysPerWeek = program?.daysPerWeekTarget || 1;
  const isSaturday = new Date().getDay() === 6;
  const startingNextWeek = currentIndex > 0 && currentIndex % daysPerWeek === 0;
  const nextWeekLocked = startingNextWeek && !isSaturday;
  const nextWeekEnd = Math.min(currentIndex + daysPerWeek, workouts.length);

  return (
    <>
      <Navbar />
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="dashboard-page">
        <div className="dashboard-header">
          <div className="header-text">
            <h1>שלום, {userProfile?.fullName}</h1>
            <p>המשך לשרוף — אתה במסלול הנכון</p>
          </div>
          <div className="header-stats">
            <div className="stat-pill">
              <span className="stat-num">{workouts.filter(w => w.completed).length}</span>
              <span>הושלמו</span>
            </div>
            <div className="stat-pill">
              <span className="stat-num">{workouts.length - workouts.filter(w => w.completed).length}</span>
              <span>נותרו</span>
            </div>
          </div>
        </div>

        <ProgressBar
          burned={program.burnedCaloriesInProgram}
          target={program.totalTargetCalories}
        />

        {nextWeekLocked && (
          <div className="week-full-notice">
            השלמת את מכסת האימונים לשבוע זה. המתן עד מוצ"ש כדי להתחיל את השבוע הבא.
          </div>
        )}

        <div className="add-future-bar">
          <span>{hasFuture ? 'יש לך כבר תוכנית עתידית. ניתן להוסיף עוד תוכנית נוספת.' : 'רוצה לתכנן את התוכנית הבאה מראש?'}</span>
          <button className="btn-secondary" onClick={() => navigate('/questionnaire')}>
            הוסף תוכנית עתידית
          </button>
        </div>

        <div className="trail-section">
          <h2 className="trail-title">מסלול האימונים</h2>

          <div className="trail-container">
            {workouts.map((workout, index) => {
              const isDone = workout.completed;
              const isCurrent = index === currentIndex && !nextWeekLocked;
              const isBlockedByWeek = !isDone && nextWeekLocked && index >= currentIndex && index < nextWeekEnd;
              const isFuture = !isDone && !isCurrent;
              const weekNum = Math.floor(index / daysPerWeek) + 1;
              const showWeekLabel = index % daysPerWeek === 0;

              return (
                <div key={`${workout.id}-${index}`}>
                  {showWeekLabel && (
                    <div className="week-label">שבוע {weekNum}</div>
                  )}
                  <div className={`trail-item ${index % 2 === 0 ? 'left' : 'right'}`}>
                    {index < workouts.length - 1 && (
                      <div className={`trail-connector ${isDone ? 'done' : ''}`} />
                    )}

                    <div
                      className={`trail-node ${isDone ? 'node-done' : isCurrent ? 'node-current' : 'node-future'}`}
                      onClick={() => {
                        if (isDone || isCurrent) {
                          navigate(`/workout/${workout.id}`, {
                            state: { workout, programId: program.id, workoutIndex: index, fromExplore: false },
                          });
                        } else if (isBlockedByWeek) {
                          showToast('השלמת את מכסת האימונים לשבוע זה. המתן לשבוע הבא!');
                        }
                      }}
                    >
                      <div className="node-number">
                        {isDone ? (
                          <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="3">
                            <polyline points="20 6 9 17 4 12" />
                          </svg>
                        ) : (
                          <span>{index + 1}</span>
                        )}
                      </div>

                      <div className="node-content">
                        <div className="node-title">{workout.title}</div>
                        <div className="node-meta">
                          <span>{workout.durationMinutes} דק'</span>
                          <span>{workout.caloriesBurned} קל'</span>
                          <span className={`badge ${DIFF_COLORS[workout.difficultyLevel]}`}>
                            {DIFFICULTY_LABELS[workout.difficultyLevel]}
                          </span>
                        </div>
                      </div>

                      {isCurrent && <div className="node-current-label">עכשיו</div>}
                      {isFuture && !isBlockedByWeek && <div className="node-lock">—</div>}
                      {isBlockedByWeek && <div className="node-week-block">שבוע הבא</div>}
                    </div>
                  </div>
                </div>
              );
            })}

            {workouts.every(w => w.completed) && (
              <div className="trail-finish">
                <div className="finish-badge">
                  <svg viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2">
                    <circle cx="12" cy="12" r="10" />
                    <polyline points="8 12 11 15 16 9" />
                  </svg>
                  <span>תוכנית הושלמה!</span>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </>
  );
}
