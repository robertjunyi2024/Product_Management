import React from 'react';
import { useKeycloak } from './KeycloakContext';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import ProductRegistration from './pages/productRegistration/ProductForm';
import ProductListView from './pages/productListView/ProductListView';
import ProductView from './pages/productView/ProductView';
import { ToastContainer, toast } from 'react-toastify';
import './App.css';
import 'react-toastify/dist/ReactToastify.css';
import logo from './assets/lowes.png';

function App() {
  const { keycloak, authenticated } = useKeycloak();

  if (!keycloak) {
    return <div>Loading...</div>;
  }

  if (!authenticated) {
    return <div>Unable to authenticate</div>;
  }

  return (
    <div className='page'>
      <ToastContainer position="top-center" />
      <Router>
        <div className="App">
          <header className="App-header">
          <img src={logo} alt="Logo" />
            <h3>
              Welcome {keycloak.tokenParsed.preferred_username}!
            </h3>
            <center>Product Management</center>
            <button className="logoutButton" onClick={() => keycloak.logout()}>Logout</button>
          </header>
          <main className="linksContainer">
            <section>
              <Routes>
                <Route path="/product-registration-form" element={<ProductRegistration />} />
                <Route path="/product-listview" element={<ProductListView />} />
                <Route path="/product/:productId" element={<ProductView />} />
                <Route path="/" element={
                  <div>
                    <ul className="linkList">
                      <li><a href="/product-registration-form" className="link">Go to Product Registration</a></li>
                      <li><a href="/product-listview" className="link">Go to Product Listview</a></li>
                    </ul>
                  </div>
                } />
              </Routes>
              </section>
          </main>

        </div>
      </Router>
    </div>
  );
}

export default App;
