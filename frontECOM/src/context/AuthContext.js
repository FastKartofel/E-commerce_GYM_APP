// src/context/AuthContext.js
import React, { createContext, useContext, useState, useEffect } from 'react';
import axios from 'axios';

const AuthContext = createContext();

export const useAuth = () => useContext(AuthContext);

export const AuthProvider = ({ children }) => {
    const [user, setUser] = useState(null);
    const [token, setToken] = useState(null);
    const [loading, setLoading] = useState(true); // To handle initial loading state

    useEffect(() => {
        const storedToken = localStorage.getItem('token');
        if (storedToken) {
            console.log('Found stored token:', storedToken); // Debugging
            setToken(storedToken);
            // Fetch user details using the token
            fetchUserProfile(storedToken);
        } else {
            setLoading(false);
        }
    }, []);

    const fetchUserProfile = async (token) => {
        try {
            console.log('Fetching user profile with token:', token); // Debugging
            const response = await axios.get('/api/users/profile', {
                headers: { Authorization: `Bearer ${token}` }
            });
            setUser(response.data);
            console.log('Fetched user profile:', response.data); // Debugging
        } catch (err) {
            console.error('Failed to fetch user profile:', err);
            logout(); // Clear token and user if fetching profile fails
        } finally {
            setLoading(false);
        }
    };

    const login = (token) => {
        console.log('Logging in with token:', token); // Debugging
        setToken(token);
        localStorage.setItem('token', token);
        fetchUserProfile(token); // Fetch and set user details after login
    };

    const logout = () => {
        console.log('Logging out'); // Debugging
        setToken(null);
        setUser(null);
        localStorage.removeItem('token');
    };

    const isAuthenticated = !!token;

    return (
        <AuthContext.Provider value={{ user, token, isAuthenticated, login, logout, loading }}>
            {children}
        </AuthContext.Provider>
    );
};
