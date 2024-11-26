// src/components/MainPage.js
import React, { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import axios from 'axios';
import './MainPage.css';

// Importing background images
import creatineBg from '../assets/backgrounds/creatine-bg.jpg';
import wheyBg from '../assets/backgrounds/whey-bg.jpg';
import preworkoutBg from '../assets/backgrounds/pre-workout-bg.jpg';

const MainPage = () => {
    const [searchParams] = useSearchParams();
    const categoryId = searchParams.get('categoryId'); // Using categoryId instead of category name

    const [products, setProducts] = useState([]);
    const [currentIndex, setCurrentIndex] = useState(0);
    const [error, setError] = useState(null);
    const [loading, setLoading] = useState(false);

    // Mapping categoryId to background classes
    const categoryBackgroundMap = {
        '1': 'bg-whey',          // Whey Protein
        '2': 'bg-creatine',      // Creatine
        '3': 'bg-pre-workout',   // Pre-Workout
        // Add more mappings as needed
    };

    // Function to get background class based on categoryId
    const getBackgroundClass = (id) => {
        return categoryBackgroundMap[id] || '';
    };

    // Helper function to get category name based on ID
    const getCategoryName = (id) => {
        const categoryMap = {
            '1': 'Whey Protein',
            '2': 'Creatine',
            '3': 'Pre-Workout',
            // Add more mappings as needed
        };
        return categoryMap[id] || 'Category';
    };

    // Fetch products when categoryId changes
    useEffect(() => {
        if (categoryId) {
            const fetchProducts = async () => {
                setLoading(true);
                try {
                    console.log(`Fetching products for categoryId: ${categoryId}`);
                    const response = await axios.get(`/api/products?categoryId=${encodeURIComponent(categoryId)}`);
                    console.log('Fetched products:', response.data);
                    setProducts(response.data);
                    setCurrentIndex(0);
                } catch (err) {
                    if (err.response && err.response.status === 403) {
                        setError('You do not have permission to view these products.');
                    } else {
                        setError('Failed to load products.');
                    }
                    console.error(err);
                } finally {
                    setLoading(false);
                }
            };
            fetchProducts();
        }
    }, [categoryId]);

    // Navigation handlers
    const handleNext = () => {
        setCurrentIndex((prevIndex) => (prevIndex + 1) % products.length);
    };

    const handlePrev = () => {
        setCurrentIndex((prevIndex) => (prevIndex - 1 + products.length) % products.length);
    };

    return (
        <div className={`main-container ${getBackgroundClass(categoryId)}`}>
            {!categoryId ? (
                <>
                    <h2>Welcome to GymZone!</h2>
                    <p>Select a category to explore our products:</p>
                    <div className="categories">
                        <Link to="/main?categoryId=1" className="category-card">
                            Whey Protein
                        </Link>
                        <Link to="/main?categoryId=2" className="category-card">
                            Creatine
                        </Link>
                        <Link to="/main?categoryId=3" className="category-card">
                            Pre-Workout
                        </Link>
                        {/* Add more categories as needed */}
                    </div>
                </>
            ) : (
                <>
                    <h2>{getCategoryName(categoryId)} Products</h2>
                    {loading ? (
                        <div className="spinner"></div>
                    ) : error ? (
                        <p style={{ color: 'red' }}>{error}</p>
                    ) : products.length === 0 ? (
                        <p>No products found in this category.</p>
                    ) : (
                        <div className="product-viewer">
                            <button
                                onClick={handlePrev}
                                className="nav-button"
                                disabled={products.length === 1}
                                aria-label="Previous Product"
                            >
                                &#8592; Previous
                            </button>
                            <div className="product-card">
                                <h3>{products[currentIndex].name}</h3>
                                <p>{products[currentIndex].description}</p>
                                <p>Price: ${products[currentIndex].price.toFixed(2)}</p>
                                <p>Stock: {products[currentIndex].stockQuantity}</p>
                                <Link to={`/products/${products[currentIndex].id}`} className="details-button">
                                    View Details
                                </Link>
                            </div>
                            <button
                                onClick={handleNext}
                                className="nav-button"
                                disabled={products.length === 1}
                                aria-label="Next Product"
                            >
                                Next &#8594;
                            </button>
                        </div>
                    )}
                </>
            )}
        </div>
    );
};

export default MainPage;
