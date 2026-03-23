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
  totalPages: number
  totalElements: number
  number: number
}

export const getProducts = (page = 0, size = 12) =>
  client.get<ProductPage>('/products', { params: { page, size } });

export const getProduct = (id: string | number) =>
  client.get<Product>(`/products/${id}`);

export const getFavorites = () =>
  client.get<Product[]>('/products/favorites');

export const addToFavorites = (id: string | number) =>
  client.post(`/products/${id}/favorite`);

export const removeFromFavorites = (id: string | number) =>
  client.delete(`/products/${id}/favorite`);
