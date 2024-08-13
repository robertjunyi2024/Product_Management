import Keycloak from 'keycloak-js';

const keycloak = new Keycloak({
  url: 'http://localhost:8090/realms/ProductManagement/protocol/openid-connect/auth',
  realm: 'ProductManagement',
  clientId: 'ProductManagement-realm',
});

export default keycloak;

