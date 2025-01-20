import { render, screen, fireEvent, waitFor } from '@testing-library/react';
import CartPage from './CartPage';
import { BrowserRouter } from 'react-router-dom';
import * as AuthContextModule from '../context/AuthContext';
import axios from 'axios';

jest.mock('axios'); // Mock axios

describe('CartPage Component', () => {
    const mockAuthContext = {
        token: 'fake-token',
    };

    beforeEach(() => {
        jest.clearAllMocks(); // Clear mocks before each test
        jest.spyOn(AuthContextModule, 'useAuth').mockReturnValue(mockAuthContext); // Mock useAuth
        jest.spyOn(window, 'alert').mockImplementation(() => {}); // Mock alert
    });

    it('renders an empty cart message', async () => {
        axios.get.mockResolvedValueOnce({ data: { items: [] } });

        render(
            <BrowserRouter>
                <CartPage />
            </BrowserRouter>
        );

        expect(await screen.findByText(/your cart is empty/i)).toBeInTheDocument();
    });

    it('displays error message if cart fetch fails', async () => {
        axios.get.mockRejectedValueOnce(new Error('Failed to load cart items.'));

        render(
            <BrowserRouter>
                <CartPage />
            </BrowserRouter>
        );

        expect(await screen.findByText(/failed to load cart items/i)).toBeInTheDocument();
    });

    it('removes an item from the cart when remove button is clicked', async () => {
        axios.get.mockResolvedValueOnce({
            data: { items: [{ product: { id: 1, name: 'Test Product' }, quantity: 1 }] },
        });
        axios.delete.mockResolvedValueOnce({});

        render(
            <BrowserRouter>
                <CartPage />
            </BrowserRouter>
        );

        expect(await screen.findByText(/Test Product/i)).toBeInTheDocument();

        fireEvent.click(screen.getByText(/Remove/i));

        await waitFor(() => {
            expect(screen.getByText(/your cart is empty/i)).toBeInTheDocument();
        });
    });

    it('handles checkout successfully', async () => {
        axios.get.mockResolvedValueOnce({
            data: { items: [{ product: { id: 1, name: 'Test Product' }, quantity: 1 }] },
        });
        axios.post.mockResolvedValueOnce({});

        render(
            <BrowserRouter>
                <CartPage />
            </BrowserRouter>
        );

        expect(await screen.findByText(/Test Product/i)).toBeInTheDocument();

        fireEvent.click(screen.getByText(/Checkout/i));

        await waitFor(() => {
            expect(screen.getByText(/your cart is empty/i)).toBeInTheDocument();
        });
    });
});
