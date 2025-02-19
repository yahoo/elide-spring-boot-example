/*
 * Source code adapted from:
 * https://github.com/swagger-api/swagger-ui/blob/v5.17.14/dist/swagger-initializer.js
 */

window.onload = function() {
  //<editor-fold desc="Changeable Configuration Block">

  let search;
  if(parent.window) {
    search = parent.window.location.search;
  }
  if(!search) {
    search = window.location.search;
  }
  const searchParams = new URLSearchParams(search);
  let path = searchParams.get('path');
  if(!path) {
    path = '';
  } else {
    searchParams.delete('path');
    search = searchParams.toString();
    if(search) {
      search = `?${search}`
    }
  }
  const contextPath = window.location.pathname.substring(0, window.location.pathname.indexOf('/swagger'));
  // the following lines will be replaced by docker/configurator, when it runs in a docker-container
  window.ui = SwaggerUIBundle({
    url: `${contextPath}/doc${path}${search}`,
    dom_id: '#swagger-ui',
    deepLinking: true,
    // start csrf modifications    
    requestInterceptor: (request) => {
      const documentURL = new URL(document.URL);
      const requestURL = new URL(request.url, document.location.origin);
      const sameOrigin = (documentURL.protocol === requestURL.protocol && documentURL.host === requestURL.host);
      if (sameOrigin) {
        const value = `; ${document.cookie}`;
        const parts = value.split(`; XSRF-TOKEN=`);
        if (parts.length === 2) {
          const token = parts.pop().split(';').shift();
          if (token) {
            request.headers['X-XSRF-TOKEN'] = token;
          }
        }
      }
      return request;
    },
    // end csrf modifications
    presets: [
      SwaggerUIBundle.presets.apis,
      SwaggerUIStandalonePreset
    ],
    plugins: [
      SwaggerUIBundle.plugins.DownloadUrl
    ],
    layout: "StandaloneLayout"
  });

  //</editor-fold>
};
