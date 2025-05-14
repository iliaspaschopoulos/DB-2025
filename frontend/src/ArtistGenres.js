import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function ArtistGenres() {
  const [artistGenres, setArtistGenres] = useState([]);
  const [artists, setArtists] = useState([]);
  const [editingKey, setEditingKey] = useState(null);
  const [formData, setFormData] = useState({
    artistId: '',
    genre: '',
    subgenre: ''
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const [genresRes, artistsRes] = await Promise.all([
        axios.get('http://localhost:8080/api/artist-genres'),
        axios.get('http://localhost:8080/api/artists')
      ]);
      
      setArtistGenres(genresRes.data);
      setArtists(artistsRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
      setError('Failed to fetch data. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (ag) => {
    setEditingKey(`${ag.artistId}-${ag.genre}`);
    setFormData({
      artistId: ag.artistId,
      genre: ag.genre,
      subgenre: ag.subgenre || ''
    });
  };

  const handleCancel = () => {
    setEditingKey(null);
    setFormData({ artistId: '', genre: '', subgenre: '' });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setIsSubmitting(true);
      setError(null);
      
      const payload = {
        id: {
          artistId: Number(formData.artistId),
          genre: formData.genre
        },
        subgenre: formData.subgenre || null
      };
      
      if (editingKey) {
        await axios.put(`http://localhost:8080/api/artist-genres/update`, null, {
          params: { 
            artistId: payload.id.artistId, 
            genre: payload.id.genre 
          },
          data: payload
        });
      } else {
        await axios.post('http://localhost:8080/api/artist-genres', payload);
      }
      
      handleCancel();
      fetchData();
    } catch (err) {
      console.error('Error saving artist genre:', err);
      setError('Failed to save artist genre. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (artistId, genre) => {
    if (!window.confirm('Are you sure you want to delete this artist genre?')) {
      return;
    }
    
    try {
      setError(null);
      await axios.delete(`http://localhost:8080/api/artist-genres/delete`, {
        params: { artistId, genre }
      });
      fetchData();
    } catch (err) {
      console.error('Error deleting artist genre:', err);
      setError('Failed to delete artist genre. Please try again.');
    }
  };

  return (
    <div className="App">
      <h1>Artist Genres</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="artist-form">
        <select 
          name="artistId" 
          value={formData.artistId} 
          onChange={handleChange} 
          required 
          disabled={!!editingKey || isSubmitting}
        >
          <option value="">Select Artist</option>
          {artists.map(a => <option key={a.artistId} value={a.artistId}>{a.name}</option>)}
        </select>
        
        <input
          type="text"
          name="genre"
          placeholder="Genre"
          value={formData.genre}
          onChange={handleChange}
          required
          disabled={!!editingKey || isSubmitting}
        />
        
        <input
          type="text"
          name="subgenre"
          placeholder="Subgenre (optional)"
          value={formData.subgenre}
          onChange={handleChange}
          disabled={isSubmitting}
        />
        
        <button 
          type="submit" 
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Saving...' : (editingKey ? 'Update Genre' : 'Add Genre')}
        </button>
        
        {editingKey && (
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
        <div className="loading">Loading artist genres...</div>
      ) : (
        <table className="artist-table">
          <thead>
            <tr>
              <th>Artist</th>
              <th>Genre</th>
              <th>Subgenre</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {artistGenres.map(ag => (
              <tr key={`${ag.artistId}-${ag.genre}`}>
                <td>{artists.find(a => a.artistId === ag.artistId)?.name}</td>
                <td>{ag.genre}</td>
                <td>{ag.subgenre || '-'}</td>
                <td>
                  <button onClick={() => handleEdit(ag)}>Edit</button>
                  <button onClick={() => handleDelete(ag.artistId, ag.genre)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default ArtistGenres;
