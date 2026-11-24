import { Navigate, Route, Routes } from 'react-router-dom'
import Navbar from './components/Navbar.jsx'
import ProtectedRoute from './components/ProtectedRoute.jsx'
import Login from './pages/Login.jsx'
import Register from './pages/Register.jsx'
import Jobs from './pages/Jobs.jsx'
import JobDetail from './pages/JobDetail.jsx'
import MyApplications from './pages/MyApplications.jsx'
import RecruiterDashboard from './pages/RecruiterDashboard.jsx'
import PostJob from './pages/PostJob.jsx'
import Applicants from './pages/Applicants.jsx'

export default function App() {
  return (
    <>
      <Navbar />
      <main className="container">
        <Routes>
          <Route path="/" element={<Navigate to="/jobs" replace />} />
          <Route path="/login" element={<Login />} />
          <Route path="/register" element={<Register />} />
          <Route path="/jobs" element={<Jobs />} />
          <Route path="/jobs/:id" element={<JobDetail />} />

          <Route path="/my-applications" element={
            <ProtectedRoute role="CANDIDATE"><MyApplications /></ProtectedRoute>
          } />

          <Route path="/recruiter" element={
            <ProtectedRoute role="RECRUITER"><RecruiterDashboard /></ProtectedRoute>
          } />
          <Route path="/recruiter/post-job" element={
            <ProtectedRoute role="RECRUITER"><PostJob /></ProtectedRoute>
          } />
          <Route path="/recruiter/jobs/:id/applicants" element={
            <ProtectedRoute role="RECRUITER"><Applicants /></ProtectedRoute>
          } />

          <Route path="*" element={<p>Page not found.</p>} />
        </Routes>
      </main>
    </>
  )
}
