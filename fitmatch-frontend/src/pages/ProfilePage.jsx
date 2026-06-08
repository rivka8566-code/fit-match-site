import { useState, useEffect, useCallback } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllUserPrograms, getUserById, activateProgram } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const STATUS_MAP = {
  ACTIVE:    { label: 'פעילה כעת', cls: 'status-active' },
  COMPLETED: { label: 'הושלמה',    cls: 'status-done' },
  FUTURE:    { label: 'עתידית',    cls: 'status-future' },
};

export default function ProfilePage() {
  const { user } = useAuth();
  const [profile, setProfile] = useState(null);
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);
  const [activating, setActivating] = useState(null);
  const { toast, showToast, hideToast } = useToast();

  const load = useCallback(async () => {
    try {
      const [userRes, progsRes] = await Promise.all([
        getUserById(user.id),
        getAllUserPrograms(user.id),
      ]);
      setProfile(userRes.data);
      setPrograms(progsRes.data);
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בטעינת הפרופיל');
    } finally {
      setLoading(false);
    }
  }, [user.id]);

  useEffect(() => { load(); }, [load]);

  const navigate = useNavigate();

  const handleActivate = async (programId) => {
    setActivating(programId);
    try {
      await activateProgram(programId);
      showToast('התוכנית הופעלה בהצלחה!', 'success');
      setLoading(true);
      await load();
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בהפעלת התוכנית');
    } finally {
      setActivating(null);
    }
  };

  if (loading) return <><Navbar /><div className="loading">טוען...</div></>;

  const hasActive = programs.some(p => p.status === 'ACTIVE');

  return (
    <>
      <Navbar />
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="profile-page">
        <div className="profile-card">
          <div className="profile-avatar">
            {profile?.fullName?.charAt(0).toUpperCase()}
          </div>
          <h2>{profile?.fullName}</h2>
          <p className="profile-email">{profile?.email}</p>
          <div className="profile-stats">
            <div className="stat">
              <span className="stat-value">{profile?.totalCaloriesBurned?.toLocaleString()}</span>
              <span className="stat-label">קלוריות נשרפו</span>
            </div>
            <div className="stat">
              <span className="stat-value">{programs.length}</span>
              <span className="stat-label">תוכניות</span>
            </div>
            <div className="stat">
              <span className="stat-value">{programs.filter(p => p.status === 'COMPLETED').length}</span>
              <span className="stat-label">הושלמו</span>
            </div>
          </div>
        </div>

        <div className="history-section">
          <h2>היסטוריית תוכניות</h2>

          <div className="status-legend">
            <span className="legend-item"><span className="legend-dot dot-active" />פעילה</span>
            <span className="legend-item"><span className="legend-dot dot-future" />עתידית</span>
            <span className="legend-item"><span className="legend-dot dot-done" />הושלמה</span>
          </div>

          {programs.length === 0 && (
            <div className="empty-state">
              <p className="empty-state-text">עדיין אין תוכניות.</p>
              <a href="/questionnaire" className="btn-primary"
                style={{ display: 'inline-block', width: 'auto', padding: '0.6rem 1.5rem' }}>
                צור תוכנית
              </a>
            </div>
          )}

          <div className="timeline">
            {programs.map(p => {
              const s = STATUS_MAP[p.status] || STATUS_MAP.FUTURE;
              const completedCount = p.workouts?.filter(w => w.completed).length || 0;
              const totalCount = p.workouts?.length || 0;
              const canActivate = p.status === 'FUTURE';

              return (
                <div key={p.id} className="timeline-item" onClick={() => navigate('/dashboard', { state: { programId: p.id } })} style={{cursor:'pointer'}}>
                  <div className={`timeline-dot-colored ${s.cls}`} />
                  <div className="timeline-content">
                    <div className="timeline-header">
                      <span className={`status-badge ${s.cls}`}>{s.label}</span>
                      <span className="timeline-date">{p.startDate}</span>
                    </div>
                    <div className="timeline-details">
                      <span>{p.durationWeeks} שבועות</span>
                      <span>{p.daysPerWeekTarget} ימים/שבוע</span>
                      <span>יעד: {p.totalTargetCalories?.toLocaleString()} קל'</span>
                      <span>{completedCount}/{totalCount} אימונים</span>
                    </div>

                    {p.status === 'ACTIVE' && totalCount > 0 && (
                      <div className="mini-progress">
                        <div
                          className="mini-progress-fill"
                          style={{ width: `${Math.round((completedCount / totalCount) * 100)}%` }}
                        />
                      </div>
                    )}

                    {canActivate && (
                      <button
                        className="btn-activate"
                        onClick={(e) => { e.stopPropagation(); handleActivate(p.id); }}
                        disabled={activating === p.id}
                      >
                        {activating === p.id ? 'מפעיל...' : 'הפעל תוכנית זו עכשיו'}
                      </button>
                    )}

                    {p.status === 'COMPLETED' && (
                      <button className="btn-activate" onClick={(e) => { e.stopPropagation(); handleActivate(p.id); }}
                        disabled={activating === p.id}>
                        {activating === p.id ? 'טוען...' : 'אני רוצה את זה שוב'}
                      </button>
                    )}
                  </div>
                </div>
              );
            })}
          </div>
        </div>
      </div>
    </>
  );
}
