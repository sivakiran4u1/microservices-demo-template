@Library('main-shared-library') _

pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/BTQ-python-tests-Pytest-framework.yaml')
      defaultContainer "shell"
    }
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'main', description: 'Branch to clone')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    string(name: 'MACHINE_DNS1', defaultValue: '', description: 'machine dns')
  }
  options{
    buildDiscarder logRotator(numToKeepStr: '30')
    timestamps()
  }



  stages{
    stage("Init test"){
      steps{
        script{
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
      }
    }


    stage('download python agent and scanning tests') {
      steps{
        script{
          sh """

                    pip install sealights-python-agent
                    pip install pytest
                    pip install requests
                    export machine_dns="${params.MACHINE_DNS1}"

                    sl-python pytest --teststage "Pytest tests"  --labid ${params.SL_LABID} --token ${params.SL_TOKEN} integration-tests/python-tests/python-tests.py



                    """
        }
      }
    }
  }
}
