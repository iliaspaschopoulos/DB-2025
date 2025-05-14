import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function Performances() {
  const [performances, setPerformances] = useState([]);
  const [events, setEvents] = useState([]);
  const [artists, setArtists] = useState([]);
  const [bands, setBands] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    eventId: '',
    artistId: '',
    bandId: '',
    performanceType: '',
    startTime: '',
    duration: '',
    breakDuration: ''
  });
  const [isLoading, setIsLoading] = useState(true);
  const [error, setError] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);
  const [performerType, setPerformerType] = useState('artist'); // 'artist' or 'band'

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      setIsLoading(true);
      setError(null);
      
      console.log('Fetching performances data from backend...');
      
      // Make individual requests with more specific error handling
      try {
        const perfRes = await axios.get('/api/performances');
        console.log('Performances data received:', perfRes.data);
        setPerformances(perfRes.data);
      } catch (perfErr) {
        console.error('Failed to fetch performances:', perfErr);
        setError('Failed to fetch performances: ' + (perfErr.response?.data || perfErr.message));
        setIsLoading(false);
        return;
      }
      
      try {
        const eventRes = await axios.get('/api/events');
        console.log('Events data received:', eventRes.data);
        setEvents(eventRes.data);
      } catch (eventErr) {
        console.error('Failed to fetch events:', eventErr);
        setError('Failed to fetch events: ' + (eventErr.response?.data || eventErr.message));
        setIsLoading(false);
        return;
      }
      
      try {
        const artistRes = await axios.get('/api/artists');
        console.log('Artists data received:', artistRes.data);
        setArtists(artistRes.data);
      } catch (artistErr) {
        console.error('Failed to fetch artists:', artistErr);
        setError('Failed to fetch artists: ' + (artistErr.response?.data || artistErr.message));
        setIsLoading(false);
        return;
      }
      
      try {
        const bandRes = await axios.get('/api/bands');
        console.log('Bands data received:', bandRes.data);
        setBands(bandRes.data);
      } catch (bandErr) {
        console.error('Failed to fetch bands:', bandErr);
        setError('Failed to fetch bands: ' + (bandErr.response?.data || bandErr.message));
        setIsLoading(false);
        return;
      }
      
      console.log('All performance data fetched successfully');
    } catch (err) {
      console.error('Error in fetchData:', err);
      setError('Failed to fetch data: ' + err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (performance) => {
    setEditingId(performance.id);
    const performerType = performance.artist ? 'artist' : 'band';
    setPerformerType(performerType);
    
    setFormData({
      eventId: performance.event?.eventId || '',
      artistId: performance.artist?.artistId || '',
      bandId: performance.band?.bandId || '',
      performanceType: performance.performanceType || '',
      startTime: performance.startTime || '',
      duration: performance.duration || '',
      breakDuration: performance.breakDuration || ''
    });
  };

  const handleCancel = () => {
    setEditingId(null);
    setFormData({
      eventId: '',
      artistId: '',
      bandId: '',
      performanceType: '',
      startTime: '',
      duration: '',
      breakDuration: ''
    });
  };

  const handleChange = (e) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handlePerformerTypeChange = (e) => {
    const type = e.target.value;
    setPerformerType(type);
    // Reset the other type's ID when switching
    if (type === 'artist') {
      setFormData({ ...formData, artistId: '', bandId: '' });
    } else {
      setFormData({ ...formData, artistId: '', bandId: '' });
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    
    try {
      setIsSubmitting(true);
      setError(null);
      
      // Create the base payload
      const payload = {
        event: { eventId: Number(formData.eventId) },
        performanceType: formData.performanceType,
        startTime: formData.startTime,
        duration: formData.duration,
        breakDuration: formData.breakDuration || null
      };
      
      // Add either artist or band based on the selected performer type
      if (performerType === 'artist') {
        payload.artist = { artistId: Number(formData.artistId) };
        payload.band = null;
      } else {
        payload.band = { bandId: Number(formData.bandId) };
        payload.artist = null;
      }
      
      if (editingId) {
        await axios.put(`/api/performances/${editingId}`, payload);
      } else {
        await axios.post('/api/performances', payload);
      }
      
      handleCancel();
      fetchData();
    } catch (err) {
      console.error('Error saving performance:', err);
      setError('Failed to save performance. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this performance?')) {
      return;
    }
    
    try {
      setError(null);
      await axios.delete(`/api/performances/${id}`);
      fetchData();
    } catch (err) {
      console.error('Error deleting performance:', err);
      setError('Failed to delete performance. Please try again.');
    }
  };

  // Helper function to format time for display
  const formatTime = (timeString) => {
    if (!timeString) return '-';
    // Remove any trailing seconds if they're zeros
    return timeString.replace(/^(\d{2}:\d{2}):00$/, '$1');
  };

  const getPerformerName = (performance) => {
    if (performance.artist) {
      return performance.artist.name || 'Unknown Artist';
    } else if (performance.band) {
      return performance.band.bandName || 'Unknown Band';
    }
    return 'No performer assigned';
  };

  const getEventName = (event) => {
    if (!event) return 'Unknown Event';
    return `Event #${event.eventId} (${event.eventDate})`;
  };

  return (
    <div className="App">
      <h1>Performances</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="artist-form">
        <select 
          name="eventId" 
          value={formData.eventId} 
          onChange={handleChange} 
          required 
          disabled={isSubmitting}
        >
          <option value="">Select Event</option>
          {events.map(e => (
            <option key={e.eventId} value={e.eventId}>
              Event #{e.eventId} ({e.eventDate}) - {e.scene?.name || 'Unknown Scene'}
            </option>
          ))}
        </select>
        
        <div className="radio-group">
          <label>
            <input
              type="radio"
              name="performerType"
              value="artist"
              checked={performerType === 'artist'}
              onChange={handlePerformerTypeChange}
              disabled={!!editingId || isSubmitting}
            />
            Artist
          </label>
          <label>
            <input
              type="radio"
              name="performerType"
              value="band"
              checked={performerType === 'band'}
              onChange={handlePerformerTypeChange}
              disabled={!!editingId || isSubmitting}
            />
            Band
          </label>
        </div>
        
        {performerType === 'artist' ? (
          <select 
            name="artistId" 
            value={formData.artistId} 
            onChange={handleChange} 
            required 
            disabled={!!editingId || isSubmitting}
          >
            <option value="">Select Artist</option>
            {artists.map(a => (
              <option key={a.artistId} value={a.artistId}>{a.name}</option>
            ))}
          </select>
        ) : (
          <select 
            name="bandId" 
            value={formData.bandId} 
            onChange={handleChange} 
            required 
            disabled={!!editingId || isSubmitting}
          >
            <option value="">Select Band</option>
            {bands.map(b => (
              <option key={b.bandId} value={b.bandId}>{b.bandName}</option>
            ))}
          </select>
        )}
        
        <select
          name="performanceType"
          value={formData.performanceType}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        >
          <option value="">Select Performance Type</option>
          <option value="warm up">Warm Up</option>
          <option value="headline">Headline</option>
          <option value="Special guest">Special Guest</option>
        </select>
        
        <div className="time-inputs">
          <input
            type="time"
            name="startTime"
            placeholder="Start Time (HH:MM)"
            value={formData.startTime}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
          
          <input
            type="time"
            name="duration"
            placeholder="Duration (HH:MM)"
            value={formData.duration}
            onChange={handleChange}
            required
            disabled={isSubmitting}
          />
          
          <input
            type="time"
            name="breakDuration"
            placeholder="Break Duration (HH:MM) (optional)"
            value={formData.breakDuration || ''}
            onChange={handleChange}
            disabled={isSubmitting}
          />
        </div>
        
        <button 
          type="submit" 
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Saving...' : (editingId ? 'Update Performance' : 'Add Performance')}
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
        <div className="loading">Loading performances...</div>
      ) : (
        <table className="artist-table">
          <thead>
            <tr>
              <th>Event</th>
              <th>Performer</th>
              <th>Type</th>
              <th>Start Time</th>
              <th>Duration</th>
              <th>Break</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {performances.map(p => (
              <tr key={p.id}>
                <td>{getEventName(p.event)}</td>
                <td>{getPerformerName(p)}</td>
                <td>{p.performanceType}</td>
                <td>{formatTime(p.startTime)}</td>
                <td>{formatTime(p.duration)}</td>
                <td>{formatTime(p.breakDuration) || '-'}</td>
                <td>
                  <button onClick={() => handleEdit(p)}>Edit</button>
                  <button onClick={() => handleDelete(p.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Performances;
