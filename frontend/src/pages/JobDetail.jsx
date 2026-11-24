import { useEffect, useState } from 'react'
import { Link, useParams } from 'react-router-dom'
import { api } from '../api.js'
import { useAuth } from '../AuthContext.jsx'

export default function JobDetail() {
  const { id } = useParams()
  const { user } = useAuth()
  const [job, setJob] = useState(null)
  const [file, setFile] = useState(null)
  const [error, setError] = useState('')
  const [message, setMessage] = useState('')
  const [busy, setBusy] = useState(false)

  useEffect(() => {
    api.getJob(id).then(setJob).catch((err) => setError(err.message))
  }, [id])

  const handleApply = async (e) => {
    e.preventDefault()
    if (!file) return setError('Please choose your resume PDF')
    setError('')
    setBusy(true)
    try {
      await api.apply(id, file)
      setMessage('Application sent! Our AI is reviewing your resume. Check "My applications" for updates.')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  if (error && !job) return <p className="error">{error}</p>
  if (!job) return <p>Loading…</p>

  return (
    <div className="card">
      <Link to="/jobs" className="muted">← All jobs</Link>
      <h1>{job.title}</h1>
      <p className="muted">{job.company}{job.location ? ` · ${job.location}` : ''} · Posted by {job.recruiterName}</p>

      {job.requiredSkills && (
        <div className="tags">
          {job.requiredSkills.split(',').map((s) => s.trim()).filter(Boolean)
            .map((skill) => <span className="tag" key={skill}>{skill}</span>)}
        </div>
      )}

      <p className="description">{job.description}</p>

      <hr />
      {!job.open && <p className="muted">This job is closed.</p>}

      {job.open && !user && (
        <p><Link to="/login" state={{ from: `/jobs/${id}` }}>Log in</Link> as a candidate to apply.</p>
      )}

      {job.open && user?.role === 'CANDIDATE' && (
        message ? <p className="success">{message}</p> : (
          <form onSubmit={handleApply} className="form">
            <label>Upload your resume (PDF, max 5 MB)
              <input type="file" accept="application/pdf"
                     onChange={(e) => setFile(e.target.files[0])} />
            </label>
            {error && <p className="error">{error}</p>}
            <button className="btn" disabled={busy}>{busy ? 'Uploading…' : 'Apply now'}</button>
          </form>
        )
      )}
    </div>
  )
}
