// src/components/Navbar.js
import React from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { FaDumbbell } from 'react-icons/fa'; // Gym-related icon

const Navbar = () => {
    const { isAuthenticated, logout } = useAuth();
    const navigate = useNavigate();

    const handleLogout = () => {
        logout();
        navigate('/login');
    };

    return (
        <nav style={styles.navbar}>
            <div style={styles.logo}>
                <FaDumbbell style={styles.icon} />
                <Link to="/main" style={styles.logoLink}>GymZone</Link>
            </div>
            <div style={styles.links}>
                {isAuthenticated ? (
                    <>
                        <Link to="/cart" style={styles.link}>Cart</Link>
                        <Link to="/profile" style={styles.link}>Profile</Link>
                        <button onClick={handleLogout} style={styles.logoutButton}>Logout</button>
                    </>
                ) : (
                    <>
                        <Link to="/login" style={styles.link}>Login</Link>
                        <Link to="/register" style={styles.link}>Register</Link>
                    </>
                )}
            </div>
        </nav>
    );
};

const styles = {
    navbar: {
        padding: '15px 30px',
        backgroundColor: '#222',
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        color: '#fff',
    },
    logo: {
        display: 'flex',
        alignItems: 'center',
        fontSize: '24px',
        fontWeight: 'bold',
    },
    logoLink: {
        textDecoration: 'none',
        color: '#fff',
        marginLeft: '10px',
    },
    icon: {
        fontSize: '30px',
        color: '#28a745',
    },
    links: {
        display: 'flex',
        gap: '15px',
    },
    link: {
        color: '#fff',
        textDecoration: 'none',
        fontSize: '18px',
        transition: 'color 0.2s',
    },
    logoutButton: {
        backgroundColor: 'transparent',
        border: 'none',
        color: '#fff',
        fontSize: '18px',
        cursor: 'pointer',
        transition: 'color 0.2s',
    },
};

export default Navbar;
