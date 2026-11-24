import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'
import ScoreBadge from '../components/ScoreBadge.jsx'

export default function MyApplications() {
  const [applications, setApplications] = useState(null)
  const [error, setError] = useState('')

  useEffect(() => {
    api.myApplications().then(setApplications).catch((err) => setError(err.message))
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!applications) return <p>Loading…</p>

  return (
    <div>
      <h1>My applications</h1>
      {applications.length === 0 && (
        <p className="muted">You haven't applied yet. <Link to="/jobs">Browse jobs</Link></p>
      )}
      {applications.map((a) => (
        <div className="card" key={a.id}>
          <div className="row">
            <div>
              <h3><Link to={`/jobs/${a.jobId}`}>{a.jobTitle}</Link></h3>
              <p className="muted">{a.company} · Applied {new Date(a.appliedAt).toLocaleDateString()}</p>
            </div>
            <div className="right">
              <span className={`status status-${a.status.toLowerCase()}`}>{a.status}</span>
              <ScoreBadge score={a.aiScore} />
            </div>
          </div>
          {a.aiFeedback && <pre className="feedback">{a.aiFeedback}</pre>}
        </div>
      ))}
    </div>
  )
}
