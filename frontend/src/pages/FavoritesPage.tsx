import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getFavorites, removeFromFavorites, type Product } from '../api/products'
import { addToCart } from '../api/cart'

export default function FavoritesPage() {
  const navigate = useNavigate();
  const [favorites, setFavorites] = useState<Product[]>([]);
  const [error, setError] = useState('');

  const load = () =>
    getFavorites()
      .then(({ data }) => setFavorites(data))
      .catch(() => setError('Failed to load favorites'));

  useEffect(() => { load() }, []);

  const handleRemove = async (id: number) => {
    try {
      await removeFromFavorites(id);
      setFavorites(prev => prev.filter(p => p.id !== id));
    } catch {
      setError('Failed to remove favorite');
    }
  }

  const handleAddToCart = async (id: number) => {
    try {
      await addToCart(id, 1);
    } catch {
      setError('Failed to add to cart');
    }
  }

  return (
    <div className="container">
      <h2>Favorites</h2>
      {error && <p className="error">{error}</p>}
      {favorites.length === 0 && <p>No favorites yet.</p>}
      <div className="product-grid">
        {favorites.map(p => (
          <div key={p.id} className="card">
            {p.thumbnail && <img src={p.thumbnail} alt={p.title} />}
            <h3 onClick={() => navigate(`/products/${p.id}`)} className="clickable">{p.title}</h3>
            <p className="price">${p.price}</p>
            <div className="card-actions">
              <button onClick={() => handleRemove(p.id)}>♥ Unfavorite</button>
              <button onClick={() => handleAddToCart(p.id)}>Add to cart</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
