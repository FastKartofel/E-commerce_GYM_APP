import { render, screen, waitFor } from '@testing-library/react';
import CurrentOrdersPage from './CurrentOrdersPage';
import axios from 'axios';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from '../context/AuthContext';

jest.mock('axios');

describe('CurrentOrdersPage', () => {

    it('displays error message on fetch failure', async () => {
        axios.get.mockRejectedValueOnce(new Error('Failed to fetch orders'));

        render(
            <AuthProvider>
                <BrowserRouter future={{ v7_startTransition: true, v7_relativeSplatPath: true }}>
                    <CurrentOrdersPage />
                </BrowserRouter>
            </AuthProvider>
        );

        await screen.findByText(/failed to fetch current orders/i); // Exact match for error text
    });
});
