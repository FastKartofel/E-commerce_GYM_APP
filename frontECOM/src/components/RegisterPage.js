// src/components/RegisterPage.js
import React, { useState } from 'react';
import axios from 'axios';
import { useNavigate, Link } from 'react-router-dom';

const RegisterPage = () => {
    const [username, setUsername] = useState('');
    const [password, setPassword] = useState('');
    const [email, setEmail] = useState('');
    const [shippingAddress, setShippingAddress] = useState('');
    const [paymentDetails, setPaymentDetails] = useState('');
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const handleRegister = async (e) => {
        e.preventDefault();
        try {
            await axios.post('/api/users/register', { username, password, email, shippingAddress, paymentDetails });
            navigate('/login'); // Redirect to login page upon successful registration
        } catch (err) {
            setError('Registration failed. Please try again.');
            console.error(err);
        }
    };

    return (
        <div style={styles.container}>
            <h2>Register</h2>
            <form onSubmit={handleRegister} style={styles.form}>
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
                <input
                    type="email"
                    placeholder="Email"
                    value={email}
                    onChange={(e) => setEmail(e.target.value)}
                    required
                    style={styles.input}
                />
                <input
                    type="text"
                    placeholder="Shipping Address"
                    value={shippingAddress}
                    onChange={(e) => setShippingAddress(e.target.value)}
                    style={styles.input}
                />
                <input
                    type="text"
                    placeholder="Payment Details"
                    value={paymentDetails}
                    onChange={(e) => setPaymentDetails(e.target.value)}
                    style={styles.input}
                />
                <button type="submit" style={styles.button}>Register</button>
            </form>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            <p>Already have an account? <Link to="/login" style={styles.link}>Login here</Link></p>
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
    },
    form: {
        display: 'flex',
        flexDirection: 'column',
    },
    input: {
        marginBottom: '10px',
        padding: '10px',
        fontSize: '16px',
    },
    button: {
        padding: '10px',
        fontSize: '16px',
        cursor: 'pointer',
    },
    link: {
        color: '#007BFF',
        textDecoration: 'underline',
    },
};

export default RegisterPage;
