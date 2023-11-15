pipeline {
  agent {
    kubernetes {
      yaml base_pod([
        template_path: "microservices-demo/jenkins/pod-templates/shell_pod.yaml",
        base_image_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com/sl-jenkins-all-in:latest",
        ecr_uri: "534369319675.dkr.ecr.us-west-2.amazonaws.com",
        memory_request: "5000Mi",
        memory_limit: "10000Mi",
        cpu_request: "2",
        cpu_limit: "6",
        storage_limit:"10000Mi",
        node_selector: "jenkins"
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
    string(name: 'SL_TOKEN', defaultValue: 'eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYzNiM2IyY2YtYjA1Yy00ZWM2LThjNjYtZTBmZTJiYzIwNzAzLDE2OTI4Nzc3MDM4ODUiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE2OTI4Nzc3MDN9.dORXtjiTVw9vM3u2eO9l2r3f54NwEFPWVnhZnOWqV4_ZA-q2T86X861S6o4G7M371hMnoePRNoWgkjXp9isgEPEHoG_LQ_pvwc66vi5gBy8okjlypKGMTrz-N8bF1LeswguuSDDPIpm0Qq7KSjcm-GZmtO2IhJu4Q6f-tX0otMvvr6_nuwfVReExsT0Mxoyu0ZFs2HHwuIqhu12v1wNUuiTNIxQnGqckLw1qrroTG-qrDa8ydC111ML9C-u4qdS6G0iDsSdrQk9RETe0b1ow1vMXMFZeQ0vBrJDFjMnaCUhU6iid8xjkZG3T6XAI0k5SBRN8R6dtTO45mE638ohJi1_YBQL8hSkHL-8X_QkbRCH6IFqPcku0Wu2AcaRkBKOoiYAowFxnrQgYx5n_FVuTXNwW-s18Gnebd-bTBveCAHQH6CEbnpznXyMNXc15tOVdfp1n3RHLx9YE2lYI3dsTdwUlwNhto4J1Ym3ZOrLW_GZwLzZyIITfmNUOQVspwzsVOioeA48DZNpZhpZUAK5P19v0KY_iyJKxGajWnAUkXbyqc72d7eG5cUsIgv-r_p7fwnO4Rm1FVaZJ4Cpv7b4yf5YHGJ7BADI5Zw6YXuWQ3d9snZfvKOR50KVZGOykqwExYEwBACpN1WSEoIg8No7wTry_xNPmkTYOHbNoWuzyjTo', description: 'SL_TOKEN')
    string(name: 'SL_LABID', defaultValue: 'integ_ahmadbranch_d842_BTQ@ahmad-branch-1-0-267', description: 'Lab_id')
    string(name: 'MACHINE_DNS', defaultValue: 'http://10.2.11.97:8081', description: 'machine dns')

  }
  environment {
    MACHINE_DNS = "${params.MACHINE_DNS}"
    machine_dns = "${params.MACHINE_DNS}"
  }
  stages{
    stage("Init test"){
      steps{
        script{
          git branch: params.BRANCH, url: 'https://github.com/Sealights/microservices-demo.git'
        }
      }
    }
    stage('Cypress framework starting'){
      steps{
        script{
          build(job:"BTQ-nodejs-tests-Cypress-framework", parameters: [string(name: 'BRANCH', value: "${params.BRANCH}"),string(name: 'SL_LABID', value: "${params.SL_LABID}") , string(name:'SL_TOKEN' , value:"${params.SL_TOKEN}") ,string(name:'MACHINE_DNS1' , value:"${params.MACHINE_DNS}")])
        }
      }
    }

    stage('MS-Tests framework'){
      steps{
        script{
          sh """
                echo 'MS-Tests framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll testListener --workingDir .  --target dotnet   --testStage "MS-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/MS-Tests/"
                """
        }
      }
    }


    stage('N-Unit framework starting'){
      steps{
        script{
          sh """
                echo 'N-Unit framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                dotnet /sealights/sl-dotnet-agent/SL.DotNet.dll testListener --workingDir .  --target dotnet   --testStage "NUnit-Tests" --labId ${params.SL_LABID} --token ${params.SL_TOKEN} --targetArgs "test ./integration-tests/dotnet-tests/NUnit-Tests/"
                """
        }
      }
    }


    stage('Gradle framework'){
      steps{
        script{
          sh """
                    #!/bin/bash
                    export machine_dns="${params.MACHINE_DNS}"
                    cd ./integration-tests/java-tests-gradle
                    echo $SL_TOKEN>sltoken.txt
                    echo '{
                        "executionType": "testsonly",
                        "tokenFile": "./sltoken.txt",
                        "createBuildSessionId": false,
                        "testStage": "Junit without testNG-gradle",
                        "runFunctionalTests": true,
                        "labId": "${SL_LABID}",
                        "proxy": null,
                        "logEnabled": false,
                        "logDestination": "console",
                        "logLevel": "warn",
                        "sealightsJvmParams": {}
                    }' > slgradletests.json


                    echo "Adding Sealights to Tests Project gradle file..."
                    java -jar /sealights/sl-build-scanner.jar -gradle -configfile slgradletests.json -workspacepath .
                    gradle test


                    """
        }
      }
    }
    stage('robot framework'){
      steps{
        script{
          sh """
                    echo "the env var is $machine_dns"
                    export machine_dns="${params.MACHINE_DNS}"
                    echo 'robot framework starting ..... '
                    cd ./integration-tests/robot-tests
                    sl-python start --labid ${SL_LABID} --token ${SL_TOKEN} --teststage "Robot Tests"
                    robot -xunit api_tests.robot
                    sl-python uploadreports --reportfile "unit.xml" --labid ${SL_LABID} --token ${SL_TOKEN}
                    sl-python end --labid ${SL_LABID} --token ${SL_TOKEN}
                    cd ../..
                    """
        }
      }
    }

    stage('Cucumber framework') {
      steps{
        script{
          sh """
                    #!/bin/bash
                    export machine_dns="${params.MACHINE_DNS}"
                    echo 'Cucumber framework starting ..... '
                    cd ./integration-tests/cucumber-framework/
                    echo ${params.SL_TOKEN}>sltoken.txt
                    # shellcheck disable=SC2016
                    echo  '{
                            "executionType": "testsonly",
                            "tokenFile": "./sltoken.txt",
                            "createBuildSessionId": false,
                            "testStage": "Cucmber framework java ",
                            "runFunctionalTests": true,
                            "labId": "${params.SL_LABID}",
                            "proxy": null,
                            "logEnabled": false,
                            "logDestination": "console",
                            "logLevel": "warn",
                            "sealightsJvmParams": {}
                            }' > slmaventests.json
                    echo "Adding Sealights to Tests Project POM file..."
                    java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .

                    unset MAVEN_CONFIG
                    ./mvnw test
                    """

        }
      }
    }



    stage('Junit support testNG framework'){
      steps{
        script{
          sh """
                    #!/bin/bash
                    echo 'Junit support testNG framework starting ..... '
                    pwd
                    ls
                    cd ./integration-tests/support-testNG
                    export SL_TOKEN="${params.SL_TOKEN}"
                    echo $SL_TOKEN>sltoken.txt
                    export machine_dns="${params.MACHINE_DNS}"
                    # shellcheck disable=SC2016
                    echo  '{
                            "executionType": "testsonly",
                            "tokenFile": "./sltoken.txt",
                            "createBuildSessionId": false,
                            "testStage": "Junit support testNG",
                            "runFunctionalTests": true,
                            "labId": "${params.SL_LABID}",
                            "proxy": null,
                            "logEnabled": false,
                            "logDestination": "console",
                            "logLevel": "warn",
                            "sealightsJvmParams": {}
                            }' > slmaventests.json
                    echo "Adding Sealights to Tests Project POM file..."
                    java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .
                    mvn clean package
                    """
        }
      }
    }


    stage('Junit without testNG '){
      steps{
        script{
          sh """
                    #!/bin/bash
                    echo 'Junit without testNG framework starting ..... '
                    pwd
                    ls
                    cd integration-tests/java-tests
                    export SL_TOKEN="${params.SL_TOKEN}"
                    echo $SL_TOKEN>sltoken.txt
                    export machine_dns="${params.MACHINE_DNS}"
                    # shellcheck disable=SC2016
                    echo  '{
                            "executionType": "testsonly",
                            "tokenFile": "./sltoken.txt",
                            "createBuildSessionId": false,
                            "testStage": "Junit without testNG",
                            "runFunctionalTests": true,
                            "labId": "${params.SL_LABID}",
                            "proxy": null,
                            "logEnabled": false,
                            "logDestination": "console",
                            "logLevel": "warn",
                            "sealightsJvmParams": {}
                            }' > slmaventests.json
                    echo "Adding Sealights to Tests Project POM file..."
                    java -jar /sealights/sl-build-scanner.jar -pom -configfile slmaventests.json -workspacepath .

                    mvn clean package
                    """
        }
      }
    }


    stage('Postman framework'){
      steps{
        script{
          sh """
                    echo 'Postman framework starting ..... '
                    export MACHINE_DNS="${params.MACHINE_DNS}"
                    cd ./integration-tests/postman-tests/
                    cp -r /nodeModules/node_modules .
                    npm i slnodejs
                    npm install newman
                    npm install newman-reporter-xunit
                    ./node_modules/.bin/slnodejs start --labid ${params.SL_LABID} --token ${params.SL_TOKEN} --teststage "postman tests"
                    npx newman run sealights-excersise.postman_collection.json --env-var machine_dns="${params.MACHINE_DNS}" -r xunit --reporter-xunit-export './result.xml' --suppress-exit-code
                    ./node_modules/.bin/slnodejs uploadReports --labid ${params.SL_LABID} --token ${params.SL_TOKEN} --reportFile './result.xml'
                    ./node_modules/.bin/slnodejs end --labid ${params.SL_LABID} --token ${params.SL_TOKEN}
                    cd ../..
                    """
        }
      }
    }


    stage('Jest framework'){
      steps{
        script{

          sh """
                echo 'Jest framework starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                cd ./integration-tests/nodejs-tests/Jest
                cp -r /nodeModules/node_modules .
                npm i jest-cli
                export NODE_DEBUG=sl
                export SL_TOKEN="eyJhbGciOiJSUzUxMiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJodHRwczovL0RFVi1pbnRlZ3JhdGlvbi5hdXRoLnNlYWxpZ2h0cy5pby8iLCJqd3RpZCI6IkRFVi1pbnRlZ3JhdGlvbixuZWVkVG9SZW1vdmUsQVBJR1ctYzNiM2IyY2YtYjA1Yy00ZWM2LThjNjYtZTBmZTJiYzIwNzAzLDE2OTI4Nzc3MDM4ODUiLCJzdWJqZWN0IjoiU2VhTGlnaHRzQGFnZW50IiwiYXVkaWVuY2UiOlsiYWdlbnRzIl0sIngtc2wtcm9sZSI6ImFnZW50IiwieC1zbC1zZXJ2ZXIiOiJodHRwczovL2Rldi1pbnRlZ3JhdGlvbi5kZXYuc2VhbGlnaHRzLmNvL2FwaSIsInNsX2ltcGVyX3N1YmplY3QiOiIiLCJpYXQiOjE2OTI4Nzc3MDN9.dORXtjiTVw9vM3u2eO9l2r3f54NwEFPWVnhZnOWqV4_ZA-q2T86X861S6o4G7M371hMnoePRNoWgkjXp9isgEPEHoG_LQ_pvwc66vi5gBy8okjlypKGMTrz-N8bF1LeswguuSDDPIpm0Qq7KSjcm-GZmtO2IhJu4Q6f-tX0otMvvr6_nuwfVReExsT0Mxoyu0ZFs2HHwuIqhu12v1wNUuiTNIxQnGqckLw1qrroTG-qrDa8ydC111ML9C-u4qdS6G0iDsSdrQk9RETe0b1ow1vMXMFZeQ0vBrJDFjMnaCUhU6iid8xjkZG3T6XAI0k5SBRN8R6dtTO45mE638ohJi1_YBQL8hSkHL-8X_QkbRCH6IFqPcku0Wu2AcaRkBKOoiYAowFxnrQgYx5n_FVuTXNwW-s18Gnebd-bTBveCAHQH6CEbnpznXyMNXc15tOVdfp1n3RHLx9YE2lYI3dsTdwUlwNhto4J1Ym3ZOrLW_GZwLzZyIITfmNUOQVspwzsVOioeA48DZNpZhpZUAK5P19v0KY_iyJKxGajWnAUkXbyqc72d7eG5cUsIgv-r_p7fwnO4Rm1FVaZJ4Cpv7b4yf5YHGJ7BADI5Zw6YXuWQ3d9snZfvKOR50KVZGOykqwExYEwBACpN1WSEoIg8No7wTry_xNPmkTYOHbNoWuzyjTo"
                export SL_LABID="integ_ahmadbranch_d842_BTQ@ahmad-branch-1-0-267"
                npm install
                npx jest integration-tests/nodejs-tests/Jest/test.js --sl-testStage='Jest tests' --sl-token="${params.SL_TOKEN}" --sl-labId="${params.SL_LABID}"
                cd ../..
                """
        }
      }
    }



    stage('Mocha framework'){
      steps{
        script{
          sh """
                    echo 'Mocha framework starting ..... '
                    export machine_dns="${params.MACHINE_DNS}"
                    cd ./integration-tests/nodejs-tests/mocha
                    cp -r /nodeModules/node_modules .
                    npm install
                    npm install slnodejs
                    ./node_modules/.bin/slnodejs mocha --token "${params.SL_TOKEN}" --labid "${params.SL_LABID}" --teststage 'Mocha tests'  --useslnode2 -- ./test/test.js --recursive --no-timeouts
                    cd ../..
                    """
        }
      }
    }



    stage('Soap-UI framework'){
      steps{
        script{
          sh """
            echo 'Soap-UI framework starting ..... '
            wget https://dl.eviware.com/soapuios/5.7.1/SoapUI-5.7.1-mac-bin.zip
            unzip SoapUI-5.7.1-mac-bin.zip
            cp integration-tests/soapUI/test-soapui-project.xml SoapUI-5.7.1/bin
            cd SoapUI-5.7.1/bin
            echo 'Downloading Sealights Agents...'
            wget -nv https://agents.sealights.co/sealights-java/sealights-java-latest.zip
            unzip -o sealights-java-latest.zip
            echo "Sealights agent version used is:" `cat sealights-java-version.txt`
            export SL_TOKEN="${params.SL_TOKEN}"
            echo ${params.SL_TOKEN}>sltoken.txt
            echo  '{
              "executionType": "testsonly",
              "tokenFile": "./sltoken.txt",
              "createBuildSessionId": false,
              "testStage": "Soap-UI framework",
              "runFunctionalTests": true,
              "labId": "${params.SL_LABID}",
              "proxy": null,
              "logEnabled": false,
              "logDestination": "console",
              "logLevel": "warn",
              "sealightsJvmParams": {}
              }' > slmaventests.json
            echo "Adding Sealights to Tests Project POM file..."
            pwd
            sed -i "s#machine_dns#${params.MACHINE_DNS}#" test-soapui-project.xml
            sed "s#machine_dns#${params.MACHINE_DNS}#" test-soapui-project.xml
            export SL_JAVA_OPTS="-javaagent:sl-test-listener.jar -Dsl.token=${params.SL_TOKEN} -Dsl.labId=${params.SL_LABID} -Dsl.testStage=Soapui-Tests -Dsl.log.enabled=true -Dsl.log.level=debug -Dsl.log.toConsole=true"
            sed -i -r "s/(^\\S*java)(.*com.eviware.soapui.tools.SoapUITestCaseRunner)/\\1 \\\$SL_JAVA_OPTS \\2/g" testrunner.sh
            sh -x ./testrunner.sh -s "TestSuite 1" "test-soapui-project.xml"
            """
        }
      }
    }




    stage('Pytest framework'){
      steps{
        script{
          sh"""
                echo 'Pytest tests starting ..... '
                export machine_dns="${params.MACHINE_DNS}"
                cd ./integration-tests/python-tests
                pip install pytest
                pip install requests
                sl-python pytest --teststage "Pytest tests"  --labid ${params.SL_LABID} --token ${params.SL_TOKEN} python-tests.py
                cd ../..
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


  def template_path = (params.template_path == null) ? "microservices-demo/jenkins/pod-templates/shell_pod.yaml" : params.template_path
  def pod_template = new File("${template_path}").collect{it}

  def bindings = [params: params]
  def engine = new groovy.text.GStringTemplateEngine()
  pod_template = engine.createTemplate(pod_template).make(bindings).toString()

  return pod_template
}
