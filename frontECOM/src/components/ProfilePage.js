// src/components/ProfilePage.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';
import './ProfilePage.css'; // Import the CSS file

const ProfilePage = () => {
    const [user, setUser] = useState({});
    const [error, setError] = useState(null);
    const { token } = useAuth();
    const [isEditing, setIsEditing] = useState(false);
    const [formData, setFormData] = useState({
        email: '',
        shippingAddress: '',
        paymentDetails: '',
    });
    const [successMessage, setSuccessMessage] = useState(null);
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        const fetchUserProfile = async () => {
            try {
                const response = await axios.get('/api/users/profile', {
                    headers: { Authorization: `Bearer ${token}` }
                });
                setUser(response.data);
                setFormData({
                    email: response.data.email || '',
                    shippingAddress: response.data.shippingAddress || '',
                    paymentDetails: response.data.paymentDetails || '',
                });
            } catch (err) {
                setError('Failed to load profile.');
                console.error(err);
            }
        };
        fetchUserProfile();
    }, [token]);

    const handleInputChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevData => ({
            ...prevData,
            [name]: value,
        }));
    };

    const handleEditToggle = () => {
        setIsEditing(!isEditing);
        setError(null);
        setSuccessMessage(null);
    };

    const handleFormSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        setError(null);
        setSuccessMessage(null);
        try {
            const response = await axios.put('/api/users/update', formData, {
                headers: { Authorization: `Bearer ${token}` }
            });
            setSuccessMessage('Profile updated successfully.');
            setIsEditing(false);
            // Refresh user data
            const refreshedUser = await axios.get('/api/users/profile', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUser(refreshedUser.data);
        } catch (err) {
            setError('Failed to update profile.');
            console.error(err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="profile-container">
            <div className="profile-card">
                <h2>User Profile</h2>
                {error && <p className="error-message">{error}</p>}
                {successMessage && <p className="success-message">{successMessage}</p>}
                {user ? (
                    <div className="profile-details">
                        {!isEditing ? (
                            <>
                                <p><strong>Username:</strong> {user.username}</p>
                                <p><strong>Email:</strong> {user.email}</p>
                                <p><strong>Shipping Address:</strong> {user.shippingAddress || 'N/A'}</p>
                                <p><strong>Payment Details:</strong> {user.paymentDetails ? '•••• •••• •••• ' + user.paymentDetails.slice(-4) : 'N/A'}</p>
                                <p><strong>Role:</strong> {user.role}</p>
                                <button onClick={handleEditToggle} className="edit-button">Edit Profile</button>
                            </>
                        ) : (
                            <form onSubmit={handleFormSubmit}>
                                <div className="form-group">
                                    <label htmlFor="email">Email:</label>
                                    <input
                                        type="email"
                                        id="email"
                                        name="email"
                                        value={formData.email}
                                        onChange={handleInputChange}
                                        required
                                        placeholder="Enter your email"
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="shippingAddress">Shipping Address:</label>
                                    <input
                                        type="text"
                                        id="shippingAddress"
                                        name="shippingAddress"
                                        value={formData.shippingAddress}
                                        onChange={handleInputChange}
                                        placeholder="Enter your shipping address"
                                    />
                                </div>
                                <div className="form-group">
                                    <label htmlFor="paymentDetails">Payment Details:</label>
                                    <input
                                        type="text"
                                        id="paymentDetails"
                                        name="paymentDetails"
                                        value={formData.paymentDetails}
                                        onChange={handleInputChange}
                                        placeholder="e.g., 1234 5678 9012 3456"
                                    />
                                </div>
                                <div style={{ display: 'flex', justifyContent: 'flex-end' }}>
                                    <button type="submit" className="save-button" disabled={loading}>
                                        {loading ? 'Saving...' : 'Save'}
                                    </button>
                                    <button type="button" onClick={handleEditToggle} className="cancel-button" disabled={loading}>
                                        Cancel
                                    </button>
                                </div>
                            </form>
                        )}
                    </div>
                ) : (
                    <p>Loading profile...</p>
                )}
            </div>
        </div>
    );
};

export default ProfilePage;
