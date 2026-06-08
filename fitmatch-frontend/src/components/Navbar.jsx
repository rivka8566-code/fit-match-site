import { Link, useNavigate, useLocation } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const isActive = (path) => location.pathname === path;

  return (
    <nav className="navbar">
      <Link to="/dashboard" className="navbar-brand">
        <span className="logo-text">FIT</span><span className="logo-accent">MATCH</span>
      </Link>

      <div className="navbar-links">
        <Link to="/dashboard" className={isActive('/dashboard') ? 'nav-active' : ''}>דף הבית</Link>
        <Link to="/explore" className={isActive('/explore') ? 'nav-active' : ''}>חיפוש אימונים</Link>
        <Link to="/profile" className={isActive('/profile') ? 'nav-active' : ''}>הפרופיל</Link>
        {user?.role === 'ADMIN' && (
          <Link to="/admin" className={isActive('/admin') ? 'nav-active' : ''}>מנהל</Link>
        )}
        <div className="nav-actions">
          <button onClick={handleLogout} className="btn-logout">יציאה</button>
        </div>
      </div>
    </nav>
  );
}
