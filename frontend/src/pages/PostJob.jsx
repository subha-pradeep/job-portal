import { useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { api } from '../api.js'

export default function PostJob() {
  const navigate = useNavigate()
  const [form, setForm] = useState({ title: '', company: '', location: '', requiredSkills: '', description: '' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      await api.createJob(form)
      navigate('/recruiter')
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card">
      <h1>Post a job</h1>
      <form onSubmit={handleSubmit} className="form">
        <label>Job title
          <input required value={form.title} onChange={update('title')} placeholder="Java Backend Developer" />
        </label>
        <label>Company
          <input required value={form.company} onChange={update('company')} />
        </label>
        <label>Location
          <input value={form.location} onChange={update('location')} placeholder="Bhubaneswar / Remote" />
        </label>
        <label>Required skills (comma separated)
          <input value={form.requiredSkills} onChange={update('requiredSkills')}
                 placeholder="Java, Spring Boot, MySQL, Docker" />
        </label>
        <label>Description (the AI compares resumes with this, so be specific)
          <textarea required minLength={30} rows={8} value={form.description} onChange={update('description')} />
        </label>
        {error && <p className="error">{error}</p>}
        <button className="btn" disabled={busy}>{busy ? 'Posting…' : 'Post job'}</button>
      </form>
    </div>
  )
}
