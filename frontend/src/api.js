// Small wrapper around fetch: adds the JWT and turns API errors into readable messages.

const TOKEN_KEY = 'jobportal_token'

export const tokenStore = {
  get: () => localStorage.getItem(TOKEN_KEY),
  set: (token) => localStorage.setItem(TOKEN_KEY, token),
  clear: () => localStorage.removeItem(TOKEN_KEY),
}

async function request(path, { method = 'GET', body, isForm = false, raw = false } = {}) {
  const headers = {}
  const token = tokenStore.get()
  if (token) headers.Authorization = `Bearer ${token}`
  if (body && !isForm) headers['Content-Type'] = 'application/json'

  const res = await fetch(path, {
    method,
    headers,
    // FormData sets its own multipart Content-Type, so don't JSON-encode it
    body: body ? (isForm ? body : JSON.stringify(body)) : undefined,
  })

  if (res.status === 401) {
    tokenStore.clear()
  }
  if (!res.ok) {
    let message = `Request failed (${res.status})`
    try {
      const data = await res.json()
      if (data.fields) {
        message = Object.entries(data.fields).map(([field, msg]) => `${field} ${msg}`).join(', ')
      } else if (data.error) {
        message = data.error
      }
    } catch {
      /* response had no JSON body */
    }
    throw new Error(message)
  }

  if (raw) return res
  if (res.status === 204) return null
  return res.json()
}

export const api = {
  // Auth
  register: (data) => request('/api/auth/register', { method: 'POST', body: data }),
  login: (data) => request('/api/auth/login', { method: 'POST', body: data }),
  me: () => request('/api/users/me'),

  // Jobs
  listJobs: (keyword) => request(`/api/jobs${keyword ? `?keyword=${encodeURIComponent(keyword)}` : ''}`),
  getJob: (id) => request(`/api/jobs/${id}`),
  createJob: (data) => request('/api/jobs', { method: 'POST', body: data }),
  closeJob: (id) => request(`/api/jobs/${id}/close`, { method: 'PATCH' }),
  myJobs: () => request('/api/recruiter/jobs'),

  // Applications
  apply: (jobId, file) => {
    const form = new FormData()
    form.append('resume', file)
    return request(`/api/jobs/${jobId}/apply`, { method: 'POST', body: form, isForm: true })
  },
  myApplications: () => request('/api/candidate/applications'),
  applicants: (jobId) => request(`/api/recruiter/jobs/${jobId}/applications`),
  setStatus: (id, status) => request(`/api/recruiter/applications/${id}/status`, { method: 'PATCH', body: { status } }),
  rescreen: (id) => request(`/api/recruiter/applications/${id}/screen`, { method: 'POST' }),
  resumeBlob: async (id) => (await request(`/api/recruiter/applications/${id}/resume`, { raw: true })).blob(),
}
