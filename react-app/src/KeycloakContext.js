import React, { createContext, useContext, useState, useEffect } from 'react';
import Keycloak from 'keycloak-js';

const KeycloakContext = createContext(null);

export const KeycloakProvider = ({ children }) => {
  const [keycloak, setKeycloak] = useState(null);
  const [authenticated, setAuthenticated] = useState(false);

  useEffect(() => {
    const keycloakInstance = new Keycloak({
      url: 'http://localhost:8090',
      realm: 'ProductManagement',
      clientId: 'oci-client'
    });

    keycloakInstance.init({ onLoad: 'login-required', checkLoginIframe: false }).then(authenticated => {
      setKeycloak(keycloakInstance);
      setAuthenticated(authenticated);
    }).catch(error => {
      console.error('Keycloak initialization error:', error);
    });
  }, []);

  return (
    <KeycloakContext.Provider value={{ keycloak, authenticated }}>
      {children}
    </KeycloakContext.Provider>
  );
};

export const useKeycloak = () => {
  return useContext(KeycloakContext);
};
