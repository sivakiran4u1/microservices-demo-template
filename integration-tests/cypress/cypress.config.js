const { defineConfig } = require('cypress');
const { registerSealightsTasks } = require('sealights-cypress-plugin');
module.exports = defineConfig({
  e2e: {
    experimentalInteractiveRunEvents: true,
    experimentalSessionAndOrigin: true ,
    testIsolation: "off" ,
    async setupNodeEvents(on, config) {
      await registerSealightsTasks(on, config);
    },
  },

});
