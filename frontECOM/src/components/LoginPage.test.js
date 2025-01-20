import { render, screen, fireEvent } from '@testing-library/react';
import LoginPage from './LoginPage';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';

jest.mock('axios');

describe('LoginPage', () => {
    it('renders login form', () => {
        render(
            <AuthProvider>
                <BrowserRouter>
                    <LoginPage />
                </BrowserRouter>
            </AuthProvider>
        );

        expect(screen.getByPlaceholderText(/username/i)).toBeInTheDocument();
        expect(screen.getByPlaceholderText(/password/i)).toBeInTheDocument();
        expect(screen.getByRole('button', { name: /login/i })).toBeInTheDocument();
    });


    it('handles login error', async () => {
        axios.post.mockRejectedValueOnce(new Error('Invalid credentials'));

        render(
            <AuthProvider>
                <BrowserRouter>
                    <LoginPage />
                </BrowserRouter>
            </AuthProvider>
        );

        fireEvent.change(screen.getByPlaceholderText(/username/i), { target: { value: 'wronguser' } });
        fireEvent.change(screen.getByPlaceholderText(/password/i), { target: { value: 'wrongpassword' } });
        fireEvent.click(screen.getByRole('button', { name: /login/i }));

        // Check for error message
        expect(await screen.findByText(/login failed/i)).toBeInTheDocument();
    });
});
