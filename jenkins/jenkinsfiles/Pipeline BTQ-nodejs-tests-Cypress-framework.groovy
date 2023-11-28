pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/BTQ_BUILD_shell_kaniko_pod.yaml')
      defaultContainer "shell"
    }
  }
    
    parameters {
        string(name: 'BRANCH', defaultValue: 'public', description: 'Branch to clone (ahmad-branch)')
        string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
        string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
        string(name: 'MACHINE_DNS1', defaultValue: '', description: 'machine dns')
        
        
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
                    export CYPRESS_SL_TEST_STAGE="Cypress-Test-Stage"
                    export MACHINE_DNS="${params.MACHINE_DNS1}" 
                    export CYPRESS_SL_LAB_ID="${params.SL_LABID}"
                    export CYPRESS_SL_TOKEN="${params.SL_TOKEN}"
                    npx cypress run --spec "cypress/integration/api.spec.js" 
                    """
                }
            }
        }
    }
    
}
