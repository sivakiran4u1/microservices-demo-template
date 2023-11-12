@Library('main-shared-library@ahmad-branch') _
pipeline {
  agent {
    kubernetes {
      yaml kubernetes.base_pod([
        base_image_uri       : "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-base-ci:latest",
        ecr_uri              : "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        shell_memory_request : "1000Mi",
        shell_cpu_request    : "1.5",
        shell_memory_limit   : "2000Mi",
        shell_cpu_limit      : "2",
        kaniko_memory_request: "500Mi",
        kaniko_cpu_request   : "0.5",
        kaniko_memory_limit  : "600Mi",
        kaniko_cpu_limit     : "0.6",
        node_selector        : "nightly"
      ])
      defaultContainer 'shell'
    }
  }

  parameters {
    string(name: 'APP_NAME', defaultValue: 'ahmad-BTQ', description: 'name of the app (integration build)')
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'CHANGED_BRANCH', defaultValue: 'new-changed', description: 'Branch to clone (ahmad-branch)')
    string(name: 'BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to clone (ahmad-branch)')
    string(name: 'BUILD_BRANCH', defaultValue: 'ahmad-branch', description: 'Branch to Build images that have the creational LAB_ID (send to ahmad branch to build)')
    string(name: 'SL_TOKEN', defaultValue: 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctZDQ3YTRjYzAtOWM0MC00ZWIxLTg2NzYtMTA3NzMyOTk5YTA5LDE2OTg1OTU5ODUwOTYiLCJzdWJqZWN0IjoiaW50ZWdyYXRpb25AYWdlbnQiLCJhdWRpZW5jZSI6WyJhZ2VudHMiXSwieC1zbC1yb2xlIjoiYWdlbnQiLCJ4LXNsLXNlcnZlciI6Imh0dHBzOi8vZGV2LWludGVncmF0aW9uLmRldi5zZWFsaWdodHMuY28vYXBpIiwic2xfaW1wZXJfc3ViamVjdCI6IiIsImlhdCI6MTY5ODU5NTk4NX0.Zluo-1ixxNHU3QflitHZJAD3mGzDLUv2ntje-ENd_sIIfAV7t5-1AjZz5qScK9-3pKaamv3398GzlVcwWunNqpug0DfF2uCKHDb_pTBnQdbIOfLdh-d5fXHoXgSrNCSBXRsgjkuA0Sdit1jFlc198OE2FVawkLhf6MtX-6NHncV6EEyZ833i4DRvLt58IQRrDH0q6eGoG_i4ea2iwC-3TNhVskxigxCRIh2ZamNjqasXk3fJchoawKUlYKKPLyYaoPXtyWY1bCdR7UMyUmewUIJy9XBXjy7GcXNuouqu0638vjceY-IDWI6jyrAzBetAfybvDe8eVdT19cLSDP5Q_1MeVUbhfzFjkv4bUEy7kBd2ghtoMTSBAkaoRXD687MxRKanYTyrRbOOW1fXzGk-dtiWLSKAvAFsJrjibEf3EdnD8ewMvtbZWGVfn35gWffB41USTGqkkEY7sSpsEgW4nLATvYQv_nZY2bIBY2zaYi6Kklk3L2vp8i3zelZHm3dJ_IxZYDfJEp-TW0JgOdbhDgV6OjVxgW4YkGYYc58vuBpA6rIluU3OdBp9Vcr3_recnZLA3V6QtLyowOXwRgHOHaI9RSDJ5h1LG81N0nkOmrGbBiTW8vKI3taFrtu163cD0NFguYUjiZus-5gkQ1TU0bEsD3XIxh-4rTR0C2fmqXI', description: 'sl-token')
    string(name: 'BUILD_NAME', defaultValue: 'ahmad-1', description: 'build name')
    string(name: 'JAVA_AGENT_URL', defaultValue: 'https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent_alpine.tar.gz', description: 'use different java agent')
    string(name: 'DOTNET_AGENT_URL', defaultValue: 'https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz', description: 'use different dotnet agent')
    string(name: 'NODE_AGENT_URL', defaultValue: 'slnodejs', description: 'use different node agent')
    string(name: 'GO_AGENT_URL', defaultValue: 'https://agents.sealights.co/slgoagent/latest/slgoagent-linux-amd64.tar.gz', description: 'use different go agent')
    string(name: 'GO_SLCI_AGENT_URL', defaultValue: 'https://agents.sealights.co/slcli/latest/slcli-linux-amd64.tar.gz', description: 'use different slci go agent')
    string(name: 'PYTHON_AGENT_URL', defaultValue: 'sealights-python-agent', description: 'use different python agent')
    choice(name: 'TEST_TYPE', choices: ['All Tests IN One Image', 'Tests sequential', 'Tests parallel'], description: 'Choose test type')
  }


  environment {
    DEV_INTEGRATION_SL_TOKEN = secrets.get_secret("mgmt/btq_token", "us-west-2")
  }

  stages {
    stage('Clone Repository') {
      steps {
        script {
          boutique.clone_repo(
            branch: params.BRANCH
          )
        }
      }
    }


    //Build parallel images
    stage('Build BTQ') {
      steps {
        script {
          def MapUrl = new HashMap()
          MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
          MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
          MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
          MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
          MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
          MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

          boutique.build_btq(
            sl_token: params.SL_TOKEN,
            dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}",
            branch: params.BRANCH,
            mapurl: MapUrl
          )
        }
      }
    }

    stage('Spin-Up BTQ') {
      steps {
        script {
          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

          def IDENTIFIER= "${params.BRANCH}-${env.CURRENT_VERSION}"
          boutique.SpinUpBoutiqeEnvironment(
            IDENTIFIER : IDENTIFIER,
            branch: params.BRANCH,
            app_name: params.APP_NAME,
            build_branch: params.BUILD_BRANCH,
            java_agent_url: params.JAVA_AGENT_URL,
            dotnet_agent_url: params.DOTNET_AGENT_URL,
            sl_branch : params.BRANCH,
            git_branch : params.BUILD_BRANCH
          )
        }
      }
    }

    stage('Run Tests') {
      steps {
        script {
          boutique.run_tests(
            branch: params.BRANCH,
            test_type: params.TEST_TYPE
          )
        }
      }
    }

    stage('Run Api-Tests Before Changes') {
      steps {
        script {
          boutique.run_api_tests_before_changes(
            branch: params.BRANCH,
            app_name: params.APP_NAME
          )
        }
      }
    }


    stage('Changed - Clone Repository') {
      steps {
        script {


          boutique.clone_repo(
            branch: params.CHANGED_BRANCH
          )
        }
      }
    }

    stage('Changed Build BTQ') {
      steps {
        script {
          def MapUrl = new HashMap()
          MapUrl.put('JAVA_AGENT_URL', "${params.JAVA_AGENT_URL}")
          MapUrl.put('DOTNET_AGENT_URL', "${params.DOTNET_AGENT_URL}")
          MapUrl.put('NODE_AGENT_URL', "${params.NODE_AGENT_URL}")
          MapUrl.put('GO_AGENT_URL', "${params.GO_AGENT_URL}")
          MapUrl.put('GO_SLCI_AGENT_URL', "${params.GO_SLCI_AGENT_URL}")
          MapUrl.put('PYTHON_AGENT_URL', "${params.PYTHON_AGENT_URL}")

          boutique.build_btq(
            sl_token: params.SL_TOKEN,
            dev_integraion_sl_token: env.DEV_INTEGRATION_SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}",
            branch: params.CHANGED_BRANCH,
            mapurl: MapUrl
          )
        }
      }
    }



    stage('Changed Spin-Up BTQ') {
      steps {
        script {
          def IDENTIFIER= "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"

          boutique.SpinUpBoutiqeEnvironment(
            IDENTIFIER : IDENTIFIER,
            branch: params.CHANGED_BRANCH,
            git_branch : params.CHANGED_BRANCH,
            app_name: params.APP_NAME,
            build_branch: params.BRANCH,
            java_agent_url: params.JAVA_AGENT_URL,
            dotnet_agent_url: params.DOTNET_AGENT_URL,
            sl_branch : params.BRANCH
          )
        }
      }
    }

    stage('Changed Run Tests') {
      steps {
        script {
          boutique.run_tests(
            branch: params.BRANCH,
            test_type: params.TEST_TYPE
          )
        }
      }
    }


    stage('Run API-Tests After Changes') {
      steps {
        script {
          boutique.run_api_tests_after_changes(
            branch: params.BRANCH,
            app_name: params.APP_NAME
          )
        }
      }
    }
  }

  post {
    success {
      script {
        boutique.success_btq(
          IDENTIFIER : "${params.BRANCH}-${env.CURRENT_VERSION}"
        )
        boutique.success_btq(
          IDENTIFIER : "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"
        )
      }
    }
    failure {
      script {
        sts.set_assume_role([
          env       : "dev",
          account_id: "159616352881",
          role_name : "CD-TF-Role"
        ])
        boutique.failure_btq(
          IDENTIFIER : "${params.BRANCH}-${env.CURRENT_VERSION}"
        )
        boutique.failure_btq(
          IDENTIFIER : "${params.CHANGED_BRANCH}-${env.CURRENT_VERSION}"
        )
      }
    }
  }
}


