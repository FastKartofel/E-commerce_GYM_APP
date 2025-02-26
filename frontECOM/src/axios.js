// src/axios.js
import axios from 'axios';

// Create an Axios instance
const axiosInstance = axios.create({
    baseURL: 'https://ecommerce-backend-app.azurewebsites.net', // Update if your backend runs elsewhere
    headers: {
        'Content-Type': 'application/json',
    },
});

// Add a request interceptor to include the JWT token
axiosInstance.interceptors.request.use(
    (config) => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    (error) => Promise.reject(error)
);

// Optional: Add a response interceptor for global error handling
axiosInstance.interceptors.response.use(
    (response) => response,
    (error) => {
        if (error.response && error.response.status === 401) {
            // Handle unauthorized access, e.g., redirect to login
            // You might need to integrate with your AuthContext here
        }
        return Promise.reject(error);
    }
);

export default axiosInstance;
