import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function Bands() {
  const [bands, setBands] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    bandName: '',
    formationDate: '',
    website: ''
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  useEffect(() => {
    fetchBands();
  }, []);

  const fetchBands = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      const res = await axios.get('http://localhost:8080/api/bands');
      setBands(res.data);
    } catch (err) {
      console.error('Error fetching bands:', err);
      setError('Failed to fetch band data. Please try again later.');
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (band) => {
    setEditingId(band.bandId);
    setFormData({
      bandName: band.bandName || '',
      formationDate: band.formationDate || '',
      website: band.website || ''
    });
  };

  const handleCancel = () => {
    setEditingId(null);
    setFormData({ bandName: '', formationDate: '', website: '' });
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
        await axios.put(`http://localhost:8080/api/bands/${editingId}`, formData);
      } else {
        await axios.post('http://localhost:8080/api/bands', formData);
      }
      
      handleCancel();
      fetchBands();
    } catch (err) {
      console.error('Error saving band:', err);
      setError('Failed to save band data. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this band?')) {
      return;
    }
    
    try {
      setError(null);
      await axios.delete(`http://localhost:8080/api/bands/${id}`);
      fetchBands();
    } catch (err) {
      console.error('Error deleting band:', err);
      setError('Failed to delete band. Please try again.');
    }
  };

  return (
    <div className="App">
      <h1>Band Manager</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="artist-form">
        <input
          name="bandName"
          placeholder="Band Name"
          value={formData.bandName}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        />
        <input
          name="formationDate"
          type="date"
          placeholder="Formation Date"
          value={formData.formationDate}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        />
        <input
          name="website"
          placeholder="Website"
          value={formData.website}
          onChange={handleChange}
          disabled={isSubmitting}
        />
        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? 'Saving...' : (editingId ? 'Update' : 'Create')}
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
        <div className="loading">Loading bands...</div>
      ) : (
        <table className="artist-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Name</th>
              <th>Formation Date</th>
              <th>Website</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {bands.map((b) => (
              <tr key={b.bandId}>
                <td>{b.bandId}</td>
                <td>{b.bandName}</td>
                <td>{b.formationDate}</td>
                <td>
                  {b.website && (
                    <a href={b.website} target="_blank" rel="noreferrer">
                      {b.website}
                    </a>
                  )}
                </td>
                <td>
                  <button onClick={() => handleEdit(b)}>Edit</button>
                  <button onClick={() => handleDelete(b.bandId)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Bands;
