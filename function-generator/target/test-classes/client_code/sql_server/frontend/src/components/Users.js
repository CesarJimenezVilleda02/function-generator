import React, { useState, useEffect } from 'react'
import { fetchUsers } from '../api'

const Users = () => {
  const [users, setUsers] = useState([]) // State for user results
  const [query, setQuery] = useState('') // State for user input in the search bar

  // Fetch all users on component mount
  useEffect(() => {
    fetchUsers()
      .then((data) => {
        console.log('Fetched all users:', data)
        setUsers(data) // Set the initial user data
      })
      .catch((error) => {
        console.error('Error fetching all users:', error)
      })
  }, []) // Empty dependency array ensures this runs only once

  // Function to handle search
  const handleSearch = () => {
    fetchUsers(query)
      .then((data) => {
        console.log('Fetched users:', data)
        setUsers(data) // Update user results with fetched data
      })
      .catch((error) => {
        console.error('Error fetching users:', error)
        setUsers([]) // Clear results on error
      })
  }

  return (
    <div>
      <h1>Users</h1>

      {/* Search Bar and Button */}
      <div style={{ marginBottom: '20px' }}>
        <input
          type='text'
          placeholder='Search users...'
          value={query}
          onChange={(e) => setQuery(e.target.value)} // Update query state
          style={{ padding: '10px', width: '300px', marginRight: '10px' }}
        />
        <button
          onClick={handleSearch}
          style={{
            padding: '10px 20px',
            cursor: 'pointer',
            backgroundColor: '#007bff',
            color: 'white',
            border: 'none',
            borderRadius: '5px'
          }}>
          Search
        </button>
      </div>

      {/* Display User Results */}
      <ul>
        {users.length > 0 ? (
          users.map((user) => (
            <li key={user.id}>
              {user.name} - {user.email} - {user.company} - {user.age}
            </li>
          ))
        ) : (
          <p>No users found. Try searching for something else.</p>
        )}
      </ul>
    </div>
  )
}

export default Users
