import { render } from '@testing-library/react';
import ErrorBoundary from './ErrorBoundary';

const ThrowError = () => {
    throw new Error('Test Error');
};

describe('ErrorBoundary', () => {
    it('catches error and displays fallback UI', () => {
        const { getByText } = render(
            <ErrorBoundary>
                <ThrowError />
            </ErrorBoundary>
        );

        expect(getByText(/something went wrong/i)).toBeInTheDocument();
    });
});
