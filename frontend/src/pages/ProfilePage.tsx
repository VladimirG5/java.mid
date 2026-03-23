import { useEffect, useState } from 'react'
import { getCurrentUser, type User } from '../api/users'

export default function ProfilePage() {
  const [user, setUser] = useState<User | null>(null);
  const [error, setError] = useState('');

  useEffect(() => {
    getCurrentUser()
      .then(({ data }) => setUser(data))
      .catch(() => setError('Failed to load profile'));
  }, [])

  if (error) return <p className="error">{error}</p>
  if (!user) return <p>Loading...</p>

  return (
    <div className="container">
      <h2>Profile</h2>
      <div className="card">
        <p><strong>Username:</strong> {user.username}</p>
        <p><strong>Email:</strong> {user.email}</p>
        <p><strong>Name:</strong> {user.firstName} {user.lastName}</p>
      </div>
    </div>
  )
}
