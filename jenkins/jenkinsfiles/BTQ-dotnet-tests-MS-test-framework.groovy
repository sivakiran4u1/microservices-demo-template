@Library('main-shared-library') _

pipeline {
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        template_path: "ci/pod_templates/shell_pod.yaml",
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-dotnet-ci-alpine:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        memory_request: "100Mi",
        memory_limit: "3000Mi",
        cpu_request: "1",
        cpu_limit: "2.5",
        node_selector: "nightly"
      ])
      defaultContainer 'shell'
    }
  }
  options {
    buildDiscarder logRotator(numToKeepStr: '30')
    timestamps()
  }

  parameters {
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'SL_TOKEN', defaultValue: '', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: '', description: 'Lab_id')
    string(name: 'MACHINE_DNS1', defaultValue: '', description: 'machine dns')

  }

  stages{
    stage("Init test"){
      steps{
        script{
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
      }
    }


    stage('download DotNet agent') {
      steps{
        script{
          sh """


                    wget -nv -O sealights-dotnet-agent-alpine.tar.gz https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz
                    mkdir sl-dotnet-agent && tar -xzf ./sealights-dotnet-agent-alpine.tar.gz --directory ./sl-dotnet-agent
                    echo "[Sealights] .NetCore Agent version is: `cat ./sl-dotnet-agent/version.txt`"

                    ls
                    export machine_dns="${params.MACHINE_DNS1}"

                    env

                    dotnet ./sl-dotnet-agent/SL.DotNet.dll testListener --workingDir .  --target dotnet   --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN} --targetArgs "test integration-tests/dotnet-tests/MS-Tests/"

                    """
        }
      }
    }

  }
}
