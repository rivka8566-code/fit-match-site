import { useState } from 'react';
import { useNavigate, Navigate } from 'react-router-dom';
import { registerUser, loginUser } from '../services/api';
import { useAuth } from '../context/AuthContext';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

function EyeOpen() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z" />
      <circle cx="12" cy="12" r="3" />
    </svg>
  );
}

function EyeClosed() {
  return (
    <svg width="18" height="18" viewBox="0 0 24 24" fill="none"
      stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
      <path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19m-6.72-1.07a3 3 0 1 1-4.24-4.24" />
      <line x1="1" y1="1" x2="23" y2="23" />
    </svg>
  );
}

export default function LoginPage() {
  const [tab, setTab] = useState('login');
  const [form, setForm] = useState({ email: '', password: '', fullName: '' });
  const [showPw, setShowPw] = useState(false);
  const [loading, setLoading] = useState(false);
  const { login, user } = useAuth();
  const navigate = useNavigate();
  const { toast, showToast, hideToast } = useToast();

  const handleChange = (e) => setForm(f => ({ ...f, [e.target.name]: e.target.value }));

  const handleSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);
    try {
      if (tab === 'register') {
        const res = await registerUser({ email: form.email, password: form.password, fullName: form.fullName });
        // שומר ב-localStorage קודם, ואז login מעדכן state, ואז navigate
        localStorage.setItem('fitmatch_user', JSON.stringify(res.data));
        login(res.data);
        navigate('/questionnaire', { replace: true });
      } else {
        const res = await loginUser({ email: form.email, password: form.password });
        localStorage.setItem('fitmatch_user', JSON.stringify(res.data));
        login(res.data);
        navigate('/dashboard', { replace: true });
      }
    } catch (err) {
      showToast(err.response?.data || 'שגיאה. נסה שוב.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="auth-page">
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="auth-card">
        <div className="auth-logo">
          <span className="logo-text">FIT</span><span className="logo-accent">MATCH</span>
        </div>
        <p className="auth-sub">פלטפורמת הכושר והתזונה האישית שלך</p>

        <div className="tab-toggle">
          <button className={tab === 'login' ? 'active' : ''} onClick={() => setTab('login')}>
            התחברות
          </button>
          <button className={tab === 'register' ? 'active' : ''} onClick={() => setTab('register')}>
            הרשמה
          </button>
        </div>

        <form onSubmit={handleSubmit} className="auth-form">
          {tab === 'register' && (
            <div className="input-group">
              <label>שם מלא</label>
              <input name="fullName" placeholder="ישראל ישראלי" value={form.fullName}
                onChange={handleChange} required />
            </div>
          )}
          <div className="input-group">
            <label>אימייל</label>
            <input name="email" type="email" placeholder="your@email.com" value={form.email}
              onChange={handleChange} required />
          </div>
          <div className="input-group">
            <label>סיסמה</label>
            <div className="pw-wrap">
              <input
                name="password"
                type={showPw ? 'text' : 'password'}
                placeholder="••••••••"
                value={form.password}
                onChange={handleChange}
                required
              />
              <button
                type="button"
                className="pw-toggle"
                onClick={() => setShowPw(v => !v)}
                tabIndex={-1}
              >
                {showPw ? <EyeClosed /> : <EyeOpen />}
              </button>
            </div>
          </div>

          <button type="submit" className="btn-primary" disabled={loading}>
            {loading ? 'טוען...' : tab === 'login' ? 'התחבר' : 'צור חשבון'}
          </button>
        </form>

        <p className="auth-switch">
          {tab === 'login' ? 'אין לך חשבון עדיין? ' : 'יש לך חשבון? '}
          <button className="link-btn" onClick={() => setTab(tab === 'login' ? 'register' : 'login')}>
            {tab === 'login' ? 'הרשם עכשיו' : 'התחבר'}
          </button>
        </p>
      </div>
    </div>
  );
}
