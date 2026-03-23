import { useState, FormEvent } from 'react'
import { useNavigate, Link } from 'react-router-dom'
import { register } from '../api/auth'
import { useAuth } from '../context/AuthContext'

interface RegisterForm {
  username: string
  email: string
  password: string
  firstName: string
  lastName: string
}

export default function RegisterPage() {
  const { saveToken } = useAuth()
  const navigate = useNavigate()
  const [form, setForm] = useState<RegisterForm>({
    username: '',
    email: '',
    password: '',
    firstName: '',
    lastName: '',
  })
  const [error, setError] = useState('')

  const handleSubmit = async (e: FormEvent) => {
    e.preventDefault()
    setError('')
    try {
      const { data } = await register(form)
      saveToken(data.token)
      navigate('/products')
    } catch (err: unknown) {
      const message = (err as { response?: { data?: { message?: string } } })
        .response?.data?.message
      setError(message ?? 'Registration failed')
    }
  }

  const set = (field: keyof RegisterForm) => (e: React.ChangeEvent<HTMLInputElement>) =>
    setForm({ ...form, [field]: e.target.value })

  return (
    <div className="form-container">
      <h2>Register</h2>
      {error && <p className="error">{error}</p>}
      <form onSubmit={handleSubmit}>
        <input placeholder="Username" value={form.username} onChange={set('username')} required />
        <input type="email" placeholder="Email" value={form.email} onChange={set('email')} required />
        <input type="password" placeholder="Password" value={form.password} onChange={set('password')} required />
        <input placeholder="First name" value={form.firstName} onChange={set('firstName')} />
        <input placeholder="Last name" value={form.lastName} onChange={set('lastName')} />
        <button type="submit">Register</button>
      </form>
      <p>Already have an account? <Link to="/login">Login</Link></p>
    </div>
  )
}
