
pipeline {
  agent any


  parameters {
    string(name: 'APP_NAME', defaultValue: 'public-BTQ', description: 'name of the app (integration build)')
    string(name: 'COMPANY_NAME', defaultValue: '', description: 'name of the app (integration build)')
    string(name: 'BRANCH', defaultValue: 'public', description: 'Branch to clone')
    string(name: 'CHANGED_BRANCH', defaultValue: 'public', description: 'Branch to compare')
    string(name: 'BUILD_BRANCH', defaultValue: 'public', description: 'Branch to Build images that have the creational LAB_ID (send to public branch to build)')
    string(name: 'SL_TOKEN', defaultValue: 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYzNiM2IyY2YtYjA1Yy00ZWM2LThjNjYtZTBmZTJiYzIwNzAzLDE2OTI4Nzc3MDM4ODUiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE2OTI4Nzc3MDN9.dORXtjiTVw9vM3u2eO9l2r3f54NwEFPWVnhZnOWqV4_ZA-q2T86X861S6o4G7M371hMnoePRNoWgkjXp9isgEPEHoG_LQ_pvwc66vi5gBy8okjlypKGMTrz-N8bF1LeswguuSDDPIpm0Qq7KSjcm-GZmtO2IhJu4Q6f-tX0otMvvr6_nuwfVReExsT0Mxoyu0ZFs2HHwuIqhu12v1wNUuiTNIxQnGqckLw1qrroTG-qrDa8ydC111ML9C-u4qdS6G0iDsSdrQk9RETe0b1ow1vMXMFZeQ0vBrJDFjMnaCUhU6iid8xjkZG3T6XAI0k5SBRN8R6dtTO45mE638ohJi1_YBQL8hSkHL-8X_QkbRCH6IFqPcku0Wu2AcaRkBKOoiYAowFxnrQgYx5n_FVuTXNwW-s18Gnebd-bTBveCAHQH6CEbnpznXyMNXc15tOVdfp1n3RHLx9YE2lYI3dsTdwUlwNhto4J1Ym3ZOrLW_GZwLzZyIITfmNUOQVspwzsVOioeA48DZNpZhpZUAK5P19v0KY_iyJKxGajWnAUkXbyqc72d7eG5cUsIgv-r_p7fwnO4Rm1FVaZJ4Cpv7b4yf5YHGJ7BADI5Zw6YXuWQ3d9snZfvKOR50KVZGOykqwExYEwBACpN1WSEoIg8No7wTry_xNPmkTYOHbNoWuzyjTo', description: 'sl-token')
    string(name: 'BUILD_NAME', defaultValue: '', description: 'build name (should change on every build)')
    string(name: 'JAVA_AGENT_URL', defaultValue: 'https://storage.googleapis.com/cloud-profiler/java/latest/profiler_java_agent_alpine.tar.gz', description: 'use different java agent')
    string(name: 'DOTNET_AGENT_URL', defaultValue: 'https://agents.sealights.co/dotnetcore/latest/sealights-dotnet-agent-alpine-self-contained.tar.gz', description: 'use different dotnet agent')
    string(name: 'NODE_AGENT_URL', defaultValue: 'slnodejs', description: 'use different node agent')
    string(name: 'GO_AGENT_URL', defaultValue: 'https://agents.sealights.co/slgoagent/latest/slgoagent-linux-amd64.tar.gz', description: 'use different go agent')
    string(name: 'GO_SLCI_AGENT_URL', defaultValue: 'https://agents.sealights.co/slcli/latest/slcli-linux-amd64.tar.gz', description: 'use different slci go agent')
    string(name: 'PYTHON_AGENT_URL', defaultValue: 'sealights-python-agent', description: 'use different python agent')
  }

  stages {
    stage('Clone Repository') {
      steps {
        script {
          clone_repo(
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
          build_btq(
            sl_report_branch: params.BRANCH,
            sl_token: params.SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}",
            branch: params.BRANCH,
            mapurl: MapUrl
          )
        }
      }
    }

    stage('update-btq') {
      steps {
        script {
          def IDENTIFIER= "${params.BRANCH}-${env.CURRENT_VERSION}"
          env.LAB_ID = create_lab_id(
          token: "${params.SL_TOKEN}",
          machine: "https://dev-integration.dev.sealights.co",
          app: "${params.APP_NAME}",
          branch: "${params.BUILD_BRANCH}",
          test_env: "${IDENTIFIER}",
          lab_alias: "${IDENTIFIER}",
          cdOnly: true,
          )
          //env.LAB_ID = "integ_public_97ba_publicBTQ"

          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

          build(job: 'update-btq', parameters: [string(name: 'IDENTIFIER', value: "http://${params.machine_dns}"),
                                                string(name:'tag' , value:"${env.CURRENT_VERSION}"),
                                                string(name:'buildname' , value:"${params.BRANCH}-${env.CURRENT_VERSION}"),
                                                string(name:'labid' , value:"${env.LAB_ID}"),
                                                string(name:'branch' , value:"${params.BRANCH}"),
                                                string(name:'token' , value:"${env.TOKEN}"),
                                                string(name:'sl_branch' , value:"${params.BRANCH}")])
        }
      }
    }



    stage('Run Tests') {
      steps {
        script {
          run_tests(
            branch: params.BRANCH,
            lab_id: env.LAB_ID,
            token: params.SL_TOKEN,
            machine_dns: "http://${params.machine_dns}"
          )
        }
      }
    }



    stage('Changed - Clone Repository') {
      steps {
        script {
          clone_repo(
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
          build_btq(
            sl_report_branch: params.BRANCH,
            sl_token: params.SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}-changed",
            branch: params.BRANCH,
            mapurl: MapUrl
          )
        }
      }
    }



    stage('update-btq changed') {
      steps {
        script {
          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

          def IDENTIFIER= "${params.BRANCH}-${env.CURRENT_VERSION}"
          build(job: 'update-btq', parameters: [string(name: 'IDENTIFIER', value: "http://${params.machine_dns}"),
                                                string(name:'tag' , value:"${env.CURRENT_VERSION}"),
                                                string(name:'buildname' , value:"${params.BRANCH}-${env.CURRENT_VERSION}"),
                                                string(name:'labid' , value:"${env.LAB_ID}"),
                                                string(name:'branch' , value:"${params.CHANGED_BRANCH}"),
                                                string(name:'token' , value:"${env.TOKEN}"),
                                                string(name:'sl_branch' , value:"${params.CHANGED_BRANCH}")])
        }
      }
    }

    stage('Changed Run Tests') {
      steps {
        script {
          run_tests(
            branch: params.BRANCH,
            lab_id: env.LAB_ID,
            token: params.SL_TOKEN,
            machine_dns: "http://${params.machine_dns}"
          )
        }
      }
    }
  }
}

def get_secret (SecretID, Region, Profile="") {
  if (Profile != "") {
    Profile = "--profile ${Profile}"
  }
  String secret_key = "${SecretID.split('/')[-1]}" as String
  def secret_value = (sh(returnStdout: true, script: "aws secretsmanager get-secret-value --secret-id ${SecretID} --region ${Region} ${Profile}| jq -r '.SecretString' | jq -r '.${secret_key}'")).trim()
  return secret_value
}


def build_btq(Map params){
  env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

  def parallelLabs = [:]
  //List of all the images name
  env.TOKEN= "${params.sl_token}"

  def services_list = ["adservice","cartservice","checkoutservice", "currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice"]
  //def special_services = ["cartservice"].
  env.BUILD_NAME= "${params.build_name}" == "" ? "${params.branch}-${env.CURRENT_VERSION}" : "${params.build_name}"

  services_list.each { service ->
    parallelLabs["${service}"] = {
      def AGENT_URL = getParamForService(service , params.mapurl)
      build(job: 'BTQ-BUILD', parameters: [string(name: 'SERVICE', value: "${service}"),
                                           string(name:'TAG' , value:"${env.CURRENT_VERSION}"),
                                           string(name:'SL_REPORT_BRANCH' , value:"${params.sl_report_branch}"),
                                           string(name:'BRANCH' , value:"${params.branch}"),
                                           string(name:'BUILD_NAME' , value:"${env.BUILD_NAME}"),
                                           string(name:'SL_TOKEN' , value:"${env.TOKEN}"),
                                           string(name:'AGENT_URL' , value:AGENT_URL[0]),
                                           string(name:'AGENT_URL_SLCI' , value:AGENT_URL[1])])
    }
  }
  parallel parallelLabs
}

def getParamForService(service, mapurl) {

  switch (service) {
    case "adservice":
      return [mapurl['JAVA_AGENT_URL'].toString(),""]
    case "cartservice":
      return [mapurl['DOTNET_AGENT_URL'].toString(),""]
    case ["checkoutservice","frontend","productcatalogservice","shippingservice"]:
      return [mapurl['GO_AGENT_URL'].toString(),mapurl['GO_SLCI_AGENT_URL'].toString()]
    case ["emailservice","recommendationservice"]:
      return [mapurl['PYTHON_AGENT_URL'].toString(),""]
    case ["currencyservice","paymentservice"]:
      return [mapurl['NODE_AGENT_URL'].toString(),""]
  }
}


def run_tests(Map params){
      sleep time: 120, unit: 'SECONDS'
      build(job: "test_runner", parameters: [
        string(name: 'BRANCH', value: "${params.branch}"),
        string(name: 'SL_LABID', value: "${params.lab_id}"),
        string(name: 'SL_TOKEN', value: "${params.token}"),
        string(name: 'MACHINE_DNS', value: "http://${params.machine_dns}")
      ])

}


def run_api_tests_before_changes(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "StableApiTests", parameters: [
      string(name: 'BRANCH', value: "${params.branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}

def run_api_tests_after_changes(Map params){
  catchError(buildResult: 'SUCCESS', stageResult: 'FAILURE') {
    build(job: "ApiTests", parameters: [
      string(name: 'BRANCH', value: "${params.branch}"),
      string(name: 'APP_NAME', value: "${params.app_name}")
    ])
  }
}



def clone_repo(Map params){
  // Clone the repository with the specified branch
  git branch: params.branch, url: 'https://github.com/Sealights/microservices-demo-template.git'
}

def set_assume_role(Map params) {
  params.set_globaly = params.set_globaly == null ? true : params.set_globaly
  def credential_map = sh (returnStdout: true, script: """
                                aws sts assume-role --role-arn arn:aws:iam::${params.account_id}:role/${params.role_name}  \\
                                --role-session-name ${params.env}-access --query \"Credentials\"
                            """).replace('"', '').replaceAll('[\\s]', '').trim()

  def map = convert_to_map(credential_map)
  if (params.set_globaly) {
    env.AWS_ACCESS_KEY_ID = "${map.AccessKeyId}"
    env.AWS_SECRET_ACCESS_KEY = "${map.SecretAccessKey}"
    env.AWS_SESSION_TOKEN = "${map.SessionToken}"
  } else {
    return map
  }
}

def create_lab_id(Map params) {
  try {
    def cdOnlyString = ""
    if (params.cdOnly){
      cdOnlyString = ', "cdOnly": true'
    }
    if (params.isPR){
      env.LAB_ID = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids/pull-request" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "targetBranch": "${params.target_branch}", "isHidden": true }' | grep -o '"labId": *"[^"]*"' your_file.json | awk -F'"' '{print $4}'
           """)).trim()
    } else {
      env.LAB_ID = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "labAlias": "${params.lab_alias}", "isHidden": true ${cdOnlyString}}' | grep -o '"labId": *"[^"]*"' your_file.json | awk -F'"' '{print $4}'
           """)).trim()
    }
    echo "LAB ID: ${env.LAB_ID}"
    return env.LAB_ID
  } catch (err) {
    echo env.LAB_ID
    error "Failed to create lab id"
  }
}

def convert_to_map(mapAsString) {
  def map =
    // Take the String value between
    // the [ and ] brackets.
    mapAsString[1..-2]
    // Split on , to get a List.
      .split(',')
    // Each list item is transformed
    // to a Map entry with key/value.
      .collectEntries { entry ->
        def pair = entry.split(':')
        [(pair.first()): "${pair.last()}"]
      }

  return map
}





