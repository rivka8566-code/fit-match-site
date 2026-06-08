export default function ProgressBar({ burned, target }) {
  const pct = target > 0 ? Math.min(Math.round((burned / target) * 100), 100) : 0;

  return (
    <div className="progress-widget">
      <div className="progress-header">
        <span>🔥 התקדמות קלוריות</span>
        <span>{burned} / {target} קל'</span>
      </div>
      <div className="progress-track">
        <div className="progress-fill" style={{ width: `${pct}%` }} />
      </div>
      <div className="progress-pct">{pct}%</div>
    </div>
  );
}
