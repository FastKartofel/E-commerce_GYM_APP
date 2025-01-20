// src/index.js
import React from 'react';
import ReactDOM from 'react-dom/client';
import App from './App';
import axios from 'axios';
import './index.css';
import ErrorBoundary from './components/ErrorBoundary'; // If implemented

// Axios Configuration
axios.defaults.baseURL = 'http://localhost:8080/'; // Backend URL
axios.defaults.headers.post['Content-Type'] = 'application/json';

// Add a request interceptor to include JWT token in headers
axios.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
            console.log('Adding Authorization header:', `Bearer ${token}`); // Debugging
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Optional: Implement response interceptors for global error handling
axios.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access, e.g., redirect to login
            console.warn('Unauthorized access - redirecting to login.');
            // Optionally, trigger a global logout or show a notification
        }
        return Promise.reject(error);
    }
);

// Render the application
const root = ReactDOM.createRoot(document.getElementById('root'));
root.render(
    <React.StrictMode>
        <ErrorBoundary> {/* If implemented */}
            <App />
        </ErrorBoundary>
    </React.StrictMode>
);
