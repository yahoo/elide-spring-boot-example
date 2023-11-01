/*
 * Source code adapted from:
 * https://github.com/swagger-api/swagger-ui/blob/v5.1.3/dist/swagger-initializer.js
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
