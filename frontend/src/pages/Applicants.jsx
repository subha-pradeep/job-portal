import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import ScoreBadge from '../components/ScoreBadge.jsx'

export default function Applicants() {
  const { id } = useParams()
  const [job, setJob] = useState(null)
  const [applicants, setApplicants] = useState(null)
  const [error, setError] = useState('')
  const [busyId, setBusyId] = useState(null)

  const load = () => api.applicants(id).then(setApplicants).catch((err) => setError(err.message))

  useEffect(() => {
    api.getJob(id).then(setJob).catch(() => {})
    load()
  }, [id])

  // Runs an action for one applicant, then reloads the list
  const act = (applicationId, action) => async () => {
    setBusyId(applicationId)
    setError('')
    try {
      await action()
      await load()
    } catch (err) {
      setError(err.message)
    } finally {
      setBusyId(null)
    }
  }

  // The resume needs our JWT, so fetch it as a file and open that in a new tab
  const openResume = async (applicationId) => {
    try {
      const blob = await api.resumeBlob(applicationId)
      window.open(URL.createObjectURL(blob), '_blank')
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <Link to="/recruiter" className="muted">← My jobs</Link>
      <div className="row">
        <h1>Applicants{job ? ` – ${job.title}` : ''}</h1>
        <button className="btn btn-light" onClick={load}>Refresh</button>
      </div>
      <p className="muted">Sorted by AI match score. New applications are scored within a few seconds.</p>

      {error && <p className="error">{error}</p>}
      {!applicants && !error && <p>Loading…</p>}
      {applicants?.length === 0 && <p className="muted">No applications yet.</p>}

      {applicants?.map((a) => (
        <div className="card" key={a.id}>
          <div className="row">
            <div>
              <h3>{a.candidateName}</h3>
              <p className="muted">{a.candidateEmail} · Applied {new Date(a.appliedAt).toLocaleDateString()}</p>
            </div>
            <div className="right">
              <span className={`status status-${a.status.toLowerCase()}`}>{a.status}</span>
              <ScoreBadge score={a.aiScore} />
            </div>
          </div>

          {a.aiFeedback && <pre className="feedback">{a.aiFeedback}</pre>}

          <div className="actions">
            <button className="btn btn-light" onClick={() => openResume(a.id)}>View resume</button>
            <button className="btn btn-light" disabled={busyId === a.id}
                    onClick={act(a.id, () => api.rescreen(a.id))}>Re-run AI</button>
            <button className="btn btn-success" disabled={busyId === a.id || a.status === 'SHORTLISTED'}
                    onClick={act(a.id, () => api.setStatus(a.id, 'SHORTLISTED'))}>Shortlist</button>
            <button className="btn btn-danger" disabled={busyId === a.id || a.status === 'REJECTED'}
                    onClick={act(a.id, () => api.setStatus(a.id, 'REJECTED'))}>Reject</button>
          </div>
        </div>
      ))}
    </div>
  )
}
