// src/components/LoginPage.js
import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import axiosInstance from '../axios';
import { FaDumbbell } from 'react-icons/fa'; // Gym-related icon

const LoginPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [error, setError] = useState(null);
    const { login } = useAuth();
    const navigate = useNavigate();
    const [loading, setLoading] = useState(false); // Loading state

    const handleLogin = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            console.log('Attempting to log in with:', { username, password });
            const response = await axiosInstance.post('/api/auth/login', { username, password }, { responseType: 'text' });
            const token = response.data.trim();
            console.log('Received token:', token);

            if (token) {
                login(token);
                navigate('/main');
            } else {
                setError('Invalid login response.');
            }
        } catch (err) {
            setError('Login failed. Please check your credentials.');
            console.error('Login Error:', err);
        } finally {
            setLoading(false);
        }
    };

    return (
        <div style={styles.container}>
            <h2>Login</h2>
            <form onSubmit={handleLogin} style={styles.form}>
                <input
                    type="text"
                    placeholder="Username"
                    value={username}
                    onChange={(e) => setUsername(e.target.value)}
                    required
                    style={styles.input}
                />
                <input
                    type="password"
                    placeholder="Password"
                    value={password}
                    onChange={(e) => setPassword(e.target.value)}
                    required
                    style={styles.input}
                />
                <button type="submit" style={styles.button} disabled={loading}>
                    {loading ? 'Logging in...' : 'Login'}
                </button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <button onClick={() => navigate('/register')} style={styles.linkButton}>Register</button>
        </div>
    );
};

const styles = {
    container: {
        maxWidth: '400px',
        margin: '50px auto',
        padding: '20px',
        border: '1px solid #ccc',
        borderRadius: '5px',
        boxShadow: '0 0 10px rgba(0,0,0,0.1)',
        backgroundColor: '#fff', // Added for better visibility
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
    },
    input: {
        marginBottom: '10px',
        padding: '10px',
        fontSize: '16px',
        borderRadius: '4px',
        border: '1px solid #ccc',
    },
    button: {
        padding: '10px',
        fontSize: '16px',
        cursor: 'pointer',
        backgroundColor: '#28a745',
        color: '#fff',
        border: 'none',
        borderRadius: '4px',
        transition: 'background-color 0.3s',
    },
    linkButton: {
        marginTop: '10px',
        padding: '10px',
        fontSize: '16px',
        cursor: 'pointer',
        backgroundColor: 'transparent',
        border: 'none',
        color: '#007BFF',
        textDecoration: 'underline',
    },
};

export default LoginPage;
