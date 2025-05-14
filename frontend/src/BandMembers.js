import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function BandMembers() {
  const [members, setMembers] = useState([]);
  const [bands, setBands] = useState([]);
  const [artists, setArtists] = useState([]);
  const [editingKey, setEditingKey] = useState(null);
  const [formData, setFormData] = useState({ bandId: '', artistId: '' });

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const [mRes, bRes, aRes] = await Promise.all([
        axios.get('http://localhost:8080/api/band-members'),
        axios.get('http://localhost:8080/api/bands'),
        axios.get('http://localhost:8080/api/artists')
      ]);
      setMembers(mRes.data);
      setBands(bRes.data);
      setArtists(aRes.data);
    } catch (err) {
      console.error('Error fetching data:', err);
      alert('Failed to fetch Band Member data. See console for details.');
    }
  };

  const handleEdit = (m) => {
    setEditingKey(`${m.id.bandId}-${m.id.artistId}`);
    setFormData({
      bandId: m.id.bandId,
      artistId: m.id.artistId
    });
  };

  const handleCancel = () => {
    setEditingKey(null);
    setFormData({ bandId: '', artistId: '' });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    const payload = { 
      id: { 
        bandId: Number(formData.bandId), 
        artistId: Number(formData.artistId) 
      } 
    };
    if (editingKey) {
      await axios.put('http://localhost:8080/api/band-members', payload);
    } else {
      await axios.post('http://localhost:8080/api/band-members', payload);
    }
    handleCancel();
    fetchData();
  };

  const handleDelete = async (bandId, artistId) => {
    await axios.delete('http://localhost:8080/api/band-members/delete', { params: { bandId, artistId } });
    fetchData();
  };

  return (
    <div className="App">
      <h1>Band Members</h1>
      <form onSubmit={handleSubmit} className="artist-form">
        <select name="bandId" value={formData.bandId} onChange={handleChange} required disabled={!!editingKey}>
          <option value="">Select Band</option>
          {bands.map(b => <option key={b.bandId} value={b.bandId}>{b.bandName}</option>)}
        </select>
        <select name="artistId" value={formData.artistId} onChange={handleChange} required disabled={!!editingKey}>
          <option value="">Select Artist</option>
          {artists.map(a => <option key={a.artistId} value={a.artistId}>{a.name}</option>)}
        </select>
        <button type="submit">{editingKey ? 'Update Association' : 'Create Association'}</button>
        {editingKey && <button type="button" onClick={handleCancel}>Cancel</button>}
      </form>
      <table className="artist-table">
        <thead>
          <tr>
            <th>Band</th>
            <th>Artist</th>
            <th>Actions</th>
          </tr>
        </thead>
        <tbody>
          {members.map(m => (
            <tr key={`${m.id.bandId}-${m.id.artistId}`}> 
              <td>{bands.find(b => b.bandId === m.id.bandId)?.bandName}</td>
              <td>{artists.find(a => a.artistId === m.id.artistId)?.name}</td>
              <td>
                <button onClick={() => handleDelete(m.id.bandId, m.id.artistId)}>Delete</button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
}

export default BandMembers;
