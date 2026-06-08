import { useState, useEffect } from 'react';
import {
  getAllUsers, getAllNutritionTips, addNutritionTip, deleteNutritionTip,
  getAllWorkouts, addWorkout,
} from '../services/api';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const EMPTY_TIP = {
  foodRecommendation: '', waterRecommendation: '',
  minCaloriesThreshold: 0, maxCaloriesThreshold: 500,
};

const EMPTY_WORKOUT = {
  title: '', description: '', youtubeUrl: '',
  durationMinutes: 30, caloriesBurned: 200,
  difficultyLevel: 'BEGINNER', location: 'HOME',
  bodyPartIds: [], equipmentIds: [],
};

const BODY_PARTS = [
  { id: 1, label: 'בטן וליבה' }, { id: 2, label: 'רגליים וישבן' },
  { id: 3, label: 'חזה וידיים' }, { id: 4, label: 'גב וכתפיים' },
];
const EQUIPMENT = [
  { id: 1, label: 'משקולות יד' }, { id: 2, label: 'מזרן יוגה' },
  { id: 3, label: 'גומיות התנגדות' }, { id: 4, label: 'ללא ציוד' },
];

export default function AdminPage() {
  const [users, setUsers] = useState([]);
  const [tips, setTips] = useState([]);
  const [workouts, setWorkouts] = useState([]);
  const [showTipForm, setShowTipForm] = useState(false);
  const [showWorkoutForm, setShowWorkoutForm] = useState(false);
  const [newTip, setNewTip] = useState(EMPTY_TIP);
  const [newWorkout, setNewWorkout] = useState(EMPTY_WORKOUT);
  const [loading, setLoading] = useState(true);
  const { toast, showToast, hideToast } = useToast();

  const loadAll = async () => {
    try {
      const [usersRes, tipsRes, workoutsRes] = await Promise.all([
        getAllUsers(), getAllNutritionTips(), getAllWorkouts(),
      ]);
      setUsers(usersRes.data);
      setTips(tipsRes.data);
      setWorkouts(workoutsRes.data);
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בטעינת נתונים');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => { loadAll(); }, []);

  const handleAddTip = async (e) => {
    e.preventDefault();
    try {
      await addNutritionTip(newTip);
      showToast('הטיפ נוסף בהצלחה', 'success');
      setNewTip(EMPTY_TIP);
      setShowTipForm(false);
      const res = await getAllNutritionTips();
      setTips(res.data);
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בהוספת טיפ');
    }
  };

  const handleDeleteTip = async (id) => {
    if (!window.confirm('למחוק את הטיפ?')) return;
    try {
      await deleteNutritionTip(id);
      setTips(t => t.filter(tip => tip.id !== id));
      showToast('הטיפ נמחק', 'success');
    } catch (err) {
      showToast(err.response?.data || 'שגיאה במחיקת טיפ');
    }
  };

  const toggleBodyPart = (id) => {
    setNewWorkout(w => ({
      ...w,
      bodyPartIds: w.bodyPartIds.includes(id)
        ? w.bodyPartIds.filter(x => x !== id)
        : [...w.bodyPartIds, id],
    }));
  };

  const toggleEquipment = (id) => {
    setNewWorkout(w => ({
      ...w,
      equipmentIds: w.equipmentIds.includes(id)
        ? w.equipmentIds.filter(x => x !== id)
        : [...w.equipmentIds, id],
    }));
  };

  const handleAddWorkout = async (e) => {
    e.preventDefault();
    try {
      await addWorkout(newWorkout);
      showToast('האימון נוסף בהצלחה', 'success');
      setNewWorkout(EMPTY_WORKOUT);
      setShowWorkoutForm(false);
      const res = await getAllWorkouts();
      setWorkouts(res.data);
    } catch (err) {
      showToast(err.response?.data || 'שגיאה בהוספת אימון');
    }
  };

  if (loading) return <><Navbar /><div className="loading">טוען...</div></>;

  return (
    <>
      <Navbar />
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="admin-page">
        <h1>פאנל ניהול</h1>

        {/* ── משתמשים ── */}
        <section className="admin-section">
          <h2>משתמשים רשומים ({users.length})</h2>
          <div className="admin-table-wrap">
            <table className="admin-table">
              <thead>
                <tr>
                  <th>ID</th><th>שם</th><th>אימייל</th><th>תפקיד</th><th>קלוריות</th>
                </tr>
              </thead>
              <tbody>
                {users.map(u => (
                  <tr key={u.id}>
                    <td>{u.id}</td>
                    <td>{u.fullName}</td>
                    <td>{u.email}</td>
                    <td><span className={`role-badge ${u.role === 'ADMIN' ? 'role-admin' : 'role-user'}`}>{u.role}</span></td>
                    <td>{u.totalCaloriesBurned?.toLocaleString()}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        {/* ── טיפים תזונתיים ── */}
        <section className="admin-section">
          <div className="section-header-row">
            <h2>טיפים תזונתיים ({tips.length})</h2>
            <button className="btn-add-toggle" onClick={() => setShowTipForm(v => !v)}>
              {showTipForm ? 'ביטול' : '+ הוסף טיפ'}
            </button>
          </div>

          {showTipForm && (
            <form className="admin-inline-form" onSubmit={handleAddTip}>
              <div className="form-row">
                <div className="input-group">
                  <label>קלוריות מינימום</label>
                  <input type="number" value={newTip.minCaloriesThreshold}
                    onChange={e => setNewTip(t => ({ ...t, minCaloriesThreshold: +e.target.value }))} required />
                </div>
                <div className="input-group">
                  <label>קלוריות מקסימום</label>
                  <input type="number" value={newTip.maxCaloriesThreshold}
                    onChange={e => setNewTip(t => ({ ...t, maxCaloriesThreshold: +e.target.value }))} required />
                </div>
              </div>
              <div className="input-group">
                <label>המלצת אוכל</label>
                <textarea value={newTip.foodRecommendation}
                  onChange={e => setNewTip(t => ({ ...t, foodRecommendation: e.target.value }))} required />
              </div>
              <div className="input-group">
                <label>המלצת שתייה</label>
                <textarea value={newTip.waterRecommendation}
                  onChange={e => setNewTip(t => ({ ...t, waterRecommendation: e.target.value }))} required />
              </div>
              <button type="submit" className="btn-primary" style={{ width: 'auto' }}>שמור טיפ</button>
            </form>
          )}

          <div className="admin-table-wrap">
            <table className="admin-table">
              <thead>
                <tr><th>טווח קלוריות</th><th>המלצת אוכל</th><th>המלצת שתייה</th><th></th></tr>
              </thead>
              <tbody>
                {tips.map(tip => (
                  <tr key={tip.id}>
                    <td style={{ whiteSpace: 'nowrap' }}>{tip.minCaloriesThreshold} – {tip.maxCaloriesThreshold}</td>
                    <td>{tip.foodRecommendation}</td>
                    <td>{tip.waterRecommendation}</td>
                    <td><button className="btn-delete" onClick={() => handleDeleteTip(tip.id)}>מחק</button></td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        {/* ── אימונים ── */}
        <section className="admin-section">
          <div className="section-header-row">
            <h2>אימונים במערכת ({workouts.length})</h2>
            <button className="btn-add-toggle" onClick={() => setShowWorkoutForm(v => !v)}>
              {showWorkoutForm ? 'ביטול' : '+ הוסף אימון'}
            </button>
          </div>

          {showWorkoutForm && (
            <form className="admin-inline-form" onSubmit={handleAddWorkout}>
              <div className="form-row">
                <div className="input-group" style={{ flex: 2 }}>
                  <label>כותרת האימון</label>
                  <input value={newWorkout.title}
                    onChange={e => setNewWorkout(w => ({ ...w, title: e.target.value }))} required />
                </div>
                <div className="input-group">
                  <label>רמת קושי</label>
                  <select value={newWorkout.difficultyLevel}
                    onChange={e => setNewWorkout(w => ({ ...w, difficultyLevel: e.target.value }))}>
                    <option value="BEGINNER">מתחיל</option>
                    <option value="INTERMEDIATE">בינוני</option>
                    <option value="ADVANCED">מתקדם</option>
                  </select>
                </div>
                <div className="input-group">
                  <label>מיקום</label>
                  <select value={newWorkout.location}
                    onChange={e => setNewWorkout(w => ({ ...w, location: e.target.value }))}>
                    <option value="HOME">בית</option>
                    <option value="GYM">חדר כושר</option>
                    <option value="OUTDOOR">בחוץ</option>
                  </select>
                </div>
              </div>
              <div className="input-group">
                <label>תיאור</label>
                <textarea value={newWorkout.description}
                  onChange={e => setNewWorkout(w => ({ ...w, description: e.target.value }))} />
              </div>
              <div className="input-group">
                <label>קישור YouTube (ID או URL מלא)</label>
                <input value={newWorkout.youtubeUrl}
                  onChange={e => setNewWorkout(w => ({ ...w, youtubeUrl: e.target.value }))} required />
              </div>
              <div className="form-row">
                <div className="input-group">
                  <label>משך (דקות)</label>
                  <input type="number" min="1" value={newWorkout.durationMinutes}
                    onChange={e => setNewWorkout(w => ({ ...w, durationMinutes: +e.target.value }))} required />
                </div>
                <div className="input-group">
                  <label>קלוריות לשריפה</label>
                  <input type="number" min="1" value={newWorkout.caloriesBurned}
                    onChange={e => setNewWorkout(w => ({ ...w, caloriesBurned: +e.target.value }))} required />
                </div>
              </div>
              <div className="form-row">
                <div className="input-group">
                  <label>חלקי גוף</label>
                  <div className="chips-wrap">
                    {BODY_PARTS.map(bp => (
                      <button key={bp.id} type="button"
                        className={`chip ${newWorkout.bodyPartIds.includes(bp.id) ? 'selected' : ''}`}
                        onClick={() => toggleBodyPart(bp.id)}>
                        {bp.label}
                      </button>
                    ))}
                  </div>
                </div>
                <div className="input-group">
                  <label>ציוד נדרש</label>
                  <div className="chips-wrap">
                    {EQUIPMENT.map(eq => (
                      <button key={eq.id} type="button"
                        className={`chip ${newWorkout.equipmentIds.includes(eq.id) ? 'selected' : ''}`}
                        onClick={() => toggleEquipment(eq.id)}>
                        {eq.label}
                      </button>
                    ))}
                  </div>
                </div>
              </div>
              <button type="submit" className="btn-primary" style={{ width: 'auto' }}>שמור אימון</button>
            </form>
          )}

          <div className="admin-table-wrap">
            <table className="admin-table">
              <thead>
                <tr><th>כותרת</th><th>רמה</th><th>מיקום</th><th>משך</th><th>קל'</th></tr>
              </thead>
              <tbody>
                {workouts.map(w => (
                  <tr key={w.id}>
                    <td>{w.title}</td>
                    <td>{w.difficultyLevel}</td>
                    <td>{w.location}</td>
                    <td>{w.durationMinutes} דק'</td>
                    <td>{w.caloriesBurned}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      </div>
    </>
  );
}
