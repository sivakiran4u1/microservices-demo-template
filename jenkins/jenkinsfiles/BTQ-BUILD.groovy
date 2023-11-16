@Library('main-shared-library') _
pipeline{
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/BTQ_BUILD_shell_kaniko_pod.yaml')
      defaultContainer "shell"
    }
  }

  options {
    buildDiscarder logRotator(numToKeepStr: '100')
    timestamps()
  }
  parameters {
    string(name: 'TAG', defaultValue: '1.2.2', description: 'latest tag')
    string(name: 'BRANCH', defaultValue: 'main', description: 'default branch')
    string(name: 'SL_REPORT_BRANCH', defaultValue: 'main', description: 'default branch')
    //string(name: 'ecr_uri1', defaultValue: '534369319675.dkr.ecr.us-west-2.amazonaws.com/btq', description: 'ecr btq')
    string(name: 'SERVICE', defaultValue: '', description: 'SErvice name to build')
    string(name: 'machine_dns', defaultValue: 'http://DEV-${env.IDENTIFIER}.dev.sealights.co', description: 'machine DNS')
    string(name: 'BUILD_NAME', defaultValue: 'none', description: 'build name')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'build token')
    string(name: 'AGENT_URL', defaultValue: '', description: 'agent version')
    string(name: 'AGENT_URL_SLCI', defaultValue: '', description: 'agent slci version')
  }
  environment{
    ECR_FULL_NAME = "btq-${params.SERVICE}"
    ECR_URI = "534369319675.dkr.ecr.us-west-2.amazonaws.com/${env.ECR_FULL_NAME}"
  }
  stages{
    stage('Init') {
      steps {
        script {
          // Clone the repository with the specified branch.
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
          stage("Create ECR repository") {
            def repo_policy = libraryResource 'ci/ecr/repo_policy.json'
            ecr.create_repo([
              artifact_name: "${env.ECR_FULL_NAME}",
              key_type: "KMS"
            ])
            ecr.set_repo_policy([
              artifact_name: "${env.ECR_FULL_NAME}",
              repo_policy: repo_policy
            ])
          }
          stage("Build Docker ${params.SERVICE} Image") {
            container(name: 'kaniko'){
              script {
                def CONTEXT = params.SERVICE == "cartservice" ? "./src/${params.SERVICE}/src" : "./src/${params.SERVICE}"
                def DP = "${CONTEXT}/Dockerfile"
                def D = "${env.ECR_URI}:${params.TAG}"
                def BRANCH = params.SL_REPORT_BRANCH
                def BUILD_NAME = params.BUILD_NAME
                def SL_TOKEN = params.SL_TOKEN
                def AGENT_URL = params.AGENT_URL
                def AGENT_URL_SLCI = params.AGENT_URL_SLCI

                sh """
                    /kaniko/executor \
                    --context ${CONTEXT} \
                    --dockerfile ${DP} \
                    --destination ${D} \
                    --build-arg BRANCH=${BRANCH} \
                    --build-arg BUILD_NAME=${BUILD_NAME} \
                    --build-arg SEALIGHTS_TOKEN=${SL_TOKEN} \
                    --build-arg AGENT_URL=${AGENT_URL} \
                    --build-arg AGENT_URL_SLCI=${AGENT_URL_SLCI}
                """
              }
            }
          }
        }
      }
    }
  }
}
