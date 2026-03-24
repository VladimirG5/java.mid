import { useEffect, useState } from 'react'
import { useNavigate } from 'react-router-dom'
import { getProducts, addToFavorites, removeFromFavorites, getFavorites, type Product } from '../api/products'
import { addToCart } from '../api/cart'

const PAGE_SIZE_OPTIONS = [5, 10, 20, 50];
const SORT_FIELDS = ['id', 'title', 'price'];

export default function ProductsPage() {
  const navigate = useNavigate();
  const [products, setProducts] = useState<Product[]>([]);
  const [favorites, setFavorites] = useState<number[]>([]);
  const [page, setPage] = useState(0);
  const [size, setSize] = useState(10);
  const [sortBy, setSortBy] = useState('id');
  const [order, setOrder] = useState<'asc' | 'desc'>('asc');
  const [totalPages, setTotalPages] = useState(0);
  const [total, setTotal] = useState(0);
  const [error, setError] = useState('');

  const load = (p: number, s: number, sb: string, o: string) =>
    getProducts(p, s, sb, o)
      .then(({ data }) => {
        setProducts(data.content);
        setTotalPages(data.totalPages);
        setTotal(data.total);
      })
      .catch(() => setError('Failed to load products'));

  useEffect(() => {
    load(page, size, sortBy, order);
    getFavorites()
      .then(({ data }) => setFavorites(data.map(p => p.id)))
      .catch(() => {})
  }, [page, size, sortBy, order]);

  const handleSizeChange = (newSize: number) => {
    setSize(newSize);
    setPage(0);
  };

  const handleSortChange = (field: string) => {
    if (field === sortBy) {
      setOrder(o => o === 'asc' ? 'desc' : 'asc');
    } else {
      setSortBy(field);
      setOrder('asc');
    }
    setPage(0);
  };

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
      <div className="products-toolbar">
        <span className="products-total">{total} products</span>
        <div className="toolbar-controls">
          <div className="sort-controls">
            <span>Sort by:</span>
            {SORT_FIELDS.map(field => (
              <button
                key={field}
                className={`sort-btn${sortBy === field ? ' active' : ''}`}
                onClick={() => handleSortChange(field)}
              >
                {field}
                {sortBy === field && (
                  <span className="sort-arrow">{order === 'asc' ? ' ▲' : ' ▼'}</span>
                )}
              </button>
            ))}
          </div>
          <div className="size-controls">
            <span>Per page:</span>
            <select value={size} onChange={e => handleSizeChange(Number(e.target.value))}>
              {PAGE_SIZE_OPTIONS.map(s => (
                <option key={s} value={s}>{s}</option>
              ))}
            </select>
          </div>
        </div>
      </div>
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
        <button disabled={page === 0} onClick={() => setPage(0)}>«</button>
        <button disabled={page === 0} onClick={() => setPage(p => p - 1)}>Prev</button>
        <span>Page {page + 1} of {totalPages}</span>
        <button disabled={page >= totalPages - 1} onClick={() => setPage(p => p + 1)}>Next</button>
        <button disabled={page >= totalPages - 1} onClick={() => setPage(totalPages - 1)}>»</button>
      </div>
    </div>
  )
}