import { renderHook, act } from '@testing-library/react';
import { AuthProvider, useAuth } from '../context/AuthContext';

describe('AuthContext', () => {
    it('should provide default token as null', () => {
        const { result } = renderHook(() => useAuth(), { wrapper: AuthProvider });
        expect(result.current.token).toBe(null);
    });

    it('should update the token on login', () => {
        const { result } = renderHook(() => useAuth(), { wrapper: AuthProvider });

        act(() => {
            result.current.login('test-token');
        });

        expect(result.current.token).toBe('test-token');
    });

    it('should clear the token on logout', () => {
        const { result } = renderHook(() => useAuth(), { wrapper: AuthProvider });

        act(() => {
            result.current.login('test-token');
            result.current.logout();
        });

        expect(result.current.token).toBe(null);
    });
});
