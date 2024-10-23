Implemented role-based access control for product management and order viewing. [23.10.2024]

- Admin users can now manage products (add, edit, delete) via the admin panel.
- Admin users can view all customer orders.
- Regular users can only view and manage their own orders, with no access to product management.
- Added role assignment functionality during user registration, ensuring users are assigned either 'USER' or 'ADMIN' roles.
- Enforced role-based permissions at the controller level for secure access.
- Added logging to capture unauthorized access attempts for better security auditing.

Allow Users to Update Profile Details [23.10.2024]
- Implemented the ability for users to update their profile information, including email, shipping address, and payment details.
- Ensured profile updates persist in the database across sessions.
- Secured the update process by requiring authentication for user profile updates.
- Added validation and handling for authenticated requests during profile updates.