import React, { useState } from 'react';
import { NavLink } from 'react-router-dom';
import './App.css';

function Navbar() {
  const [isOpen, setIsOpen] = useState(false);

  const toggleMenu = () => {
    setIsOpen(!isOpen);
  };

  return (
    <nav className="navbar">
      <div className="navbar-brand">
        <NavLink to="/">DB-2025 NTUA</NavLink>
      </div>
      
      <button className="navbar-toggle" onClick={toggleMenu}>
        <span className="navbar-toggle-icon"></span>
      </button>
      
      <div className={`navbar-links ${isOpen ? 'open' : ''}`}>
        <NavLink to="/" end className={({ isActive }) => isActive ? "active" : ""}>
          Home
        </NavLink>
        <NavLink to="/artists" className={({ isActive }) => isActive ? "active" : ""}>
          Artists
        </NavLink>
        <NavLink to="/bands" className={({ isActive }) => isActive ? "active" : ""}>
          Bands
        </NavLink>
        <NavLink to="/band-members" className={({ isActive }) => isActive ? "active" : ""}>
          Band Members
        </NavLink>
        <NavLink to="/artist-genres" className={({ isActive }) => isActive ? "active" : ""}>
          Artist Genres
        </NavLink>
        <NavLink to="/events" className={({ isActive }) => isActive ? "active" : ""}>
          Events
        </NavLink>
        <NavLink to="/performances" className={({ isActive }) => isActive ? "active" : ""}>
          Performances
        </NavLink>
      </div>
    </nav>
  );
}

export default Navbar;
