## Table of Contents

- [Overview](#overview)
- [Implementation](#implementation)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)


## Overview

The Product Management system consists of several microservices and a ReactJS UI. This setup enables users to register, view, update, and delete products, as well as navigate through the product list.

## Implementation

Refer to "ProductService.pptx" in this repository for all the design and implementation information.

## Getting Started

1. **Clone the repository:**

    ```sh
    git clone git@github.com:robertjunyi2024/Product_Management.git
    cd Product_Management
    ```

2. **Run docker compose:**

    ```sh
    docker compose up
    ```

After all the containers are up, access the browser through the URL:

[http://localhost:8090/](http://localhost:8090/)

and log in as admin (password: `password`).

 Click the **Create realm** button, and enter a real name: `ProductManagement`.

 Click **"Clients"** on the left panel and click the **Create client** button. Enter the **Client ID**: `oci-client`.

 Under **Access settings**, and **Valid redirect URIs**, enter:
   - `http://localhost:3000/*`
   - `http://localhost:3000`

 In **Web origins**, enter:
   - `/*`
   - `http://localhost:3000`

 Under **Users** on the left panel, click the **Create new user** button and enter a user's information. Set the user's password under the **Credentials** tab.


3. **Run first microservice:**

    ```sh
    cd Product_Management/product-microservice
    mvn spring-boot:run
    ```

4. **Run second microservice:**

    ```sh
    cd Product_Management/persistence-microservice
    mvn spring-boot:run
    ```

5. **Run ReactJS client:**

    ```sh
    cd Product_Management/react-app
    npm install
    npm start
    ```

6. **Browser Access:**

http://localhost:3000/

## API Endpoints
1. POST /api/v1/products - Register Product
2. GET /api/v1/products - Get list of products
3. PUT /api/v1/products/{id}/update-price - Update Product Price
4. DELETE /api/v1/products/{id} - Delete Product
5. GET /api/v1/products/{id} - GET product details
6. GET /api/v1/products/updates - GET product price update
7. GET /api/v1/products - Get list of products
8. GET /api/v1/products/{id} - Get Product Details
9. GET /api/v1/products/exists-by-name? name=<Product_Name> - Check if Product Exists by Name

## Contributing

Contributions are welcome! Please open an issue or submit a pull request for any changes.

1. **Fork the repository**
2. **Create a new branch**
    
    ```sh
    git checkout -b feature/new-feature
    ```

3. **Make your changes**
4. **Commit your changes**
    
    ```sh
    git commit -m "Add new feature"
    ```

5. **Push to the branch**

    ```sh
    git push origin feature/new-feature
    ``` 
    
6. **Open a pull request**

## License
    
None

