import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function Artists() {
  const [artists, setArtists] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    stageName: '',
    dateOfBirth: '',
    website: '',
    instagramProfile: ''
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchArtists();
  }, []);

  const fetchArtists = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const res = await axios.get('http://localhost:8080/api/artists');
      setArtists(res.data);
    } catch (err) {
      console.error('Error fetching artists:', err);
      setError('Failed to fetch artist data. Please try again later.');
    } finally {
      setIsLoading(false);
    }
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
    try {
      setIsSubmitting(true);
      setError(null);
      
      if (editingId) {
        // Update existing artist
        await axios.put(`http://localhost:8080/api/artists/${editingId}`, formData);
      } else {
        // Create new artist
        await axios.post('http://localhost:8080/api/artists', formData);
      }
      
      handleCancel();
      fetchArtists();
    } catch (err) {
      console.error('Error saving artist:', err);
      setError('Failed to save artist data. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (window.confirm('Are you sure you want to delete this artist?')) {
      try {
        setError(null);
        await axios.delete(`http://localhost:8080/api/artists/${id}`);
        fetchArtists();
      } catch (err) {
        console.error('Error deleting artist:', err);
        setError('Failed to delete artist. Please try again.');
      }
    }
  };

  return (
    <div className="App">
      <h1>Artists</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="artist-form">
        <input
          type="text"
          name="name"
          placeholder="Name"
          value={formData.name}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        />
        <input
          type="text"
          name="stageName"
          placeholder="Stage Name"
          value={formData.stageName}
          onChange={handleChange}
          disabled={isSubmitting}
        />
        <input
          type="date"
          name="dateOfBirth"
          placeholder="Date of Birth"
          value={formData.dateOfBirth}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        />
        <input
          type="text"
          name="website"
          placeholder="Website"
          value={formData.website}
          onChange={handleChange}
          disabled={isSubmitting}
        />
        <input
          type="text"
          name="instagramProfile"
          placeholder="Instagram Profile"
          value={formData.instagramProfile}
          onChange={handleChange}
          disabled={isSubmitting}
        />
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : (editingId ? 'Update' : 'Create')} Artist
        </button>
        {editingId && (
          <button 
            type="button" 
            onClick={handleCancel}
            disabled={isSubmitting}
          >
            Cancel
          </button>
        )}
      </form>
      
      {isLoading ? (
        <div className="loading">Loading artists...</div>
      ) : (
      <table className="artist-table">
        <thead>
          <tr>
            <th>Name</th>
            <th>Stage Name</th>
            <th>Date of Birth</th>
            <th>Website</th>
            <th>Instagram</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {artists.map((a) => (
            <tr key={a.artistId}>
              <td>{a.name}</td>
              <td>{a.stageName}</td>
              <td>{a.dateOfBirth}</td>
              <td>
                {a.website && <a href={a.website} target="_blank" rel="noopener noreferrer">
                  {a.website}
                </a>}
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
      )}
    </div>
  );
}

export default Artists;
