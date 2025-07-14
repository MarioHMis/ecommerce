# Postman Testing Guide for Ecommerce Application

This guide helps you test the API endpoints of the Ecommerce application using Postman.

## 1. Register a New User

- **Endpoint:** `POST /auth/register`
- **URL:** `http://localhost:8080/auth/register`
- **Body (JSON):**
```json
{
  "username": "testuser",
  "password": "testpassword",
  "email": "testuser@example.com"
}
```
- **Description:** Registers a new user and returns an authentication token.

## 2. Login

- **Endpoint:** `POST /auth/login`
- **URL:** `http://localhost:8080/auth/login`
- **Body (JSON):**
```json
{
  "username": "testuser",
  "password": "testpassword"
}
```
- **Description:** Logs in the user and returns a JWT token.

- **Save the JWT token** from the response for use in subsequent requests.

## 3. Set Authorization Header in Postman

- In Postman, go to the **Headers** tab for your requests.
- Add a header:
  - **Key:** `Authorization`
  - **Value:** `Bearer YOUR_JWT_TOKEN_HERE`
- Replace `YOUR_JWT_TOKEN_HERE` with the token received from the login response.

## 4. Test Product Endpoints

### Create Product

- **Endpoint:** `POST /api/products`
- **URL:** `http://localhost:8080/api/products`
- **Body:** Use `form-data` in Postman
  - Key: `product` (type: text)
    - Value (JSON string):
    ```json
    {
      "name": "Sample Product",
      "description": "This is a sample product",
      "price": 19.99,
      "category": "Sample Category"
    }
    ```
  - Key: `image` (type: file) - optional, upload an image file

### Search Products

- **Endpoint:** `GET /api/products/search`
- **URL:** `http://localhost:8080/api/products/search?query=sample`
- **Description:** Searches products by query string.

### Get All Products (Paged)

- **Endpoint:** `GET /api/products/paged`
- **URL:** `http://localhost:8080/api/products/paged?page=0&size=10`
- **Description:** Gets paged list of products.

### Get All Products (List)

- **Endpoint:** `GET /api/products`
- **URL:** `http://localhost:8080/api/products`
- **Description:** Gets list of all products.

### Get Products by Seller

- **Endpoint:** `GET /api/products/mine`
- **URL:** `http://localhost:8080/api/products/mine`
- **Description:** Gets products created by the logged-in seller.

## 5. Test Role-Based Access Endpoints

- These endpoints require specific roles and authorization.

| Endpoint               | URL                          | Role Required          | Description                      |
|------------------------|------------------------------|-----------------------|---------------------------------|
| GET /api/test/admin    | http://localhost:8080/api/test/admin    | Admin                 | Access for admin only            |
| GET /api/test/seller   | http://localhost:8080/api/test/seller   | Seller                | Access for seller only           |
| GET /api/test/buyer    | http://localhost:8080/api/test/buyer    | Buyer                 | Access for buyer only            |
| GET /api/test/any      | http://localhost:8080/api/test/any      | Admin or Seller       | Access for admin or seller       |

- Use the JWT token of a user with the appropriate role to test these endpoints.

---

## Notes

- Replace `http://localhost:8080` with your actual server URL if different.
- Ensure your server is running before testing.
- Use the token from login to authorize requests that require authentication.

---

This guide should help you test all main endpoints and functionality of your app using Postman.