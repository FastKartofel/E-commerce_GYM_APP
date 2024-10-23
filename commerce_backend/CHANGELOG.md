Implemented role-based access control for product management and order viewing.

- Admin users can now manage products (add, edit, delete) via the admin panel.
- Admin users can view all customer orders.
- Regular users can only view and manage their own orders, with no access to product management.
- Added role assignment functionality during user registration, ensuring users are assigned either 'USER' or 'ADMIN' roles.
- Enforced role-based permissions at the controller level for secure access.
- Added logging to capture unauthorized access attempts for better security auditing.