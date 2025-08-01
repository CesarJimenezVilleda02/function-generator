import React, { useState, useEffect } from 'react'
import { fetchBooks } from '../api'

const Books = () => {
  const [books, setBooks] = useState([]) // State for book results
  const [query, setQuery] = useState('') // State for user input in the search bar

  // Fetch all books on component mount
  useEffect(() => {
    fetchBooks()
      .then((data) => {
        console.log('Fetched all books:', data)
        setBooks(data) // Set the initial book data
      })
      .catch((error) => {
        console.error('Error fetching all books:', error)
      })
  }, []) // Empty dependency array ensures this runs only once

  // Function to handle search
  const handleSearch = () => {
    fetchBooks(query)
      .then((data) => {
        console.log('Fetched books:', data)
        setBooks(data) // Update book results with fetched data
      })
      .catch((error) => {
        console.error('Error fetching books:', error)
        setBooks([]) // Clear results on error
      })
  }

  return (
    <div>
      <h1>Books</h1>

      {/* Search Bar and Button */}
      <div style={{ marginBottom: '20px' }}>
        <input
          type='text'
          placeholder='Search books...'
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

      {/* Display Book Results */}
      <ul>
        {books.length > 0 ? (
          books.map((book) => (
            <li key={book.id}>
              {book.title} by {book.author}. Published in {book.year}. Genre: {book.genre}
            </li>
          ))
        ) : (
          <p>No books found. Try searching for something else.</p>
        )}
      </ul>
    </div>
  )
}

export default Books
