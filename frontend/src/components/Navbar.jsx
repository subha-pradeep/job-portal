import { Link, NavLink, useNavigate } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

export default function Navbar() {
  const { user, logout } = useAuth()
  const navigate = useNavigate()

  const handleLogout = () => {
    logout()
    navigate('/jobs')
  }

  return (
    <header className="navbar">
      <div className="container navbar-inner">
        <Link to="/jobs" className="brand">HireSense</Link>
        <nav>
          <NavLink to="/jobs">Jobs</NavLink>
          {user?.role === 'CANDIDATE' && <NavLink to="/my-applications">My applications</NavLink>}
          {user?.role === 'RECRUITER' && <NavLink to="/recruiter">Dashboard</NavLink>}
          {user ? (
            <>
              <span className="muted">Hi, {user.fullName.split(' ')[0]}</span>
              <button className="btn btn-light" onClick={handleLogout}>Log out</button>
            </>
          ) : (
            <>
              <NavLink to="/login">Log in</NavLink>
              <Link to="/register" className="btn">Sign up</Link>
            </>
          )}
        </nav>
      </div>
    </header>
  )
}
