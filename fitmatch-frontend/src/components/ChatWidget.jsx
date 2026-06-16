import React, { useState, useEffect, useRef } from 'react';
import { useAuth } from '../context/AuthContext';

export default function ChatWidget() {
  const { user } = useAuth();
  const [isOpen, setIsOpen] = useState(false);
  const [message, setMessage] = useState('');
  const [history, setHistory] = useState([]);
  const [loading, setLoading] = useState(false);
  
  const messagesEndRef = useRef(null);

  // האפקט שאחראי לרנדר את הצ'אט מחדש ולנקות אותו בכל פעם שהוא נפתח/נסגר
  useEffect(() => {
    if (!isOpen) {
      // ברגע שהמשתמש סוגר את הצ'אט, אנחנו מנקים את ההיסטוריה וההודעה שבקלט
      setHistory([]);
      setMessage('');
    }
  }, [isOpen]);

  // גלילה אוטומטית לסוף השיחה
  useEffect(() => {
    if (messagesEndRef.current) {
      messagesEndRef.current.scrollIntoView({ behavior: 'smooth' });
    }
  }, [history, loading]);

  if (!user) return null;

  const handleSendMessage = async (e) => {
    e.preventDefault();
    if (!message.trim()) return;

    const userMsg = { role: 'user', content: message };
    setHistory((prev) => [...prev, userMsg]);
    setMessage('');
    setLoading(true);

    try {
      const response = await fetch('http://localhost:8080/api/bot/message', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify({ message: message, history: history }),
      });
      const data = await response.json();
      
      if (data.response) {
        setHistory((prev) => [...prev, { role: 'assistant', content: data.response }]);
      } else {
        setHistory((prev) => [...prev, { role: 'assistant', content: 'מתנצל, חלה שגיאה זמנית בעיבוד הנתונים. נסה שנית.' }]);
      }
    } catch (error) {
      setHistory((prev) => [...prev, { role: 'assistant', content: 'לא הצלחתי ליצור קשר עם שרת האימונים. ודא שהשרת פעיל.' }]);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{ position: 'fixed', bottom: '30px', right: '30px', zIndex: 1000, fontFamily: 'Segoe UI, Roboto, Helvetica, Arial, sans-serif', direction: 'rtl' }}>
      
      {/* חלונית השיחה המעוצבת */}
      {isOpen && (
        <div style={{
          width: '360px', height: '500px', backgroundColor: '#ffffff', borderRadius: '16px',
          boxShadow: '0px 12px 36px rgba(0,0,0,0.15)', display: 'flex', flexDirection: 'column', overflow: 'hidden',
          border: '1px solid #f0f0f0', marginBottom: '80px', 
          animation: 'fadeIn 0.2s ease-out'
        }}>
          
          {/* כותרת החלונית */}
          <div style={{ 
            backgroundColor: '#ff6b00', color: 'white', padding: '16px', 
            display: 'flex', justifyContent: 'space-between', alignItems: 'center',
            borderBottom: '2px solid rgba(0,0,0,0.05)'
          }}>
            <div style={{ display: 'flex', alignItems: 'center', gap: '10px' }}>
              <div style={{ width: '10px', height: '10px', backgroundColor: '#28a745', borderRadius: '50%' }}></div>
              <span style={{ fontWeight: '600', fontSize: '16px', letterSpacing: '0.5px' }}>Fit-Match AI Coach</span>
            </div>
          </div>

          {/* אזור תצוגת ההודעות */}
          <div style={{ flex: 1, padding: '16px', overflowY: 'auto', backgroundColor: '#f8f9fa', display: 'flex', flexDirection: 'column', gap: '12px' }}>
            <div style={{ alignSelf: 'flex-start', backgroundColor: '#ffffff', color: '#333333', padding: '10px 14px', borderRadius: '4px 12px 12px 12px', maxWidth: '85%', fontSize: '14px', boxShadow: '0px 2px 4px rgba(0,0,0,0.02)', border: '1px solid #eaeaea' }}>
              שלום <strong>{user.name || 'מתאמן'}</strong>! גאה לראות אותך כאן. צריך עזרה עם תוכנית האימונים, תפריט התזונה או חישוב מדדי גוף (BMI וקלוריות)? שאל אותי!
            </div>

            {history.map((msg, index) => (
              <div
                key={index}
                style={{
                  alignSelf: msg.role === 'user' ? 'flex-end' : 'flex-start',
                  backgroundColor: msg.role === 'user' ? '#ff6b00' : '#ffffff',
                  color: msg.role === 'user' ? '#ffffff' : '#333333',
                  padding: '10px 14px', 
                  borderRadius: msg.role === 'user' ? '12px 12px 4px 12px' : '4px 12px 12px 12px', 
                  maxWidth: '85%', fontSize: '14px',
                  boxShadow: '0px 2px 6px rgba(0,0,0,0.03)',
                  border: msg.role === 'user' ? 'none' : '1px solid #eaeaea',
                  whiteSpace: 'pre-line',
                  lineHeight: '1.4'
                }}
              >
                {msg.content}
              </div>
            ))}
            
            {loading && (
              <div style={{ alignSelf: 'flex-start', color: '#888888', fontSize: '13px', display: 'flex', gap: '4px', paddingRight: '4px' }}>
                <span>המאמן מנתח נתונים...</span>
              </div>
            )}
            <div ref={messagesEndRef} />
          </div>

          {/* שורת קלט מעוצבת עם לחצן חץ שליחה מופנה כלפי מעלה */}
          <form onSubmit={handleSendMessage} style={{ display: 'flex', backgroundColor: '#ffffff', padding: '12px', borderTop: '1px solid #eeeeee', gap: '10px', alignItems: 'center' }}>
            <input
              type="text"
              value={message}
              onChange={(e) => setMessage(e.target.value)}
              placeholder="שאל את המאמן משהו..."
              style={{ 
                flex: 1, padding: '12px 16px', border: '1px solid #e0e0e0', borderRadius: '24px', 
                outline: 'none', fontSize: '14px', backgroundColor: '#fdfdfd'
              }}
              onFocus={(e) => e.currentTarget.style.borderColor = '#ff6b00'}
              onBlur={(e) => e.currentTarget.style.borderColor = '#e0e0e0'}
            />
            <button 
              type="submit" 
              style={{ 
                backgroundColor: '#ff6b00', color: 'white', border: 'none', 
                width: '42px', height: '42px', borderRadius: '50%', cursor: 'pointer', 
                fontSize: '18px', display: 'flex', justifyContent: 'center', alignItems: 'center',
                transition: 'background-color 0.2s, transform 0.1s',
                padding: '0', margin: '0'
              }}
              onMouseEnter={(e) => {
                e.currentTarget.style.backgroundColor = '#e05e00';
                e.currentTarget.style.transform = 'scale(1.05)';
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.backgroundColor = '#ff6b00';
                e.currentTarget.style.transform = 'scale(1)';
              }}
            >
              <svg 
                width="18" 
                height="18" 
                viewBox="0 0 24 24" 
                fill="none" 
                stroke="currentColor" 
                strokeWidth="2.5" 
                strokeLinecap="round" 
                strokeLinejoin="round"
              >
                <line x1="12" y1="19" x2="12" y2="5"></line>
                <polyline points="5 12 12 5 19 12"></polyline>
              </svg>
            </button>
          </form>
        </div>
      )}

      {/* הבלון הכתום הראשי */}
      <button
        onClick={() => setIsOpen(!isOpen)}
        style={{
          position: 'absolute', bottom: '0', right: '0',
          width: '65px', height: '65px', borderRadius: '50%', 
          backgroundColor: '#ff6b00', color: 'white', border: 'none', cursor: 'pointer', 
          boxShadow: '0px 6px 16px rgba(255, 107, 0, 0.4)',
          display: 'flex', justifyContent: 'center', alignItems: 'center',
          transition: 'transform 0.2s ease',
        }}
        onMouseEnter={(e) => e.currentTarget.style.transform = 'scale(1.08)'}
        onMouseLeave={(e) => e.currentTarget.style.transform = 'scale(1)'}
      >
        {isOpen ? (
          <span style={{ fontSize: '24px', fontWeight: '300' }}>✕</span>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', justifyContent: 'center' }}>
            <img 
              src="/favicon.svg" 
              alt="Fit-Match Bot" 
              style={{ width: '28px', height: '28px', filter: 'brightness(0) invert(1)' }} 
            />
            <span style={{ fontSize: '10px', fontWeight: 'bold', marginTop: '2px', textTransform: 'uppercase' }}>CHAT</span>
          </div>
        )}
      </button>

    </div>
  );
}