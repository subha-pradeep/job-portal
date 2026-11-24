// Colored AI score: green = strong match, amber = partial, red = weak
export default function ScoreBadge({ score }) {
  if (score === null || score === undefined) {
    return <span className="score score-pending">AI reviewing…</span>
  }
  const level = score >= 75 ? 'high' : score >= 50 ? 'mid' : 'low'
  return <span className={`score score-${level}`}>{score}/100</span>
}
