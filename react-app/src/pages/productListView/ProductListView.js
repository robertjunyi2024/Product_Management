import React, { useState, useEffect } from 'react';
import { useKeycloak } from '../../KeycloakContext';
import { BrowserRouter as Router, Routes, Route, useNavigate } from 'react-router-dom';
import { ToastContainer, toast } from 'react-toastify';
import axios from 'axios';
import './ProductListView.css';
import 'react-toastify/dist/ReactToastify.css';

function ProductListviewPage() {
    const [products, setProducts] = useState([]);
    const [page, setPage] = useState(0);
    const [totalPages, setTotalPages] = useState(0);
    const [selectedProduct, setSelectedProduct] = useState(null);
    const [showPopup, setShowPopup] = useState(false);
    const [actions, setActions] = useState({});
    const [newPrice, setNewPrice] = useState('');
    const navigate = useNavigate(); // Initialize useNavigate hook
    const { keycloak, authenticated } = useKeycloak();
    const [token, setToken] = useState('');
  
    useEffect(() => {
      if (authenticated && keycloak) {
        setToken(keycloak.token);
      }
    }, [authenticated, keycloak]);

    useEffect(() => {
        const fetchProducts = async () => {
            try {
                const token = keycloak.token;
                const response = await axios.get(`http://localhost:7000/api/v1/products?page=${page}&size=10`, {
                    headers: {
                      'Authorization': `Bearer ${token}`,  // Include the token in the Authorization header
                      'Content-Type': 'application/json',
                    }
                  });
                setProducts(response.data.content);
                setTotalPages(response.data.totalPages);
            } catch (error) {
                console.error('Error fetching products:', error);
                toast.error('Failed to fetch products.');
            }
        };
        fetchProducts();
    }, [page]);

    const handleDropdownAction = (productId, action) => {
        if (action === '') {
            return;
        }
        if (action === 'viewProduct') {
            navigate(`/product/${productId}`); // Navigate to ProductView page
        } else {
            setActions(prevActions => ({ ...prevActions, [productId]: action }));
            setSelectedProduct(productId);
            setShowPopup(true);
        }
    };

    const handleDeleteProduct = async () => {
        if (selectedProduct) {
            try {
                const token = keycloak.token;
                await axios.delete(`http://localhost:7000/api/v1/products/${selectedProduct}`, {
                    headers: {
                      'Authorization': `Bearer ${token}`,  // Include the token in the Authorization header
                      'Content-Type': 'application/json',
                    }
                  });
                setProducts(prevProducts => prevProducts.filter(product => product.id !== selectedProduct));
                setSelectedProduct(null);
                toast.success('Product deleted successfully.');
            } catch (error) {
                console.error('Error deleting product:', error);
                toast.error('Failed to delete product.');
            }
        }
    };

    const handleUpdatePrice = async () => {
        if (selectedProduct && newPrice) {
            try {
                const token = keycloak.token;
                await axios.put(`http://localhost:7000/api/v1/products/${selectedProduct}/update-price`, { price: newPrice },
                {
                    headers: {
                        'Authorization': `Bearer ${token}`,  // Include the token in the Authorization header
                        'Content-Type': 'application/json',
                    }
                });
                setProducts(prevProducts => 
                    prevProducts.map(product => 
                        product.id === selectedProduct ? { ...product, price: newPrice } : product
                    )
                );
                setSelectedProduct(null);
                setNewPrice('');
                toast.success('Product price updated successfully.');
            } catch (error) {
                console.error('Error updating product price:', error);
                toast.error('Failed to update product price.');
            }
        }
    };

    const handlePopupAction = () => {
        const action = actions[selectedProduct];
        if (action === 'deleteProduct') {
            handleDeleteProduct();
        } else if (action === 'updatePrice') {
            handleUpdatePrice();
        }
        setShowPopup(false);
        setActions(prevActions => ({ ...prevActions, [selectedProduct]: '' }));
    };

    const handleClosePopup = () => {
        setShowPopup(false);
        setActions(prevActions => ({ ...prevActions, [selectedProduct]: '' }));
    };

    return (
        <div className="product-page">
            <h2>Product List</h2>
            <table className="product-table">
                <thead>
                    <tr>
                        <th>Name</th>
                        <th>Price</th>
                        <th>Action</th>
                    </tr>
                </thead>
                <tbody>
                    {products.map(product => (
                        <tr key={product.id}>
                            <td>{product.name}</td>
                            <td>${product.price}</td>
                            <td>
                                <select
                                    onChange={(e) => handleDropdownAction(product.id, e.target.value)}
                                    value={actions[product.id] || ''} 
                                >
                                    <option value="">Select Action</option>
                                    <option value="updatePrice">Update Price</option>
                                    <option value="deleteProduct">Delete Product</option>
                                    <option value="viewProduct">View Product</option>
                                </select>
                            </td>
                        </tr>
                    ))}
                </tbody>
            </table>
            <div className="pagination">
                <button onClick={() => setPage(page - 1)} disabled={page === 0}>Previous</button>
                <span>Page {page + 1} of {totalPages}</span>
                <button onClick={() => setPage(page + 1)} disabled={page >= totalPages - 1}>Next</button>
            </div>
            {showPopup && (
                <div className="popup">
                    <div className="popup-content">
                        <h3>{actions[selectedProduct] === 'deleteProduct' ? 'Confirm Deletion' : 'Update Price'}</h3>
                        {actions[selectedProduct] === 'deleteProduct' && (
                            <div>
                                <p>Are you sure you want to delete this product?</p>
                                <button onClick={handlePopupAction}>Yes</button>
                                <button onClick={handleClosePopup}>No</button>
                            </div>
                        )}
                        {actions[selectedProduct] === 'updatePrice' && (
                            <div>
                                <input 
                                    type="number" 
                                    placeholder="Enter new price" 
                                    value={newPrice}
                                    onChange={(e) => setNewPrice(e.target.value)}
                                />
                                <button onClick={handlePopupAction}>OK</button>
                                <button onClick={handleClosePopup}>Cancel</button>
                            </div>
                        )}
                    </div>
                </div>
            )}
            <ToastContainer position="top-center" />
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

export default ProductListviewPage;
