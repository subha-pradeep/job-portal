import { Navigate, useLocation } from 'react-router-dom'
import { useAuth } from '../AuthContext.jsx'

// Only shows the page to logged-in users with the right role.
// (The backend checks again; this is just for a nicer experience.)
export default function ProtectedRoute({ role, children }) {
  const { user, loading } = useAuth()
  const location = useLocation()

  if (loading) return <p>Loading…</p>
  if (!user) return <Navigate to="/login" replace state={{ from: location.pathname }} />
  if (role && user.role !== role) return <p className="error">This page is for {role.toLowerCase()}s only.</p>
  return children
}
