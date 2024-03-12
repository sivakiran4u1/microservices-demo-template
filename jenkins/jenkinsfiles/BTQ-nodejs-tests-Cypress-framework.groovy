pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/cypress.yaml')
      defaultContainer "shell"
    }
  }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'public', description: 'Branch to clone (ahmad-branch)')
        string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    }
    environment {
        SL_TOKEN = (sh(returnStdout: true, script:"aws secretsmanager get-secret-value --region eu-west-1 --secret-id 'btq/template_token' | jq -r '.SecretString' | jq -r '.template_token'" )).trim()
        MACHINE_DNS = 'http://54.246.240.122:8081'
    }
    options{
        buildDiscarder logRotator(numToKeepStr: '10')
        timestamps()
    }
    
    
    
    stages{
        stage("Init test"){
            steps{
                script{
                git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo-template.git'   
                }
            }
        }
        
        
        stage('download NodeJs agent and scanning Cypress tests') {
            steps{
                script{
                    sh """
                    cd integration-tests/cypress/
                    npm install 
                    npm install sealights-cypress-plugin
                    export NODE_DEBUG=sl
                    export CYPRESS_SL_ENABLE_REMOTE_AGENT=true
                    export CYPRESS_SL_TEST_STAGE="Cypress-Test-Stage"
                    export MACHINE_DNS="${env.MACHINE_DNS}" 
                    export CYPRESS_SL_LAB_ID="${params.SL_LABID}"
                    export CYPRESS_SL_TOKEN="${env.SL_TOKEN}"
                    npx cypress run
                    """
                }
            }
        }
    }
    
}
