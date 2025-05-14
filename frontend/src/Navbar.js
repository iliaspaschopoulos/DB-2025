import React from 'react';
import { NavLink } from 'react-router-dom';
import './App.css';

function Navbar() {
  return (
    <nav className="navbar">
      <ul className="nav-list">
        <li><NavLink to="/artists" activeClassName="active-link">Artists</NavLink></li>
        <li><NavLink to="/bands" activeClassName="active-link">Bands</NavLink></li>
        <li><NavLink to="/band-members" activeClassName="active-link">Band Members</NavLink></li>
      </ul>
    </nav>
  );
}

export default Navbar;
