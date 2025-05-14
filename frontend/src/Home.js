import React from 'react';
import { Link } from 'react-router-dom';
import './App.css';

function Home() {
  const tables = ['Artists']; // Add more table names as needed
  return (
    <div className="App">
      <h1>Database Tables</h1>
      <ul>
        {tables.map(table => (
          <li key={table}>
            <Link to={`/${table.toLowerCase()}`}>{table}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Home;
