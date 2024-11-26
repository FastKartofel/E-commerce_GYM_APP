// src/components/CartPage.js
import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

const CartPage = () => {
    const [cartItems, setCartItems] = useState([]);
    const [error, setError] = useState(null);
    const navigate = useNavigate();
    const { token } = useAuth();

    useEffect(() => {
        const fetchCart = async () => {
            try {
                const response = await axios.get('/api/cart');
                setCartItems(response.data.items);
            } catch (err) {
                setError('Failed to load cart items.');
                console.error(err);
            }
        };
        fetchCart();
    }, []);

    const handleRemove = async (productId) => {
        try {
            await axios.delete(`/api/cart/remove?productId=${productId}`);
            setCartItems(cartItems.filter(item => item.product.id !== productId));
        } catch (err) {
            setError('Failed to remove item.');
            console.error(err);
        }
    };

    const handleCheckout = async () => {
        try {
            // Prepare cart items for the backend
            const formattedCartItems = cartItems.map(item => ({
                productId: item.product.id,
                quantity: item.quantity,
            }));

            const response = await axios.post('/api/orders/place', formattedCartItems, {
                headers: { Authorization: `Bearer ${token}` },
            });

            alert('Order placed successfully!');
            navigate('/products');

            // Optionally, clear the cart
            setCartItems([]);
        } catch (err) {
            if (err.response && err.response.data) {
                setError(err.response.data);
            } else {
                setError('Failed to place order.');
            }
            console.error(err);
        }
    };

    return (
        <div style={styles.container}>
            <h2>My Cart</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {cartItems.length === 0 ? (
                <p>Your cart is empty.</p>
            ) : (
                <>
                    <ul style={styles.list}>
                        {cartItems.map(item => (
                            <li key={item.product.id} style={styles.listItem}>
                                <span>{item.product.name} - Quantity: {item.quantity}</span>
                                <button onClick={() => handleRemove(item.product.id)} style={styles.button}>Remove</button>
                            </li>
                        ))}
                    </ul>
                    <button onClick={handleCheckout} style={styles.checkoutButton}>Checkout</button>
                </>
            )}
        </div>
    );
};

const styles = {
    container: {
        padding: '20px',
    },
    list: {
        listStyleType: 'none',
        padding: 0,
    },
    listItem: {
        display: 'flex',
        justifyContent: 'space-between',
        alignItems: 'center',
        padding: '10px 0',
        borderBottom: '1px solid #ccc',
    },
    button: {
        padding: '5px 10px',
        cursor: 'pointer',
    },
    checkoutButton: {
        marginTop: '20px',
        padding: '10px 20px',
        fontSize: '16px',
        cursor: 'pointer',
    },
};

export default CartPage;
