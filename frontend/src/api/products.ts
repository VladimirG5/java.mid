import client from './client'

export interface Product {
  id: number
  title: string
  description: string
  price: number
  thumbnail?: string
}

export interface ProductPage {
  content: Product[]
  page: number
  size: number
  total: number
  totalPages: number
}

export const getProducts = (page = 0, size = 10, sortBy = 'id', order = 'asc') =>
  client.get<ProductPage>('/products', { params: { page, size, sortBy, order } });

export const getProduct = (id: string | number) =>
  client.get<Product>(`/products/${id}`);

export const getFavorites = () =>
  client.get<Product[]>('/products/favorites');

export const addToFavorites = (id: string | number) =>
  client.post(`/products/${id}/favorite`);

export const removeFromFavorites = (id: string | number) =>
  client.delete(`/products/${id}/favorite`);
