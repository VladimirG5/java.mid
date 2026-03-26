import { useEffect, useState } from 'react'
import axios from 'axios'
import { getCart, removeFromCart, addToCart, decreaseCartItemQuantity, type Cart } from '../api/cart'
import Toast from '../components/Toast'

export default function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [error, setError] = useState('');
  const [toast, setToast] = useState('');

  const load = () =>
    getCart().then(({ data }) => setCart(data)).catch(() => setError('Failed to load cart'));

  useEffect(() => { load() }, []);

  const handleRemove = async (productId: number) => {
    try {
      const { data } = await removeFromCart(productId);
      setCart(data);
    } catch {
      setError('Failed to remove item');
    }
  }

  const handleIncrease = async (productId: number) => {
    try {
      const { data } = await addToCart(productId, 1);
      setCart(data);
    } catch {
      setError('Failed to update quantity');
    }
  }

  const handleDecrease = async (productId: number) => {
    try {
      const { data } = await decreaseCartItemQuantity(productId);
      setCart(data);
    } catch (err) {
      if (axios.isAxiosError(err) && err.response?.status === 422) {
        setToast(err.response.data?.message ?? 'Cannot decrease quantity below zero');
      } else {
        setError('Failed to update quantity');
      }
    }
  }

  if (error) return <p className="error">{error}</p>
  if (!cart) return <p>Loading...</p>

  return (
    <div className="container">
      <h2>Cart ({cart.totalItems} items)</h2>
      {cart.items.length === 0 && <p>Your cart is empty.</p>}
      {toast && <Toast message={toast} onClose={() => setToast('')} />}
      <div className="cart-list">
        {cart.items.map(item => (
          <div key={item.productId} className="cart-item">
            {item.product?.thumbnail && (
              <img src={item.product.thumbnail} alt={item.product?.title} />
            )}
            <div className="cart-item-info">
              <h4>{item.product?.title ?? `Product #${item.productId}`}</h4>
              <p>${item.product?.price}</p>
            </div>
            <div className="cart-item-controls">
              <button onClick={() => handleIncrease(item.productId)}>+</button>
              <span>{item.quantity}</span>
              <button onClick={() => handleDecrease(item.productId)}>-</button>
              <button onClick={() => handleRemove(item.productId)} className="remove-btn">
                Remove
              </button>
            </div>
          </div>
        ))}
      </div>
    </div>
  )
}
