## Table of Contents

- [Overview](#overview)
- [Design & Implementation](#implementation)
- [Getting Started](#getting-started)
- [API Endpoints](#api-endpoints)
- [Contributing](#contributing)
- [License](#license)


## Overview

The Product Management system consists of several microservices and a ReactJS UI. This setup enables users to register, view, update, and delete products, as well as navigate through the product list.

## Design & Implementation

Refer to "ProductService.pptx" in this repository for all the design and implementation information.

## Getting Started

**Clone the repository:**

    ```sh
    git clone git@github.com:robertjunyi2024/Product_Management.git
    cd Product_Management
    ```

**Run docker compose:**

    ```sh
    docker compose up
    ```

After all the containers are up, access the browser through the URL:

[http://localhost:8090/](http://localhost:8090/)

and log in as admin (password: `password`).

1. Click the **Create realm** button, and enter a real name: `ProductManagement`.

2. Click **"Clients"** on the left panel and click the **Create client** button. Enter the **Client ID**: `oci-client`.

3. Under **Access settings**, and **Valid redirect URIs**, enter:
   - `http://localhost:3000/*`
   - `http://localhost:3000`

4. In **Web origins**, enter:
   - `/*`
   - `http://localhost:3000`

5. Under **Users** on the left panel, click the **Create new user** button and enter a user's information. Set the user's password under the **Credentials** tab.
`

**Run first microservice:**

    ```sh
    cd Product_Management/product-microservice
    mvn spring-boot:run
    ```

**Run second microservice:**

    ```sh
    cd Product_Management/persistence-microservice
    mvn spring-boot:run

**Browser Access:**

http://localhost:3000/

