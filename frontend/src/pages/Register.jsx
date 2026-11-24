import { useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

export default function Register() {
  const { register } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState({ fullName: '', email: '', password: '', role: 'CANDIDATE' })
  const [error, setError] = useState('')
  const [busy, setBusy] = useState(false)

  const update = (field) => (e) => setForm({ ...form, [field]: e.target.value })

  const handleSubmit = async (e) => {
    e.preventDefault()
    setError('')
    setBusy(true)
    try {
      const user = await register(form)
      navigate(user.role === 'RECRUITER' ? '/recruiter' : '/jobs', { replace: true })
    } catch (err) {
      setError(err.message)
    } finally {
      setBusy(false)
    }
  }

  return (
    <div className="card narrow">
      <h1>Create an account</h1>
      <form onSubmit={handleSubmit} className="form">
        <div className="role-toggle">
          {['CANDIDATE', 'RECRUITER'].map((role) => (
            <button type="button" key={role}
                    className={form.role === role ? 'active' : ''}
                    onClick={() => setForm({ ...form, role })}>
              {role === 'CANDIDATE' ? "I'm looking for a job" : "I'm hiring"}
            </button>
          ))}
        </div>
        <label>Full name
          <input required value={form.fullName} onChange={update('fullName')} />
        </label>
        <label>Email
          <input type="email" required value={form.email} onChange={update('email')} />
        </label>
        <label>Password (at least 6 characters)
          <input type="password" required minLength={6} value={form.password} onChange={update('password')} />
        </label>
        {error && <p className="error">{error}</p>}
        <button className="btn" disabled={busy}>{busy ? 'Creating…' : 'Sign up'}</button>
      </form>
      <p className="muted">Already have an account? <Link to="/login">Log in</Link></p>
    </div>
  )
}
