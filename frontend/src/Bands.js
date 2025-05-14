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

  useEffect(() => {
    fetchBands();
  }, []);

  const fetchBands = async () => {
    const res = await axios.get('http://localhost:8080/api/bands');
    setBands(res.data);
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
    if (editingId) {
      await axios.put(`http://localhost:8080/api/bands/${editingId}`, formData);
    } else {
      await axios.post('http://localhost:8080/api/bands', formData);
    }
    handleCancel();
    fetchBands();
  };

  const handleDelete = async (id) => {
    await axios.delete(`http://localhost:8080/api/bands/${id}`);
    fetchBands();
  };

  return (
    <div className="App">
      <h1>Band Manager</h1>
      <form onSubmit={handleSubmit} className="artist-form">
        <input
          name="bandName"
          placeholder="Band Name"
          value={formData.bandName}
          onChange={handleChange}
          required
        />
        <input
          name="formationDate"
          type="date"
          placeholder="Formation Date"
          value={formData.formationDate}
          onChange={handleChange}
          required
        />
        <input
          name="website"
          placeholder="Website"
          value={formData.website}
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
    </div>
  );
}

export default Bands;
