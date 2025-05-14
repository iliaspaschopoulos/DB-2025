import React from 'react';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import './App.css';
import Home from './Home';
import Artists from './Artists';
import Bands from './Bands';
import BandMembers from './BandMembers';
import ArtistGenres from './ArtistGenres';
import Performances from './Performances';
import Events from './Events';
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
        <Route path="/artist-genres" element={<ArtistGenres />} />
        <Route path="/performances" element={<Performances />} />
        <Route path="/events" element={<Events />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;
