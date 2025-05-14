import React from 'react';
import { Link } from 'react-router-dom';
import './App.css';

function Home() {
  const tables = [
    { name: 'Artists', path: 'artists' },
    { name: 'Bands', path: 'bands' },
    { name: 'Band Members', path: 'band-members' }
  ];
  return (
    <div className="App">
      <h1>Database Tables</h1>
      <ul>
        {tables.map(table => (
          <li key={table.path}>
            <Link to={`/${table.path}`}>{table.name}</Link>
          </li>
        ))}
      </ul>
    </div>
  );
}

export default Home;
