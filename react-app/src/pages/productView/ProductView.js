import React, { useState, useEffect } from 'react';
import { useKeycloak } from '../../KeycloakContext';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import axios from 'axios';
import { ToastContainer, toast } from 'react-toastify';
import { useParams } from 'react-router-dom'; // To get product id from URL
import 'react-toastify/dist/ReactToastify.css';
import './ProductView.css';

function ProductViewPage() {
    const [product, setProduct] = useState(null);
    const { productId } = useParams(); // Get product ID from URL
    const { keycloak, authenticated } = useKeycloak();
    const [token, setToken] = useState('');
  
    useEffect(() => {
      if (authenticated && keycloak) {
        setToken(keycloak.token);
      }
    }, [authenticated, keycloak]);

    useEffect(() => {
        const fetchProduct = async () => {
            try {
                const token = keycloak.token;
                const response = await axios.get(`http://localhost:7000/api/v1/products/${productId}`, {
                    headers: {
                      'Authorization': `Bearer ${token}`,  // Include the token in the Authorization header
                      'Content-Type': 'application/json',
                    }
                  });
                setProduct(response.data);
            } catch (error) {
                console.error('Error fetching product:', error);
                toast.error('Failed to fetch product.');
            }
        };
        fetchProduct();
    }, [productId]);

    useEffect(() => {
        const token = keycloak.token;
        const eventSource = new EventSource(`http://localhost:7000/api/v1/products/updates?token=${token}`);

        eventSource.onmessage = (event) => {
            const updatedProduct = JSON.parse(event.data);
            if (String(updatedProduct.id) === productId) {
                setProduct(prevProduct => ({
                    ...prevProduct,
                    price: updatedProduct.price
                }));
                toast.info(`Price updated: $${updatedProduct.price}`);
            }
        };

        return () => {
            eventSource.close();
        };
    }, [productId]);

    if (!product) return <div>Loading...</div>;

    return (
        <div className="product-view-page">
            <h2>Product View</h2>
            <table className="product-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Price</th>
                    </tr>
                </thead>
                <tbody>
                    <tr key={product.id}>
                        <td>{product.name}</td>
                        <td>${product.price}</td>
                    </tr>
                </tbody>
            </table>
            <Routes>
            <Route path="/" element={
            <div>
                <ul className="linkList">
                <li><a href="/product-listview" className="link">Go to Listview</a></li>
                </ul>
            </div>
            } />
        </Routes>
            <ToastContainer position="top-center" />
        </div>
    );
}

export default ProductViewPage;
