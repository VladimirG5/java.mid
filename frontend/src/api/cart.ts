import client from './client'
import type { Product } from './products'

export interface CartItem {
  productId: number
  quantity: number
  product?: Product
}

export interface Cart {
  items: CartItem[]
  totalItems: number
}

export const getCart = () =>
  client.get<Cart>('/cart');

export const addToCart = (productId: number, quantity: number) =>
  client.post<Cart>('/cart/items', { productId, quantity });

export const decreaseCartItemQuantity = (productId: number) =>
  client.patch<Cart>(`/cart/items/${productId}/decrease`);

export const removeFromCart = (productId: number) =>
  client.delete<Cart>(`/cart/items/${productId}`);
