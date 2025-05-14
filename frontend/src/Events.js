import React, { useState, useEffect } from 'react';
import axios from 'axios';
import './App.css';

function Events() {
  const [events, setEvents] = useState([]);
  const [festivals, setFestivals] = useState([]);
  const [scenes, setScenes] = useState([]);
  const [editingId, setEditingId] = useState(null);
  const [formData, setFormData] = useState({
    festivalId: '',
    sceneId: '',
    eventDate: ''
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
      
      console.log('Fetching events data from backend...');
      
      // Make individual requests with more specific error handling
      try {
        const eventsRes = await axios.get('/api/events');
        console.log('Events data received:', eventsRes.data);
        setEvents(eventsRes.data);
      } catch (eventErr) {
        console.error('Failed to fetch events:', eventErr);
        setError('Failed to fetch events: ' + (eventErr.response?.data || eventErr.message));
        setIsLoading(false);
        return;
      }
      
      try {
        const festivalsRes = await axios.get('/api/festivals');
        console.log('Festivals data received:', festivalsRes.data);
        setFestivals(festivalsRes.data);
      } catch (festivalErr) {
        console.error('Failed to fetch festivals:', festivalErr);
        setError('Failed to fetch festivals: ' + (festivalErr.response?.data || festivalErr.message));
        setIsLoading(false);
        return;
      }
      
      try {
        const scenesRes = await axios.get('/api/scenes');
        console.log('Scenes data received:', scenesRes.data);
        setScenes(scenesRes.data);
      } catch (sceneErr) {
        console.error('Failed to fetch scenes:', sceneErr);
        setError('Failed to fetch scenes: ' + (sceneErr.response?.data || sceneErr.message));
        setIsLoading(false);
        return;
      }
      
      console.log('All data fetched successfully');
    } catch (err) {
      console.error('Error in fetchData:', err);
      setError('Failed to fetch data: ' + err.message);
    } finally {
      setIsLoading(false);
    }
  };

  const handleEdit = (event) => {
    setEditingId(event.eventId);
    setFormData({
      festivalId: event.festival?.festivalId || '',
      sceneId: event.scene?.sceneId || '',
      eventDate: event.eventDate || ''
    });
  };

  const handleCancel = () => {
    setEditingId(null);
    setFormData({
      festivalId: '',
      sceneId: '',
      eventDate: ''
    });
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
        festival: { festivalId: Number(formData.festivalId) },
        scene: { sceneId: Number(formData.sceneId) },
        eventDate: formData.eventDate
      };
      
      if (editingId) {
        await axios.put(`/api/events/${editingId}`, payload);
      } else {
        await axios.post('/api/events', payload);
      }
      
      handleCancel();
      fetchData();
    } catch (err) {
      console.error('Error saving event:', err);
      setError('Failed to save event. Please try again.');
    } finally {
      setIsSubmitting(false);
    }
  };

  const handleDelete = async (id) => {
    if (!window.confirm('Are you sure you want to delete this event?')) {
      return;
    }
    
    try {
      setError(null);
      await axios.delete(`/api/events/${id}`);
      fetchData();
    } catch (err) {
      console.error('Error deleting event:', err);
      setError('Failed to delete event. Please try again.');
    }
  };

  const getFestivalName = (festival) => {
    if (!festival) return 'Unknown Festival';
    return `${festival.year} Festival`;
  };

  const getSceneName = (scene) => {
    if (!scene) return 'Unknown Scene';
    return scene.name;
  };

  return (
    <div className="App">
      <h1>Events</h1>
      
      {error && <div className="error-message">{error}</div>}
      
      <form onSubmit={handleSubmit} className="artist-form">
        <select 
          name="festivalId" 
          value={formData.festivalId} 
          onChange={handleChange} 
          required 
          disabled={isSubmitting}
        >
          <option value="">Select Festival</option>
          {festivals.map(f => (
            <option key={f.festivalId} value={f.festivalId}>
              {f.year} Festival ({new Date(f.startDate).toLocaleDateString()} - {new Date(f.endDate).toLocaleDateString()})
            </option>
          ))}
        </select>
        
        <select 
          name="sceneId" 
          value={formData.sceneId} 
          onChange={handleChange} 
          required 
          disabled={isSubmitting}
        >
          <option value="">Select Scene</option>
          {scenes.map(s => (
            <option key={s.sceneId} value={s.sceneId}>
              {s.name} (Capacity: {s.maxCapacity})
            </option>
          ))}
        </select>
        
        <input
          type="date"
          name="eventDate"
          placeholder="Event Date"
          value={formData.eventDate}
          onChange={handleChange}
          required
          disabled={isSubmitting}
        />
        
        <button 
          type="submit" 
          disabled={isSubmitting}
        >
          {isSubmitting ? 'Saving...' : (editingId ? 'Update Event' : 'Add Event')}
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
        <div className="loading">Loading events...</div>
      ) : (
        <table className="artist-table">
          <thead>
            <tr>
              <th>ID</th>
              <th>Festival</th>
              <th>Scene</th>
              <th>Date</th>
              <th>Actions</th>
            </tr>
          </thead>
          <tbody>
            {events.map(e => (
              <tr key={e.eventId}>
                <td>{e.eventId}</td>
                <td>{getFestivalName(e.festival)}</td>
                <td>{getSceneName(e.scene)}</td>
                <td>{new Date(e.eventDate).toLocaleDateString()}</td>
                <td>
                  <button onClick={() => handleEdit(e)}>Edit</button>
                  <button onClick={() => handleDelete(e.eventId)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}
    </div>
  );
}

export default Events;
