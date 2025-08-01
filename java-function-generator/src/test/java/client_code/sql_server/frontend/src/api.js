import axios from 'axios'
import { FireError } from './components/Swal'

const BASE_URL = 'http://localhost:8080' // Your Java server's URL

/**
 * Fetch users from the server
 * @param {string} userQuery - Optional user query parameter
 * @returns {Promise<Object[]>} - Array of user objects
 */
export const fetchUsers = async (userQuery = '') => {
  try {
    const response = await axios.get(`${BASE_URL}/users`, {
      params: { userQuery }
    })
    return response.data
  } catch (error) {
    console.log(error)
    console.error('Error fetching users:', error)
    FireError(error.response.data.error)
    return []
  }
}

/**
 * Fetch books from the server
 * @param {string} userQuery - Optional user query parameter
 * @returns {Promise<Object[]>} - Array of book objects
 */
export const fetchBooks = async (userQuery = '') => {
  try {
    const response = await axios.get(`${BASE_URL}/books`, {
      params: { userQuery }
    })
    return response.data
  } catch (error) {
    console.error('Error fetching books:', error)
    FireError(error.response.data.error)
    return []
  }
}
