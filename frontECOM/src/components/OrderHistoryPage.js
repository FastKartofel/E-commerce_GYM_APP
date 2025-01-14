import React, { useEffect, useState } from 'react';
import axios from 'axios';
import { useAuth } from '../context/AuthContext';

const OrderHistoryPage = () => {
    const [orders, setOrders] = useState([]);
    const [error, setError] = useState(null);
    const { token } = useAuth();

    useEffect(() => {
        const fetchOrders = async () => {
            try {
                const response = await axios.get('/api/orders/history', {
                    headers: { Authorization: `Bearer ${token}` },
                });
                setOrders(response.data); // Ensure response is an array
            } catch (err) {
                setError('Failed to fetch order history.');
                console.error(err);
            }
        };
        fetchOrders();
    }, [token]);

    return (
        <div>
            <h2>Order History</h2>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {orders.length > 0 ? (
                <ul>
                    {orders.map((order) => (
                        <li key={order.id}>
                            Order ID: {order.id} - Total: ${order.totalAmount.toFixed(2)} - Status: {order.status} -
                            Date: {new Date(order.orderDate).toLocaleDateString()}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>No order history found.</p>
            )}
        </div>
    );
};

export default OrderHistoryPage;
