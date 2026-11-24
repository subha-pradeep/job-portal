import { useEffect, useState } from 'react'
import { Link } from 'react-router-dom'
import { api } from '../api.js'

export default function Jobs() {
  const [jobs, setJobs] = useState([])
  const [keyword, setKeyword] = useState('')
  const [loading, setLoading] = useState(true)
  const [error, setError] = useState('')

  const load = async (search = '') => {
    setLoading(true)
    setError('')
    try {
      setJobs(await api.listJobs(search))
    } catch (err) {
      setError(err.message)
    } finally {
      setLoading(false)
    }
  }

  useEffect(() => { load() }, [])

  return (
    <div>
      <h1>Open jobs</h1>
      <form className="search" onSubmit={(e) => { e.preventDefault(); load(keyword) }}>
        <input placeholder="Search by title, e.g. Java" value={keyword}
               onChange={(e) => setKeyword(e.target.value)} />
        <button className="btn">Search</button>
      </form>

      {loading && <p>Loading jobs…</p>}
      {error && <p className="error">{error}</p>}
      {!loading && !error && jobs.length === 0 && <p className="muted">No open jobs found.</p>}

      <div className="grid">
        {jobs.map((job) => (
          <Link to={`/jobs/${job.id}`} key={job.id} className="card job-card">
            <h3>{job.title}</h3>
            <p className="muted">{job.company}{job.location ? ` · ${job.location}` : ''}</p>
            {job.requiredSkills && (
              <div className="tags">
                {job.requiredSkills.split(',').map((s) => s.trim()).filter(Boolean).slice(0, 5)
                  .map((skill) => <span className="tag" key={skill}>{skill}</span>)}
              </div>
            )}
          </Link>
        ))}
      </div>
    </div>
  )
}
