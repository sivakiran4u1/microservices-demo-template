const { defineConfig } = require('cypress');
const { registerSealightsTasks } = require('sealights-cypress-plugin');
module.exports = defineConfig({
  e2e: {
    experimentalInteractiveRunEvents: true,
    testIsolation: false,
    setupNodeEvents(on, config) {
      registerSealightsTasks(on, config);
    },
  },
});
