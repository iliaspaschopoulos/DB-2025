import React from 'react';
import { Link } from 'react-router-dom';
import './App.css';

function Home() {
  const tables = [
    { name: 'Artists', path: 'artists', description: 'Manage artists and their information' },
    { name: 'Bands', path: 'bands', description: 'Manage bands and their details' },
    { name: 'Band Members', path: 'band-members', description: 'Manage associations between bands and artists' }
  ];
  
  return (
    <div className="App">
      <div className="home-container">
        <h1>Music Database Management</h1>
        <p className="home-intro">
          Welcome to the DB-2025 music database application. Use this interface to manage 
          artists, bands, and their relationships in the database.
        </p>
        
        <div className="card-container">
          {tables.map(table => (
            <div className="card" key={table.path}>
              <h2>{table.name}</h2>
              <p>{table.description}</p>
              <Link to={`/${table.path}`} className="card-button">
                Open {table.name}
              </Link>
            </div>
          ))}
        </div>
      </div>
    </div>
  );
}

export default Home;
