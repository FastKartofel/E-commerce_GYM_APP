import React, { useState } from 'react';
import axios from 'axios';
import './ProductChatbot.css'; // Separate CSS file for styling

const ProductChatbot = () => {
    const [userInput, setUserInput] = useState('');
    const [recommendation, setRecommendation] = useState('');
    const [loading, setLoading] = useState(false);

    const handleSubmit = async (e) => {
        e.preventDefault();
        setLoading(true);
        try {
            const response = await axios.post(
                '/api/recommendations',
                { userPrompt: userInput },
                { headers: { 'Content-Type': 'application/json' } }
            );
            setRecommendation(response.data.recommendation);
            setUserInput('');
        } catch (err) {
            console.error(err);
            alert('Error fetching recommendations');
        } finally {
            setLoading(false);
        }
    };

    return (
        <div className="chatbot-container">
            <h2 className="chatbot-title">Product Recommendation Chatbot</h2>
            <form onSubmit={handleSubmit} className="chatbot-form">
                <textarea
                    value={userInput}
                    onChange={(e) => setUserInput(e.target.value)}
                    placeholder="Type your query here..."
                    required
                    className="chatbot-input"
                />
                <button type="submit" disabled={loading} className="chatbot-button">
                    {loading ? 'Fetching...' : 'Get Recommendations'}
                </button>
            </form>
            {recommendation && (
                <div className="chatbot-output">
                    <h3>Recommended Products:</h3>
                    <p>{recommendation}</p>
                </div>
            )}
        </div>
    );
};

export default ProductChatbot;
