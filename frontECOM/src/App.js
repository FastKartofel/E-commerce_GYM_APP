// src/App.js
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { AuthProvider } from './context/AuthContext';
import LoginPage from './components/LoginPage';
import RegisterPage from './components/RegisterPage';
import MainPage from './components/MainPage';
import PrivateRoute from './components/PrivateRoute';
import Navbar from './components/Navbar';
import ProductDetailsPage from './components/ProductDetailsPage';
import ProductsPage from './components/ProductsPage';
import ProfilePage from './components/ProfilePage';
import CartPage from './components/CartPage'; // Assuming you have this component
import NotFoundPage from './components/NotFoundPage'; // For 404

function App() {
    return (
        <AuthProvider>
            <Router>
                <Navbar />
                <Routes>
                    <Route path="/login" element={<LoginPage />} />
                    <Route path="/register" element={<RegisterPage />} />
                    <Route
                        path="/main"
                        element={
                            <PrivateRoute>
                                <MainPage />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/products"
                        element={
                            <PrivateRoute>
                                <ProductsPage />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/products/:id"
                        element={
                            <PrivateRoute>
                                <ProductDetailsPage />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/profile"
                        element={
                            <PrivateRoute>
                                <ProfilePage />
                            </PrivateRoute>
                        }
                    />
                    <Route
                        path="/cart"
                        element={
                            <PrivateRoute>
                                <CartPage />
                            </PrivateRoute>
                        }
                    />
                    <Route path="*" element={<NotFoundPage />} />
                </Routes>
            </Router>
        </AuthProvider>
    );
}

export default App;
