import { render, screen } from '@testing-library/react';
import Navbar from './Navbar';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';

describe('Navbar', () => {

    it('renders login link for unauthenticated users', () => {
        const mockAuthContext = { token: null }; // Simulate unauthenticated user

        render(
            <AuthProvider value={mockAuthContext}>
                <BrowserRouter>
                    <Navbar />
                </BrowserRouter>
            </AuthProvider>
        );

        // Use getByRole for better matching
        expect(screen.getByRole('link', { name: /login/i })).toBeInTheDocument();
        expect(screen.queryByRole('link', { name: /logout/i })).toBeNull();
    });
});
