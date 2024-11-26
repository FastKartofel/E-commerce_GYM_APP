import React from 'react';

const NotFoundPage = () => {
    return (
        <div style={styles.container}>
            <h2>404 - Page Not Found</h2>
            <p>The page you are looking for does not exist.</p>
        </div>
    );
};

const styles = {
    container: {
        textAlign: 'center',
        padding: '50px',
        color: '#333',
    },
};

export default NotFoundPage;
