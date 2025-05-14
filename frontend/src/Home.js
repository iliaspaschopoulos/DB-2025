import React from 'react';
import { Link } from 'react-router-dom';
import './App.css';

function Home() {
  const tables = [
    { name: 'Artists', path: 'artists', description: 'Manage artists and their information' },
    { name: 'Bands', path: 'bands', description: 'Manage bands and their details' },
    { name: 'Band Members', path: 'band-members', description: 'Manage associations between bands and artists' },
    { name: 'Artist Genres', path: 'artist-genres', description: 'Manage genres associated with artists' },
    { name: 'Events', path: 'events', description: 'Manage festival events and their venues' },
    { name: 'Performances', path: 'performances', description: 'Manage performances by artists or bands at events' }
  ];
  
  return (
    <div className="App">
      <div className="home-container">
        <h1>Music Festivals Database Management</h1>
        <p className="home-intro">
          Welcome to the DB-2025 music festivals database application. Use this interface to manage 
          artists, bands, events, performances, and their relationships in the database.
        </p>
        
        <div className="home-info">
          <h2>Managing Your Music Festival Database</h2>
          <p>This application allows you to:</p>
          <ul>
            <li>Create and manage artists, bands, and their relationships</li>
            <li>Track artist genres and musical styles</li>
            <li>Set up festival events at different scenes</li>
            <li>Schedule performances by artists or bands</li>
          </ul>
          <p>To get started, select one of the data entities below:</p>
        </div>
        
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
