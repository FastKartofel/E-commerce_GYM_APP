// src/components/ProductDetailsPage.js
import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ProductDetailsPage = () => {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    useEffect(() => {
        const fetchProductDetails = async () => {
            try {
                const response = await axios.get(`/api/products/${id}`);
                setProduct(response.data);
            } catch (err) {
                setError('Failed to load product details.');
                console.error(err);
            }
        };
        fetchProductDetails();
    }, [id]);

    const handleAddToCart = async () => {
        try {
            await axios.post('/api/cart/add', { productId: product.id, quantity });
            alert('Item added to cart');
            navigate('/cart');
        } catch (err) {
            setError('Failed to add to cart.');
            console.error(err);
        }
    };

    return (
        <div style={styles.container}>
            {error && <p style={{ color: 'red' }}>{error}</p>}
            {product ? (
                <div style={styles.product}>
                    <h2>{product.name}</h2>
                    <p>{product.description}</p>
                    <p>Price: ${product.price.toFixed(2)}</p>
                    <p>Stock: {product.stockQuantity}</p>
                    <div style={styles.quantityContainer}>
                        <label htmlFor="quantity">Quantity:</label>
                        <input
                            type="number"
                            id="quantity"
                            value={quantity}
                            onChange={(e) => setQuantity(parseInt(e.target.value))}
                            min="1"
                            max={product.stockQuantity}
                            style={styles.input}
                        />
                    </div>
                    <button onClick={handleAddToCart} style={styles.button}>Add to Cart</button>
                </div>
            ) : (
                <p>Loading product...</p>
            )}
        </div>
    );
};

const styles = {
    container: {
        padding: '20px',
    },
    product: {
        border: '1px solid #ccc',
        padding: '20px',
        borderRadius: '5px',
        maxWidth: '600px',
        margin: '0 auto',
    },
    quantityContainer: {
        display: 'flex',
        alignItems: 'center',
        marginBottom: '10px',
    },
    input: {
        marginLeft: '10px',
        padding: '5px',
        width: '60px',
    },
    button: {
        padding: '10px 20px',
        fontSize: '16px',
        cursor: 'pointer',
    },
};

export default ProductDetailsPage;
