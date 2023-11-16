@Library('main-shared-library') _
pipeline{
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        shell_memory_request: "2000Mi",
        shell_cpu_request: "1.0",
        shell_memory_limit: "3000Mi",
        shell_cpu_limit: "1.5",
        kaniko_memory_request: "3500Mi",
        kaniko_cpu_request: "1.0",
        kaniko_memory_limit: "4500Mi",
        kaniko_cpu_limit: "2.5",
        kaniko_storage_limit:"6500Mi",
        node_selector: "jenkins"
      ])
      defaultContainer 'shell'
    }
  }
  parameters {
    string(name: 'TAG', defaultValue: '1.2.2', description: 'latest tag')
    string(name: 'BRANCH', defaultValue: 'main', description: 'defult branch')
    //string(name: 'ecr_uri1', defaultValue: '534369319675.dkr.ecr.us-west-2.amazonaws.com/btq', description: 'ecr btq')
    string(name: 'LANG', defaultValue: '', description: 'Service name to build')
    string(name: 'BUILD_NAME', defaultValue: 'none', description: 'build name')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'build token')
    string(name: 'AGENT_URL', defaultValue: '', description: 'agent version')
  }
  environment{
    ECR_FULL_NAME = "btq-${params.LANG}"
    ECR_URI = "ahmadSealights/${env.ECR_FULL_NAME}"
  }
  stages{
    stage('Init') {
      steps {
        script {
          // Clone the repository with the specified branch.
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
          }
          stage("Build Docker ${params.LANG} Image") {
           container(name: 'kaniko'){
            script {
                def CONTEXT = "./initContainers/${params.LANG}InitContainer"
                def DP = "${CONTEXT}/Dockerfile"
                def D = "${env.ECR_URI}:${params.TAG}"
                def BUILD_NAME = params.BUILD_NAME
                def SL_TOKEN = params.SL_TOKEN
                def AGENT_URL = params.AGENT_URL
                def AGENT_URL_SLCI = params.AGENT_URL_SLCI

                sh """
                    /kaniko/executor \
                    --context ${CONTEXT} \
                    --dockerfile ${DP} \
                    --destination ${D} \
                    --build-arg BUILD_NAME=${BUILD_NAME} \
                    --build-arg SEALIGHTS_TOKEN=${SL_TOKEN} \
                    --build-arg AGENT_URL=${AGENT_URL}
                """
                }
            }
          }
        }
      }
    }
  }
}
