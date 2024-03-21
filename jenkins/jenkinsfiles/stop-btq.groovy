pipeline {
   agent {
        kubernetes {
            defaultContainer 'shell'
            yaml """
                apiVersion: v1
                kind: Pod
                metadata:
                  labels:
                    some-label: some-value
                spec:
                  containers:
                  - name: shell
                    image: "public.ecr.aws/a2q7i5i2/sl-jenkins-base-ci:latest"
                    command:
                    - cat
                    tty: true
            """
        }
    }
    environment {
        IDENTIFIER = '54.246.240.122'
    }
     stages {
        stage("Uninstalling helm") {
            steps {
                script {
                        sh script: """
                            aws secretsmanager get-secret-value --region eu-west-1 --secret-id 'jkns-key_pair' | jq -r '.SecretString' | jq -r '.jkns_key_pair' > key.pem
                            chmod 0400 key.pem
                            
                            ssh -o StrictHostKeyChecking=no -i key.pem ec2-user@54.246.240.122 'export KUBECONFIG=\$(k3d kubeconfig write btq) && helm uninstall btq'
                        """
                }
            }
        }
    }
}