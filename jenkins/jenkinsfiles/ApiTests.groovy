@Library('main-shared-library') _

pipeline {
  agent {
    kubernetes {
      yaml base_pod([
        template_path: "microservices-demo/jenkins/pod-templates/shell_pod_all_in.yaml",
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        memory_request: "1500Mi",
        memory_limit: "3000Mi",
        cpu_request: "2",
        cpu_limit: "3.5",
        node_selector: "nightly"
      ])
      defaultContainer 'shell'
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

def base_pod(Map params) {
  params["job_name"] = params.job_name == null || params.job_name == "" ? "${JOB_NAME}-${BUILD_NUMBER}" : params.job_name

  params["kaniko_memory_request"] = params.kaniko_memory_request == null || params.kaniko_memory_request == "" ? "250Mi" : params.kaniko_memory_request
  params["kaniko_cpu_request"] = params.kaniko_cpu_request == null || params.kaniko_cpu_request == "" ? "500m" : params.kaniko_cpu_request
  params["kaniko_memory_limit"] = params.kaniko_memory_limit == null || params.kaniko_memory_limit == "" ? "1400Mi" : params.kaniko_memory_limit
  params["kaniko_cpu_limit"] = params.kaniko_cpu_limit == null || params.kaniko_cpu_limit == ""  ? "1400m" : params.kaniko_cpu_limit

  params["shell_memory_request"] = params.shell_memory_request == null || params.shell_memory_request == "" ? "250Mi" : params.shell_memory_request
  params["shell_cpu_request"] = params.shell_cpu_request == null || params.shell_cpu_request == "" ? "1000m" : params.shell_cpu_request
  params["shell_storage_request"] = params.shell_storage_request == null || params.shell_storage_request == "" ? "2000Mi" : params.shell_storage_request
  params["shell_memory_limit"] = params.shell_memory_limit == null || params.shell_memory_limit == "" ? "2500Mi" : params.shell_memory_limit
  params["shell_cpu_limit"] = params.shell_cpu_limit == null || params.shell_cpu_limit == ""  ? "2000m" : params.shell_cpu_limit
  params["shell_storage_limit"] = params.shell_storage_limit == null || params.shell_storage_limit == "" ? "4000Mi" : params.shell_storage_limit

  params["storage_request"] = params.storage_request == null || params.storage_request == "" ? "500Mi" : params.storage_request
  params["storage_limit"] = params.storage_limit == null || params.storage_limit == "" ? "1500Mi" : params.storage_limit

  params["kaniko_storage_request"] = params.kaniko_storage_request == null || params.kaniko_storage_request == "" ? "2000Mi" : params.kaniko_storage_request
  params["kaniko_storage_limit"] = params.kaniko_storage_limit == null || params.kaniko_storage_limit == "" ? "3200Mi" : params.kaniko_storage_limit

  params["node_selector"] = params.node_selector == null || params.node_selector == "" ? "jenkins" : params.node_selector


  def template_path = (params.template_path == null) ? "microservices-demo/jenkins/pod-templates/shell_pod_all_in.yaml" : params.template_path
  def pod_template = libraryResource "${template_path}"

  def bindings = [params: params]
  def engine = new groovy.text.GStringTemplateEngine()
  pod_template = engine.createTemplate(pod_template).make(bindings).toString()

  return pod_template
}

