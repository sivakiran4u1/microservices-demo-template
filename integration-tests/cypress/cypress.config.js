const { defineConfig } = require('cypress');
const { registerSealightsTasks } = require('sealights-cypress-plugin');

module.exports = defineConfig({
  e2e: {
    video: false,
    experimentalInteractiveRunEvents: true,
    testIsolation: false,
    async setupNodeEvents(on, config) {
      await registerSealightsTasks(on, config);
     },
  },
});