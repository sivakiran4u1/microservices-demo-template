
pipeline {
  agent {
    kubernetes {
      yaml readTrusted('jenkins/pod-templates/CI_shell_pod.yaml')
      defaultContainer "shell"
    }
  }


  parameters {
    string(name: 'APP_NAME', defaultValue: 'public-BTQ', description: 'name of the app (integration build)')
    string(name: 'machine_dns', defaultValue: 'your_dns', description: 'name of the app (integration build)')
    string(name: 'BRANCH', defaultValue: 'public', description: 'Branch to clone')
    string(name: 'CHANGED_BRANCH', defaultValue: 'changed-branch', description: 'Branch to compare')
    string(name: 'BUILD_BRANCH', defaultValue: 'public', description: 'Branch to Build images that have the creational LAB_ID (send to public branch to build)')
    string(name: 'SL_TOKEN', defaultValue: 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYzNiM2IyY2YtYjA1Yy00ZWM2LThjNjYtZTBmZTJiYzIwNzAzLDE2OTI4Nzc3MDM4ODUiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE2OTI4Nzc3MDN9.dORXtjiTVw9vM3u2eO9l2r3f54NwEFPWVnhZnOWqV4_ZA-q2T86X861S6o4G7M371hMnoePRNoWgkjXp9isgEPEHoG_LQ_pvwc66vi5gBy8okjlypKGMTrz-N8bF1LeswguuSDDPIpm0Qq7KSjcm-GZmtO2IhJu4Q6f-tX0otMvvr6_nuwfVReExsT0Mxoyu0ZFs2HHwuIqhu12v1wNUuiTNIxQnGqckLw1qrroTG-qrDa8ydC111ML9C-u4qdS6G0iDsSdrQk9RETe0b1ow1vMXMFZeQ0vBrJDFjMnaCUhU6iid8xjkZG3T6XAI0k5SBRN8R6dtTO45mE638ohJi1_YBQL8hSkHL-8X_QkbRCH6IFqPcku0Wu2AcaRkBKOoiYAowFxnrQgYx5n_FVuTXNwW-s18Gnebd-bTBveCAHQH6CEbnpznXyMNXc15tOVdfp1n3RHLx9YE2lYI3dsTdwUlwNhto4J1Ym3ZOrLW_GZwLzZyIITfmNUOQVspwzsVOioeA48DZNpZhpZUAK5P19v0KY_iyJKxGajWnAUkXbyqc72d7eG5cUsIgv-r_p7fwnO4Rm1FVaZJ4Cpv7b4yf5YHGJ7BADI5Zw6YXuWQ3d9snZfvKOR50KVZGOykqwExYEwBACpN1WSEoIg8No7wTry_xNPmkTYOHbNoWuzyjTo', description: 'sl-token')
    string(name: 'BUILD_NAME', defaultValue: '', description: 'build name (should change on every build)')
    
    booleanParam(name: 'MY_BOOLEAN', defaultValue: true, description: 'My boolean')
    choice(name: 'MODIFIED_COVERAGE', choices: ['OFF', 'ON'], description: 'MODIFIED COVERAGE')

  }

  stages {
    stage('Clone Repository') {
      steps {
        script {
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo-template.git'
          
        }
      }
    }
    //Build parallel images
    stage('Build BTQ') {
      steps {
        script {
          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"

          build_btq(
            sl_report_branch: params.BRANCH,
            sl_token: params.SL_TOKEN,
            build_name: "1-0-${BUILD_NUMBER}",
            branch: params.BRANCH,
            tag: env.CURRENT_VERSION,
          )
        }
      }
    }

    stage('update-btq') {
      steps {
        script {
          env.CURRENT_VERSION = "1-0-${BUILD_NUMBER}"
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


          build(job: 'update-btq', parameters: [string(name: 'IDENTIFIER', value: "${params.machine_dns}"),
                                                string(name:'tag' , value:"${env.CURRENT_VERSION}"),
                                                string(name:'buildname' , value:"${params.BRANCH}-${env.CURRENT_VERSION}"),
                                                string(name:'labid' , value:"${env.LAB_ID}"),
                                                string(name:'branch' , value:"${params.BRANCH}"),
                                                string(name:'token' , value:"${params.SL_TOKEN}"),
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
            machine_dns: "${params.machine_dns}:8081"
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
  

  def parallelLabs = [:]
  //List of all the images name
  params.SL_TOKEN= "${params.sl_token}"

  def services_list = ["adservice","cartservice","checkoutservice", "currencyservice","emailservice","frontend","paymentservice","productcatalogservice","recommendationservice","shippingservice"]
  //def special_services = ["cartservice"].
  env.BUILD_NAME= "${params.build_name}" == "" ? "${params.branch}-${env.CURRENT_VERSION}" : "${params.build_name}"

  services_list.each { service ->
    parallelLabs["${service}"] = {
      build(job: 'BTQ-BUILD', parameters: [string(name: 'SERVICE', value: "${service}"),
                                           string(name:'TAG' , value:"${params.tag}"),
                                           string(name:'SL_REPORT_BRANCH' , value:"${params.sl_report_branch}"),
                                           string(name:'BRANCH' , value:"${params.branch}"),
                                           string(name:'BUILD_NAME' , value:"${env.BUILD_NAME}"),
                                           string(name:'SL_TOKEN' , value:"${params.SL_TOKEN}")])
    }
  }
  parallel parallelLabs
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
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids/pull-request" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "targetBranch": "${params.target_branch}", "isHidden": true }' | jq -r '.data.labId'
           """)).trim()
    } else {
      env.LAB_ID = (sh(returnStdout: true, script:"""
            #!/bin/sh -e +x
            curl -X POST "${params.machine}/sl-api/v1/agent-apis/lab-ids" -H "Authorization: Bearer ${params.token}" -H "Content-Type: application/json" -d '{ "appName": "${params.app}", "branchName": "${params.branch}", "testEnv": "${params.test_env}", "labAlias": "${params.lab_alias}", "isHidden": true ${cdOnlyString}}' | jq -r '.data.labId'
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
