import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';
import { BrowserRouter, Routes, Route } from 'react-router-dom';
import Home from './Home';
import Bands from './Bands';
import BandMembers from './BandMembers';
import Navbar from './Navbar';

function App() {
  const [artists, setArtists] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    stageName: '',
    dateOfBirth: '',
    website: '',
    instagramProfile: ''
  });

  useEffect(() => {
    fetchArtists();
  }, []);

  const fetchArtists = async () => {
    const res = await axios.get('http://localhost:8080/api/artists');
    setArtists(res.data);
  };

  const handleEdit = (artist) => {
    setEditingId(artist.artistId);
    setFormData({
      name: artist.name || '',
      stageName: artist.stageName || '',
      dateOfBirth: artist.dateOfBirth || '',
      website: artist.website || '',
      instagramProfile: artist.instagramProfile || ''
    });
  };

  const handleCancel = () => {
    setEditingId(null);
    setFormData({ name: '', stageName: '', dateOfBirth: '', website: '', instagramProfile: '' });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (editingId) {
      await axios.put(`http://localhost:8080/api/artists/${editingId}`, formData);
    } else {
      await axios.post('http://localhost:8080/api/artists', formData);
    }
    handleCancel();
    fetchArtists();
  };

  const handleDelete = async (id) => {
    await axios.delete(`http://localhost:8080/api/artists/${id}`);
    fetchArtists();
  };

  return (
    <div className="App">
      <h1>Artist Manager</h1>
      <form onSubmit={handleSubmit} className="artist-form">
        <input
          name="name"
          placeholder="Name"
          value={formData.name}
          onChange={handleChange}
          required
        />
        <input
          name="stageName"
          placeholder="Stage Name"
          value={formData.stageName}
          onChange={handleChange}
        />
        <input
          name="dateOfBirth"
          type="date"
          placeholder="Date of Birth"
          value={formData.dateOfBirth}
          onChange={handleChange}
          required
        />
        <input
          name="website"
          placeholder="Website"
          value={formData.website}
          onChange={handleChange}
        />
        <input
          name="instagramProfile"
          placeholder="Instagram Profile"
          value={formData.instagramProfile}
          onChange={handleChange}
        />
        <button type="submit">{editingId ? 'Update' : 'Create'}</button>
        {editingId && <button type="button" onClick={handleCancel}>Cancel</button>}
      </form>

      <table className="artist-table">
        <thead>
          <tr>
            <th>ID</th>
            <th>Name</th>
            <th>Stage Name</th>
            <th>DOB</th>
            <th>Website</th>
            <th>Instagram</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {artists.map((a) => (
            <tr key={a.artistId}>
              <td>{a.artistId}</td>
              <td>{a.name}</td>
              <td>{a.stageName}</td>
              <td>{a.dateOfBirth}</td>
              <td>
                {a.website && (
                  <a href={a.website} target="_blank" rel="noreferrer">
                    {a.website}
                  </a>
                )}
              </td>
              <td>{a.instagramProfile}</td>
              <td>
                <button onClick={() => handleEdit(a)}>Edit</button>
                <button onClick={() => handleDelete(a.artistId)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

function AppRoutes() {
  return (
    <BrowserRouter>
      <Navbar />
      <Routes>
        <Route path="/" element={<Home />} />
        <Route path="/artists" element={<App />} />
        <Route path="/bands" element={<Bands />} />
        <Route path="/band-members" element={<BandMembers />} />
      </Routes>
    </BrowserRouter>
  );
}

export default AppRoutes;
