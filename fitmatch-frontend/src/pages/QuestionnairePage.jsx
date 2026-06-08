import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { createProgram } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const STEPS = ['רמת כושר', 'מיקום אימון', 'לוח זמנים', 'יעדים וציוד'];

const DIFFICULTY_OPTIONS = [
  { value: 'BEGINNER', label: 'מתחיל', desc: 'מתאים לאלו שרק מתחילים להתאמן' },
  { value: 'INTERMEDIATE', label: 'בינוני', desc: 'פעילות סדירה מספר פעמים בשבוע' },
  { value: 'ADVANCED', label: 'מתקדם', desc: 'אימונים אינטנסיביים ועצימים' },
];

const LOCATION_OPTIONS = [
  { value: 'HOME', label: 'בית', icon: '⌂' },
  { value: 'GYM', label: 'חדר כושר', icon: '◈' },
  { value: 'OUTDOOR', label: 'בחוץ', icon: '◉' },
];

const BODY_PARTS = [
  { id: 1, label: 'בטן וליבה' },
  { id: 2, label: 'רגליים וישבן' },
  { id: 3, label: 'חזה וידיים' },
  { id: 4, label: 'גב וכתפיים' },
];

const EQUIPMENT_LIST = [
  { id: 1, label: 'משקולות יד' },
  { id: 2, label: 'מזרן יוגה' },
  { id: 3, label: 'גומיות התנגדות' },
  { id: 4, label: 'ללא ציוד', isNone: true },
];

const NO_EQUIPMENT_ID = 4;

export default function QuestionnairePage() {
  const { user } = useAuth();
  const navigate = useNavigate();
  const { toast, showToast, hideToast } = useToast();
  const [step, setStep] = useState(0);
  const [loading, setLoading] = useState(false);

  const [form, setForm] = useState({
    difficultyLevel: '',
    preferredLocations: [],
    daysPerWeek: 3,
    durationWeeks: 4,
    weeklyCaloriesGoal: 1500,
    preferredBodyPartIds: [],
    availableEquipmentIds: [],
  });

  const set = (key, val) => setForm(f => ({ ...f, [key]: val }));

  const toggleLocation = (val) => {
    setForm(f => ({
      ...f,
      preferredLocations: f.preferredLocations.includes(val)
        ? f.preferredLocations.filter(x => x !== val)
        : [...f.preferredLocations, val],
    }));
  };

  const toggleBodyPart = (id) => {
    setForm(f => ({
      ...f,
      preferredBodyPartIds: f.preferredBodyPartIds.includes(id)
        ? f.preferredBodyPartIds.filter(x => x !== id)
        : [...f.preferredBodyPartIds, id],
    }));
  };

  const toggleEquipment = (id, isNone) => {
    setForm(f => {
      if (isNone) {
        return { ...f, availableEquipmentIds: f.availableEquipmentIds.includes(id) ? [] : [id] };
      }
      const withoutNone = f.availableEquipmentIds.filter(x => x !== NO_EQUIPMENT_ID);
      return {
        ...f,
        availableEquipmentIds: withoutNone.includes(id)
          ? withoutNone.filter(x => x !== id)
          : [...withoutNone, id],
      };
    });
  };

  const canNext = () => {
    if (step === 0) return !!form.difficultyLevel;
    if (step === 1) return form.preferredLocations.length > 0;
    return true;
  };

  const handleSubmit = async () => {
    setLoading(true);
    try {
      await createProgram({
        userId: user.id,
        difficultyLevel: form.difficultyLevel,
        preferredLocations: form.preferredLocations,
        daysPerWeek: form.daysPerWeek,
        durationWeeks: form.durationWeeks,
        weeklyCaloriesGoal: form.weeklyCaloriesGoal,
        preferredBodyPartIds: form.preferredBodyPartIds,
        availableEquipmentIds: form.availableEquipmentIds,
      });
      navigate('/dashboard');
    } catch (err) {
      showToast(err.response?.data || 'שגיאה ביצירת התוכנית');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="questionnaire-page">
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="questionnaire-card">
        <div className="q-header">
          <div className="q-logo">FITMATCH</div>
          <div className="q-header-row">
            <h2>בניית תוכנית אימונים אישית</h2>
          </div>
        </div>

        <div className="stepper">
          {STEPS.map((s, i) => (
            <div key={i} className={`step ${i === step ? 'active' : i < step ? 'done' : ''}`}>
              <div className="step-dot">{i < step ? '✓' : i + 1}</div>
              <span className="step-label">{s}</span>
            </div>
          ))}
        </div>

        <div className="step-content">
          {step === 0 && (
            <div>
              <h3 className="step-title">מה רמת הכושר הנוכחית שלך?</h3>
              <div className="options-col">
                {DIFFICULTY_OPTIONS.map(o => (
                  <button
                    key={o.value}
                    className={`option-card ${form.difficultyLevel === o.value ? 'selected' : ''}`}
                    onClick={() => set('difficultyLevel', o.value)}
                  >
                    <span className="option-title">{o.label}</span>
                    <span className="option-desc">{o.desc}</span>
                  </button>
                ))}
              </div>
            </div>
          )}

          {step === 1 && (
            <div>
              <h3 className="step-title">היכן אתה מעדיף להתאמן? (ניתן לבחור יותר מאחד)</h3>
              <div className="options-row">
                {LOCATION_OPTIONS.map(o => (
                  <button
                    key={o.value}
                    className={`option-tile ${form.preferredLocations.includes(o.value) ? 'selected' : ''}`}
                    onClick={() => toggleLocation(o.value)}
                  >
                    <span className="tile-icon">{o.icon}</span>
                    <span>{o.label}</span>
                    {form.preferredLocations.includes(o.value) && <span className="check">✓</span>}
                  </button>
                ))}
              </div>
            </div>
          )}

          {step === 2 && (
            <div className="options-form">
              <h3 className="step-title">הגדר את לוח הזמנים שלך</h3>

              <div className="slider-group">
                <div className="slider-row">
                  <label>ימי אימון בשבוע</label>
                  <span className="slider-val">{form.daysPerWeek}</span>
                </div>
                <input type="range" min="1" max="7" value={form.daysPerWeek}
                  onChange={e => set('daysPerWeek', +e.target.value)} />
                <div className="slider-ticks"><span>1</span><span>7</span></div>
              </div>

              <div className="slider-group">
                <div className="slider-row">
                  <label>משך התוכנית</label>
                  <span className="slider-val">{form.durationWeeks} שבועות</span>
                </div>
                <input type="range" min="1" max="12" value={form.durationWeeks}
                  onChange={e => set('durationWeeks', +e.target.value)} />
                <div className="slider-ticks"><span>1</span><span>12</span></div>
              </div>
            </div>
          )}

          {step === 3 && (
            <div className="options-form">
              <h3 className="step-title">יעדים וציוד</h3>

              <div className="slider-group">
                <div className="slider-row">
                  <label>יעד קלוריות שבועי</label>
                  <span className="slider-val">{form.weeklyCaloriesGoal.toLocaleString()}</span>
                </div>
                <input type="range" min="500" max="5000" step="100" value={form.weeklyCaloriesGoal}
                  onChange={e => set('weeklyCaloriesGoal', +e.target.value)} />
                <div className="slider-ticks"><span>500</span><span>5,000</span></div>
              </div>

              <div className="multi-group">
                <label>חלקי גוף למיקוד (אופציונלי)</label>
                <div className="chips-wrap">
                  {BODY_PARTS.map(bp => (
                    <button
                      key={bp.id}
                      className={`chip ${form.preferredBodyPartIds.includes(bp.id) ? 'selected' : ''}`}
                      onClick={() => toggleBodyPart(bp.id)}
                    >
                      {bp.label}
                    </button>
                  ))}
                </div>
              </div>

              <div className="multi-group">
                <label>ציוד זמין</label>
                <div className="chips-wrap">
                  {EQUIPMENT_LIST.map(eq => (
                    <button
                      key={eq.id}
                      className={`chip ${form.availableEquipmentIds.includes(eq.id) ? 'selected' : ''} ${eq.isNone ? 'chip-none' : ''}`}
                      onClick={() => toggleEquipment(eq.id, eq.isNone)}
                    >
                      {eq.label}
                    </button>
                  ))}
                </div>
              </div>
            </div>
          )}
        </div>

        <div className="step-nav">
          {step > 0 && (
            <button className="btn-secondary" onClick={() => setStep(s => s - 1)}>חזור</button>
          )}
          {step < STEPS.length - 1 ? (
            <button className="btn-primary" onClick={() => setStep(s => s + 1)} disabled={!canNext()}>
              המשך
            </button>
          ) : (
            <button className="btn-primary" onClick={handleSubmit} disabled={loading}>
              {loading ? 'בונה תוכנית...' : 'צור תוכנית'}
            </button>
          )}
        </div>

        {/* כפתור דילוג - מוצג תמיד */}
        <div className="q-skip-bar">
          <button className="btn-skip" onClick={() => navigate('/dashboard')}>
            דלג על השאלון — אמלא מאוחר יותר
          </button>
        </div>
      </div>
    </div>
  );
}
