# Sureshop Backend API

## Overview

Sureshop is a fully functional e-commerce backend API built with Java, Spring Boot, and PostgreSQL (or MySQL - depending on your choice). It provides a robust set of features to manage products, users, shopping carts, orders, and payments.  This README provides information on how to set up, run, and test the API.

## Technologies Used

*   **Java:**  Java 21
*   **Spring Boot:**  3.5.4
*   **Spring Data JPA**
*   **Spring Security:**  For authentication and authorization.
*   **JWT (JSON Web Tokens):**  For secure user authentication.
*   **MySQL:** For storinf data
*   **Swagger/OpenAPI:**  For API documentation.
*   **JUnit & Mockito:**  For testing.
*   **Stripe API (Simulation):**  For payment processing (simulated).
*   **OpenPDF:** For generating invoices.
*   **Lombok:** For reducing boilerplate code.
*   **Maven:**  For dependency management and build automation.

## Features

*   **User Management:**
    *   User registration (Customer & Admin)
    *   Secure login with JWT token generation
    *   Role-based access control (ROLE\_USER, ROLE\_ADMIN)
    *   Logout functionality
*   **Product Management:**
    *   CRUD operations for products (Admin only)
    *   Product search and filtering
    *   Product ratings and reviews
*   **Shopping Cart:**
    *   Add/Update/Remove items
    *   Cart persistence in the database
*   **Order Management:**
    *   Order creation and status updates (PENDING, CONFIRMED, SHIPPED, DELIVERED)
    *   Order cancellation (PENDING status)
    *   Order history
    *   Admin view of all orders
*   **Payment Integration (Stripe Simulation):**
    *   Simulated Stripe session creation
    *   Order confirmation and status update
*   **Invoice Generation:**
    *   PDF invoice generation
*   **API Documentation:**
    *   Swagger UI for all endpoints

## Prerequisites

*   **Java Development Kit (JDK):** Java 21
*   **Maven:** 3.8.6
*  **MySQL:** sureshop

## Setup and Installation

1.  **Clone the repository:**
    ```bash
    git clone [Your Repository URL]
    cd sure_shop_backend
    ```

2.  **Configure the database:**
    *   Open `src/main/resources/application.properties`.
    *   Modify the following properties to match your database configuration:
        ```properties
        spring.datasource.url=jdbc:postgresql://localhost:5432/sureshop  # or jdbc:mysql://... for MySQL
        spring.datasource.username=your_db_username
        spring.datasource.password=your_db_password
        # For MySQL
        spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
        spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect #For MySQL
        spring.jpa.hibernate.ddl-auto=update # or create, create-drop
        ```
        * Replace `your_db_username` and `your_db_password` with your database credentials.
        * If using MySQL, adjust the `spring.datasource.url` and `spring.datasource.driver-class-name` properties accordingly.

3.  **Build the project:**
    ```bash
    ./mvnw clean install
    ```

## Running the Application

1.  **Run the application:**
    ```bash
    ./mvnw spring-boot:run
    ```
    The application will start on port 8081 (or the port specified in `application.properties`).

## API Documentation

*   Access the API documentation using Swagger UI at:  `http://localhost:8081/swagger-ui/index.html`

## Testing

*   **Unit Tests:**  Run unit tests using:
    ```bash
    ./mvnw test
    ```
*   **Integration Tests:**  Run integration tests using:
    ```bash
    # If you have separate integration test profiles, run them appropriately
    # Example:
    ./mvnw integration-test  # If integration tests are configured
    ```

*   **Manual Testing (Postman):**
    *   Import the provided Postman collection (if available) to quickly test the API endpoints.
    *   Or, create your own requests in Postman based on the API documentation.

## API Endpoints

*   **Authentication:**
    *   `POST /api/auth/register`
    *   `POST /api/auth/login`
    *   `POST /api/auth/logout`
*   **Products:**
    *   `GET /api/products`
    *   `POST /api/products` (Admin)
    *   `PUT /api/products/{id}` (Admin)
    *   `DELETE /api/products/{id}` (Admin)
    *   `POST /api/products/{id}/reviews` (User)
*   **Cart:**
    *   `POST /api/cart/add`
    *   `PUT /api/cart/update`
    *   `DELETE /api/cart/remove`
    *   `GET /api/cart/view`
*   **Orders:**
    *   `POST /api/orders/checkout`
    *   `GET /api/orders/my`
    *   `PUT /api/orders/cancel/{id}`
    *   `GET /api/admin/orders` (Admin)
*   **Payments:**
    *   `POST /api/payments/create-session`
    *   `POST /api/payments/confirm`
    *   **Admin Analytics**
    *   `GET /api/admin/analytics/sales`
    *   `GET /api/admin/analytics/products`
    *   `GET /api/admin/analytics/users`

## Security

*   The API uses JWT (JSON Web Tokens) for authentication.
*   Admin endpoints are protected and only accessible to users with the `ROLE_ADMIN` role.
*   Use the "Authorize" button in Swagger UI to provide your JWT token for testing authenticated endpoints.

## Deployment

*   **Docker:**  A `Dockerfile` is provided to containerize the application.
    *   Build the Docker image:  `docker build -t sureshop-backend .`
    *   Run the Docker container: `docker run -p 8081:8081 sureshop-backend`
*   **Deployment Platforms:**
    *   Deploy to Railway or Render (or your preferred cloud platform).

## Contributing

[Instructions on how to contribute to the project, if applicable.]  (e.g., Fork the repository, create a branch, make your changes, and submit a pull request.)

## License

[Your License - e.g., MIT, Apache 2.0]

## Contact

[Your contact information, e.g., your email address, your GitHub profile]
