import { useEffect, useState } from 'react'
import { useParams, useNavigate } from 'react-router-dom'
import { getProduct, addToFavorites, removeFromFavorites, getFavorites, type Product } from '../api/products'
import { addToCart } from '../api/cart'

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [product, setProduct] = useState<Product | null>(null);
  const [isFavorite, setIsFavorite] = useState(false);
  const [error, setError] = useState('');
  const [cartMsg, setCartMsg] = useState('');

  useEffect(() => {
    if (!id) return
    getProduct(id).then(({ data }) => setProduct(data)).catch(() => setError('Product not found'));
    getFavorites()
      .then(({ data }) => setIsFavorite(data.some(p => p.id === Number(id))))
      .catch(() => {});
  }, [id]);

  const toggleFavorite = async () => {
    if (!id) return;
    try {
      if (isFavorite) {
        await removeFromFavorites(id);
        setIsFavorite(false);
      } else {
        await addToFavorites(id);
        setIsFavorite(true);
      }
    } catch {
      setError('Failed to update favorites');
    }
  }

  const handleAddToCart = async () => {
    if (!id) return;
    try {
      await addToCart(Number(id), 1)
      setCartMsg('Added to cart!');
      setTimeout(() => setCartMsg(''), 2000);
    } catch {
      setError('Failed to add to cart');
    }
  }

  if (error) return <p className="error">{error}</p>
  if (!product) return <p>Loading...</p>

  return (
    <div className="container">
      <button onClick={() => navigate(-1)} className="back-btn">← Back</button>
      <div className="product-detail">
        {product.thumbnail && <img src={product.thumbnail} alt={product.title} />}
        <div className="product-info">
          <h2>{product.title}</h2>
          <p>{product.description}</p>
          <p className="price">${product.price}</p>
          {cartMsg && <p className="success">{cartMsg}</p>}
          <div className="card-actions">
            <button onClick={toggleFavorite}>{isFavorite ? '♥ Unfavorite' : '♡ Favorite'}</button>
            <button onClick={handleAddToCart}>Add to cart</button>
          </div>
        </div>
      </div>
    </div>
  )
}
