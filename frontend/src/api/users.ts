import client from './client'

export interface User {
  username: string
  email: string
  firstName: string
  lastName: string
}

export const getCurrentUser = () =>
  client.get<User>('/users/me')
