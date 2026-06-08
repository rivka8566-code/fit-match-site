import { useEffect, useRef } from 'react';

export default function Confetti({ onDone }) {
  const ref = useRef();

  useEffect(() => {
    const particles = Array.from({ length: 60 }, (_, i) => {
      const el = document.createElement('div');
      el.className = 'confetti-particle';
      el.style.left = Math.random() * 100 + 'vw';
      el.style.background = `hsl(${Math.random() * 360},90%,60%)`;
      el.style.animationDelay = Math.random() * 0.5 + 's';
      el.style.width = el.style.height = Math.random() * 10 + 6 + 'px';
      ref.current.appendChild(el);
      return el;
    });
    const t = setTimeout(() => {
      particles.forEach(p => p.remove());
      onDone();
    }, 2500);
    return () => clearTimeout(t);
  }, [onDone]);

  return <div ref={ref} className="confetti-container" />;
}
