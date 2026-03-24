import { Link, useNavigate } from 'react-router-dom'
import { useAuth } from '../context/AuthContext'

export default function Navbar() {
  const { isAuthenticated, username, logout } = useAuth();
  const navigate = useNavigate();

  const handleLogout = () => {
    logout();
    navigate('/login');
  }

  return (
    <nav className="navbar">
      <Link to="/products">Abysalto Online Shop</Link>
      {isAuthenticated ? (
        <>
          <Link to="/cart">Cart</Link>
          <Link to="/favorites">Favorites</Link>
          <Link to="/profile">Profile</Link>
          {username && <span className="nav-greeting">Hi, {username}</span>}
          <button onClick={handleLogout}>Logout</button>
        </>
      ) : (
        <>
          <Link to="/login">Login</Link>
          <Link to="/register">Register</Link>
        </>
      )}
    </nav>
  )
}
