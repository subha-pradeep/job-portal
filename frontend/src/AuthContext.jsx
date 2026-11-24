import { createContext, useContext, useEffect, useState } from 'react'
import { api, tokenStore } from './api.js'

// Shares the logged-in user with every component
const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(null)
  const [loading, setLoading] = useState(true)

  // On page load, if a token is saved, ask the backend who we are
  useEffect(() => {
    if (!tokenStore.get()) {
      setLoading(false)
      return
    }
    api.me()
      .then(setUser)
      .catch(() => tokenStore.clear())
      .finally(() => setLoading(false))
  }, [])

  const saveSession = ({ token, user }) => {
    tokenStore.set(token)
    setUser(user)
    return user
  }

  const value = {
    user,
    loading,
    login: async (email, password) => saveSession(await api.login({ email, password })),
    register: async (data) => saveSession(await api.register(data)),
    logout: () => {
      tokenStore.clear()
      setUser(null)
    },
  }

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
