import client from './client'

export interface LoginRequest {
  username: string
  password: string
}

export interface RegisterRequest {
  username: string
  email: string
  password: string
  firstName?: string
  lastName?: string
}

export interface AuthResponse {
  token: string
}

export const login = (data: LoginRequest) =>
  client.post<AuthResponse>('/auth/login', data);

export const register = (data: RegisterRequest) =>
  client.post<AuthResponse>('/auth/register', data);
