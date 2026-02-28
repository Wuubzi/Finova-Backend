pipeline {
    agent any

    environment {
        PROJECT_NAME = 'Finova-Backend'
    }

    stages {


        stage('Checkout') {
            steps {
                checkout scm
                sh 'chmod +x gradlew'
                echo "🔀 Rama: ${env.BRANCH_NAME}"
            }
        }

        stage('Build') {
            steps {
                echo "🔨 Compilando todos los microservicios..."
                sh './gradlew clean build -x test'
            }
        }

        stage('Unit Tests') {
            steps {
                echo "🧪 Corriendo tests unitarios..."
                sh './gradlew test'
            }
            post {
                always {
                    junit '**/build/test-results/test/*.xml'
                }
            }
        }

     stage('SonarQube Analysis') {
         steps {
             echo "📊 Analizando calidad del código..."
             withSonarQubeEnv('SonarQube') {
                 sh """
                     ${tool 'SonarScanner'}/bin/sonar-scanner \
                     -Dsonar.projectKey=finova-backend \
                     -Dsonar.projectName=Finova-Backend \
                     -Dsonar.sources=. \
                     -Dsonar.java.binaries=**/build/classes
                 """
             }
         }
     }

     stage('Quality Gate') {
         steps {
             timeout(time: 5, unit: 'MINUTES') {
                 waitForQualityGate abortPipeline: true
             }
         }
     }

    }

    post {
        success {
            echo "✅ Pipeline exitoso en ${env.BRANCH_NAME} - PR listo para mergear"
        }
        failure {
            echo "❌ Pipeline fallido en ${env.BRANCH_NAME} - PR bloqueado"
        }
        always {
            cleanWs()
        }
    }
}
