import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import Home from './Home';
import Artists from './Artists';
import Bands from './Bands';
import BandMembers from './BandMembers';
import Navbar from './Navbar';

function App() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/artists" element={<Artists />} />
        <Route path="/bands" element={<Bands />} />
        <Route path="/band-members" element={<BandMembers />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
