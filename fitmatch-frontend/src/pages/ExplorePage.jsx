import { useState, useEffect, useRef } from 'react';
import { useNavigate } from 'react-router-dom';
import { getAllWorkouts, searchWorkouts } from '../services/api';
import Navbar from '../components/Navbar';
import Toast from '../components/Toast';
import useToast from '../components/useToast';

const DIFFICULTY_LABELS = { BEGINNER: 'מתחיל', INTERMEDIATE: 'בינוני', ADVANCED: 'מתקדם' };
const LOCATION_LABELS = { HOME: 'בית', GYM: 'חדר כושר', OUTDOOR: 'בחוץ' };
const DIFF_COLORS = { BEGINNER: 'badge-green', INTERMEDIATE: 'badge-yellow', ADVANCED: 'badge-red' };
const DIFFICULTY_OPTIONS = [
  { value: '', label: 'כל הרמות' },
  { value: 'BEGINNER', label: 'מתחיל' },
  { value: 'INTERMEDIATE', label: 'בינוני' },
  { value: 'ADVANCED', label: 'מתקדם' },
];
const LOCATION_OPTIONS = [
  { value: '', label: 'כל המיקומים' },
  { value: 'HOME', label: 'בית' },
  { value: 'GYM', label: 'חדר כושר' },
  { value: 'OUTDOOR', label: 'בחוץ' },
];

export default function ExplorePage() {
  const navigate = useNavigate();
  const [workouts, setWorkouts] = useState([]);
  const [filters, setFilters] = useState({ difficulty: '', location: '' });
  const [openFilter, setOpenFilter] = useState(null);
  const [loading, setLoading] = useState(true);
  const filtersRef = useRef(null);
  const { toast, showToast, hideToast } = useToast();

  useEffect(() => {
    const load = async () => {
      setLoading(true);
      try {
        let response;
        if (filters.difficulty || filters.location) {
          response = await searchWorkouts(filters.difficulty || undefined, filters.location || undefined);
        } else {
          response = await getAllWorkouts();
        }
        setWorkouts(response.data);
      } catch (error) {
        showToast(error.response?.data || 'שגיאה בטעינת האימונים');
      } finally {
        setLoading(false);
      }
    };
    load();
  }, [filters]);

  useEffect(() => {
    const onClick = (event) => {
      if (filtersRef.current && !filtersRef.current.contains(event.target)) {
        setOpenFilter(null);
      }
    };
    document.addEventListener('mousedown', onClick);
    return () => document.removeEventListener('mousedown', onClick);
  }, []);

  const handleFilterChange = (key, value) => {
    setFilters((prev) => ({ ...prev, [key]: value }));
    setOpenFilter(null);
  };

  const renderDropdown = (key, options) => {
    const selected = options.find((option) => option.value === filters[key]);
    return (
      <div className="filter-dropdown" ref={filtersRef}>
        <button
          type="button"
          className="filter-dropdown-toggle"
          onClick={() => setOpenFilter((prev) => (prev === key ? null : key))}
        >
          <span className="filter-dropdown-value">{selected?.label}</span>
          <span className="dropdown-arrow">▾</span>
        </button>
        {openFilter === key && (
          <div className="filter-dropdown-menu">
            {options.map((option) => (
              <button
                type="button"
                key={option.value}
                className={`filter-dropdown-item ${filters[key] === option.value ? 'selected' : ''}`}
                onClick={() => handleFilterChange(key, option.value)}
              >
                {option.label}
              </button>
            ))}
          </div>
        )}
      </div>
    );
  };

  const handleWorkoutClick = (workout) => {
    navigate(`/workout/${workout.id}`, { state: { workout, fromExplore: true } });
  };

  return (
    <>
      <Navbar />
      {toast && <Toast message={toast.message} type={toast.type} onClose={hideToast} />}

      <div className="explore-page">
        <div className="explore-header">
          <h1>ספריית האימונים</h1>
          <p>גלה אימונים, סנן לפי רמה ומיקום, והתאמן</p>
        </div>

        <div className="filter-bar">
          <div className="filter-left" ref={filtersRef}>
            {renderDropdown('difficulty', DIFFICULTY_OPTIONS)}
            {renderDropdown('location', LOCATION_OPTIONS)}
            {(filters.difficulty || filters.location) && (
              <button className="btn-clear" onClick={() => setFilters({ difficulty: '', location: '' })}>
                נקה
              </button>
            )}
          </div>
          <div className="filter-right">
            <span className="filter-count">{workouts.length} אימונים</span>
          </div>
        </div>

        {loading ? (
          <div className="loading">טוען אימונים...</div>
        ) : (
          <div className="explore-grid">
            {workouts.length === 0 && (
              <p className="no-results">לא נמצאו אימונים לפי הפילטרים שנבחרו.</p>
            )}
            {workouts.map((w) => (
              <div key={w.id} className="explore-card" onClick={() => handleWorkoutClick(w)}>
                <div className="explore-card-top">
                  <h3>{w.title}</h3>
                  <div className="workout-badges">
                    <span className={`badge ${DIFF_COLORS[w.difficultyLevel]}`}>
                      {DIFFICULTY_LABELS[w.difficultyLevel]}
                    </span>
                    <span className="badge badge-blue">{LOCATION_LABELS[w.location]}</span>
                  </div>
                </div>
                <p className="explore-desc">{w.description}</p>
                <div className="explore-meta">
                  <span>{w.durationMinutes} דקות</span>
                  <span>{w.caloriesBurned} קל'</span>
                </div>
                <div className="explore-cta">התאמן &rarr;</div>
              </div>
            ))}
          </div>
        )}
      </div>
    </>
  );
}
