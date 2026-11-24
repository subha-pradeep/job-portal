import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'

export default function RecruiterDashboard() {
  const [jobs, setJobs] = useState(null)
  const [error, setError] = useState('')

  const load = () => api.myJobs().then(setJobs).catch((err) => setError(err.message))
  useEffect(() => { load() }, [])

  const handleClose = async (id) => {
    if (!window.confirm('Close this job? Candidates will no longer be able to apply.')) return
    try {
      await api.closeJob(id)
      load()
    } catch (err) {
      setError(err.message)
    }
  }

  return (
    <div>
      <div className="row">
        <h1>My jobs</h1>
        <Link to="/recruiter/post-job" className="btn">+ Post a job</Link>
      </div>
      {error && <p className="error">{error}</p>}
      {!jobs && !error && <p>Loading…</p>}
      {jobs?.length === 0 && <p className="muted">You haven't posted any jobs yet.</p>}

      {jobs?.map((job) => (
        <div className="card row" key={job.id}>
          <div>
            <h3>{job.title}</h3>
            <p className="muted">
              {job.company} · {job.open ? 'Open' : 'Closed'} · Posted {new Date(job.createdAt).toLocaleDateString()}
            </p>
          </div>
          <div className="right">
            <Link to={`/recruiter/jobs/${job.id}/applicants`} className="btn">View applicants</Link>
            {job.open && <button className="btn btn-light" onClick={() => handleClose(job.id)}>Close</button>}
          </div>
        </div>
      ))}
    </div>
  )
}
