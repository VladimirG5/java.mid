import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getProducts, addToFavorites, removeFromFavorites, getFavorites, type Product } from '../api/products'
import { addToCart } from '../api/cart'

export default function ProductsPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [favorites, setFavorites] = useState<number[]>([]);
  const [page, setPage] = useState(0);
  const [totalPages, setTotalPages] = useState(0);
  const [error, setError] = useState('');

  const load = (p: number) =>
    getProducts(p)
      .then(({ data }) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
      })
      .catch(() => setError('Failed to load products'));

  useEffect(() => {
    load(page)
    getFavorites()
      .then(({ data }) => setFavorites(data.map(p => p.id)))
      .catch(() => {})
  }, [page]);

  const toggleFavorite = async (id: number) => {
    try {
      if (favorites.includes(id)) {
        await removeFromFavorites(id);
        setFavorites(prev => prev.filter(f => f !== id));
      } else {
        await addToFavorites(id);
        setFavorites(prev => [...prev, id]);
      }
    } catch {
      setError('Failed to update favorites');
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
      <h2>Products</h2>
      {error && <p className="error">{error}</p>}
      <div className="product-grid">
        {products.map(p => (
          <div key={p.id} className="card">
            {p.thumbnail && <img src={p.thumbnail} alt={p.title} />}
            <h3 onClick={() => navigate(`/products/${p.id}`)} className="clickable">{p.title}</h3>
            <p className="price">${p.price}</p>
            <div className="card-actions">
              <button onClick={() => toggleFavorite(p.id)}>
                {favorites.includes(p.id) ? '♥' : '♡'}
              </button>
              <button onClick={() => handleAddToCart(p.id)}>Add to cart</button>
            </div>
          </div>
        ))}
      </div>
      <div className="pagination">
        <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>Prev</button>
        <span>{page + 1} / {totalPages}</span>
        <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next</button>
      </div>
    </div>
  )
}
