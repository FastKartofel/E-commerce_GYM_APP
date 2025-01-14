import React, { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';

const ProductDetailsPage = () => {
    const { id } = useParams();
    const [product, setProduct] = useState(null);
    const [quantity, setQuantity] = useState(1);
    const [error, setError] = useState(null);
    const navigate = useNavigate();

    const [reviews, setReviews] = useState([]); // Reviews state
    const [reviewContent, setReviewContent] = useState(''); // Review content state
    const [reviewRating, setReviewRating] = useState(5); // Review rating state
    const [username, setUsername] = useState(''); // Logged-in user's username

    // Fetch reviews for the product
    const fetchReviews = async () => {
        try {
            const response = await axios.get(`/api/reviews/product/${id}`);
            setReviews(response.data); // Fetch ReviewResponseDTO objects
        } catch (err) {
            console.error('Failed to load reviews', err);
        }
    };

    useEffect(() => {
        // Fetch product details
        const fetchProductDetails = async () => {
            try {
                const response = await axios.get(`/api/products/${id}`);
                setProduct(response.data);
            } catch (err) {
                setError('Failed to load product details.');
                console.error(err);
            }
        };

        // Fetch the logged-in user's username
        const fetchUsername = async () => {
            try {
                const response = await axios.get('/api/users/me'); // Assumes a backend endpoint that returns the logged-in user's details
                setUsername(response.data.username);
            } catch (err) {
                console.error('Failed to fetch user details', err);
            }
        };

        fetchProductDetails();
        fetchReviews();
        fetchUsername();
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

    const handleAddReview = async () => {
        try {
            const newReview = {
                content: reviewContent,
                rating: reviewRating,
                productId: product.id, // Use productId directly
            };
            await axios.post('/api/reviews/add', newReview);
            setReviewContent('');
            setReviewRating(5);
            fetchReviews(); // Refresh the reviews list after submitting
        } catch (err) {
            console.error('Failed to add review', err);
            alert('Failed to add review. Please try again.');
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
                    <button onClick={handleAddToCart} style={styles.button}>
                        Add to Cart
                    </button>

                    {/* Reviews Section */}
                    <div>
                        <h3>Reviews</h3>
                        {reviews.length > 0 ? (
                            <ul>
                                {reviews.map((review, index) => (
                                    <li key={index}>
                                        <p>
                                            <strong>{review.username}:</strong> {/* Use review.username from ReviewResponseDTO */}
                                            {review.content} ({review.rating}/5) - {new Date(review.createdAt).toLocaleDateString()}
                                        </p>
                                    </li>
                                ))}
                            </ul>
                        ) : (
                            <p>No reviews yet. Be the first to review!</p>
                        )}
                        <div>
                            <textarea
                                value={reviewContent}
                                onChange={(e) => setReviewContent(e.target.value)}
                                placeholder="Write your review"
                                style={styles.textarea}
                            />
                            <select
                                value={reviewRating}
                                onChange={(e) => setReviewRating(parseInt(e.target.value))}
                                style={styles.select}
                            >
                                <option value="5">5 - Excellent</option>
                                <option value="4">4 - Good</option>
                                <option value="3">3 - Average</option>
                                <option value="2">2 - Poor</option>
                                <option value="1">1 - Terrible</option>
                            </select>
                            <button onClick={handleAddReview} style={styles.button}>
                                Submit Review
                            </button>
                        </div>
                    </div>
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
    select: {
        marginBottom: '10px',
        padding: '5px',
        width: '100%',
    },
    button: {
        padding: '10px 20px',
        fontSize: '16px',
        cursor: 'pointer',
        margin: '10px 0',
    },
    textarea: {
        display: 'block',
        width: '100%',
        marginBottom: '10px',
        padding: '10px',
        fontSize: '14px',
        borderRadius: '4px',
        border: '1px solid #ccc',
    },
};

export default ProductDetailsPage;
