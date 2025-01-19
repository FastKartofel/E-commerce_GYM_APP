# Base image
FROM node:18-alpine AS build
WORKDIR /app

# Copy project files
COPY . .

# Set environment variable for OpenSSL compatibility
ENV NODE_OPTIONS=--openssl-legacy-provider

# Install dependencies and build the project
RUN npm install && npm run build

# Use NGINX to serve the React build
FROM nginx:stable-alpine
COPY --from=build /app/build /usr/share/nginx/html
EXPOSE 80
CMD ["nginx", "-g", "daemon off;"]
