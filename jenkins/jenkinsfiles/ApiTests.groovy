
pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/ApiTests_shell_pod.yaml')
      defaultContainer "shell"
    }
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone')
    string(name: 'APP_NAME', defaultValue: 'ahmad-BTQ', description: 'app name')

  }

  options {
    buildDiscarder logRotator(numToKeepStr: '30')
    timestamps()
  }



  stages{
    stage("Init test"){
      steps{
        script{
          git credentialsId:'sldevopsd', branch: params.BRANCH, url:'git@github.com:Sealights/SL.BackendApiTests.git'
        }
      }
    }


    stage('download NodeJs agent and scanning Mocha tests') {
      steps{
        script{
          sh """
            export APP_NAME="${params.APP_NAME}"
            export BRANCH_NAME="${params.BRANCH}"
            export EXTERNAL_CUSTOMER_ID="integration"
            export EXTERNAL_USER_EMAIL="integration@sealights.io"
            export EXTERNAL_USER_PASSWORD="SeaLights2019!"

            npm install

            ./node_modules/.bin/tsc

            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Cucmber_framework_java.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_cypress.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_integration_build.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_jest.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Junit_support_testNG.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Junit_without_testNG_gradle.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Junit_without_testNG.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Mocha.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_MS_Tests.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_NUnit_Tests.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_postman.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Pytest.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Robot_tests.js --no-timeouts
            ./node_modules/mocha/bin/_mocha tsOutputs/BTQ/Modified-BTQ/btq_Soapui.js --no-timeouts
          """
        }
      }
    }
  }
}
