import React, { useState, useEffect } from 'react';
import { useKeycloak } from '../../KeycloakContext';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import axios from 'axios';
import './ProductForm.css';
import keycloak from '../../keycloak';

function ProductForm() {
  const [product, setProduct] = useState({ name: '', price: '' });
  const [message, setMessage] = useState({ text: '', type: '' });
  const { keycloak, authenticated } = useKeycloak();
  const [token, setToken] = useState('');

  useEffect(() => {
    if (authenticated && keycloak) {
      setToken(keycloak.token);
    }
  }, [authenticated, keycloak]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setProduct({ ...product, [name]: value });
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (!keycloak || !keycloak.token) {
        throw new Error("Keycloak token is not available");
      }
      const token = keycloak.token;
      await axios.post('http://localhost:7000/api/v1/products', product, {
        headers: {
          'Authorization': `Bearer ${token}`,  // Include the token in the Authorization header
          'Content-Type': 'application/json',
        }
      });
      setMessage({ text: 'Product registered successfully!', type: 'success' });
    } catch (error) {
      setMessage({ text: error.response.data, type: 'error' });
    }
  };

  return (
    <div className="product-form">
      <h2>Product Registration Form</h2>
      <form onSubmit={handleSubmit}>
        <div>
          <label>Name:</label>
          <input type="text" name="name" value={product.name} onChange={handleChange} required />
        </div>
        <div>
          <label>Price:</label>
          <input type="number" name="price" value={product.price} onChange={handleChange} required />
        </div>
        <button className="save-product-button" type="submit">Save Product</button>
      </form>
      {message.text && (
        <div className={`message ${message.type}`}>
          {message.text}
        </div>
      )}
      <Routes>
        <Route path="/" element={
          <div>
            <ul className="linkList">
              <li><a href="/" className="link">Go to Main Page</a></li>
            </ul>
          </div>
        } />
      </Routes>
    </div>
    
  );
}

export default ProductForm;
