import { useState, useEffect } from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { AuthProvider, useAuth } from './context/AuthContext';
import LoginPage from './pages/LoginPage';
import QuestionnairePage from './pages/QuestionnairePage';
import DashboardPage from './pages/DashboardPage';
import ExplorePage from './pages/ExplorePage';
import ProfilePage from './pages/ProfilePage';
import AdminPage from './pages/AdminPage';
import WorkoutDetailPage from './pages/WorkoutDetailPage';

// ייבוא פונקציית ה-API המקורית מתוך קובץ ה-api.js שלך
import { getActiveProgram } from './services/api';

// 1. קומפוננטת הגנה על נתיבים למשתמשים מחוברים
function ProtectedRoute({ children }) {
  const { user } = useAuth();
  return user ? children : <Navigate to="/login" replace />;
}

// 2. קומפוננטת הגנה על נתיבי מנהל (Admin)
function AdminRoute({ children }) {
  const { user } = useAuth();
  if (!user) return <Navigate to="/login" replace />;
  //  הקוד המתוקן והנקי
  if (user.role !== 'ADMIN') return <Navigate to="/dashboard" replace />; return children;
}

// 3. קומפוננטת הניתוב החכמה שמקשרת בין הלוגין, השאלון והדאשבורד
// בודקת בזמן אמת מול השרת האם למשתמש יש תוכנית ומנווטת בהתאם
function HomeRedirect() {
  const { user } = useAuth();
  const [checking, setChecking] = useState(true);
  const [redirectTo, setRedirectTo] = useState(null);

  useEffect(() => {
    // אם המשתמש לא מחובר בכלל, נשלח אותו להתחבר
    if (!user) {
      setRedirectTo('/login');
      setChecking(false);
      return;
    }

    const cachedStatus = localStorage.getItem('has_program');

    // אם הסטטוס כבר שמור בדפדפן (true או false), נשתמש בו מיד בלי להציק לשרת
    if (cachedStatus === 'true') {
      setRedirectTo('/dashboard');
      setChecking(false);
    } else if (cachedStatus === 'false') {
      setRedirectTo('/questionnaire');
      setChecking(false);
    } else {
      // אם אין מפתח שמור (למשל מיד אחרי לוגין או ריפרש), נבדוק דינמית מול ה-Backend
      getActiveProgram(user.id)
        .then(() => {
          // אם חזר סטטוס 200 מוצלח - קיימת תוכנית פעילה ב-DB
          localStorage.setItem('has_program', 'true');
          setRedirectTo('/dashboard');
        })
        .catch(() => {
          // אם חזרה שגיאה מהשרת (סימן שאין לו תוכנית פעילה), נשלח אותו לשאלון
          localStorage.setItem('has_program', 'false');
          setRedirectTo('/questionnaire');
        })
        .finally(() => {
          setChecking(false);
        });
    }
  }, [user]);

  if (checking) {
    return (
      <div style={{ display: 'flex', justifyContent: 'center', alignItems: 'center', height: '100vh', direction: 'rtl', fontFamily: 'sans-serif' }}>
        <h3>טוען נתוני תוכנית...</h3>
      </div>
    );
  }

  return <Navigate to={redirectTo} replace />;
}

// 4. הגדרת כל הראוטים באפליקציה
// שימי לב שנתיב הבית ("/") ונתיב ה-fallback ("*") מפנים ל-HomeRedirect שעושה את הבדיקה
function AppRoutes() {
  const { user } = useAuth();
  return (
    <Routes>
      <Route path="/login" element={user ? <Navigate to="/" replace /> : <LoginPage />} />
      <Route path="/questionnaire" element={<ProtectedRoute><QuestionnairePage /></ProtectedRoute>} />
      <Route path="/dashboard" element={<ProtectedRoute><DashboardPage /></ProtectedRoute>} />
      <Route path="/explore" element={<ProtectedRoute><ExplorePage /></ProtectedRoute>} />
      <Route path="/profile" element={<ProtectedRoute><ProfilePage /></ProtectedRoute>} />
      <Route path="/admin" element={<AdminRoute><AdminPage /></AdminRoute>} />
      <Route path="/workout/:workoutId" element={<ProtectedRoute><WorkoutDetailPage /></ProtectedRoute>} />

      {/* נתיב ברירת המחדל מריץ את הבדיקה החכמה */}
      <Route path="/" element={<HomeRedirect />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}

// 5. קומפוננטת האפליקציה הראשית - מיוצאת כ-default ומכילה את כל העטיפות
export default function App() {
  return (
    <BrowserRouter>
      <AuthProvider>
        <AppRoutes />
      </AuthProvider>
    </BrowserRouter>
  );
}