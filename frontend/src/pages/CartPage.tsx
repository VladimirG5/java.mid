import { useEffect, useState } from 'react'
import { getCart, removeFromCart, addToCart, type Cart } from '../api/cart'

export default function CartPage() {
  const [cart, setCart] = useState<Cart | null>(null);
  const [error, setError] = useState('');

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

  const handleQuantityChange = async (productId: number, delta: number) => {
    try {
      const { data } = await addToCart(productId, delta);
      setCart(data);
    } catch {
      setError('Failed to update quantity');
    }
  }

  if (error) return <p className="error">{error}</p>
  if (!cart) return <p>Loading...</p>

  return (
    <div className="container">
      <h2>Cart ({cart.totalItems} items)</h2>
      {cart.items.length === 0 && <p>Your cart is empty.</p>}
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
              <button onClick={() => handleQuantityChange(item.productId, 1)}>+</button>
              <span>{item.quantity}</span>
              <button
                onClick={() =>
                  item.quantity === 1
                    ? handleRemove(item.productId)
                    : handleQuantityChange(item.productId, -1)
                }
              >
                -
              </button>
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
