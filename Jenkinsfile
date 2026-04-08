pipeline {
    agent any

    environment {
        PROJECT_NAME       = 'Finova-Backend'
        TESTCONTAINERS_RYUK_DISABLED   = 'true'
        TESTCONTAINERS_CHECKS_DISABLE  = 'true'
        DOCKER_HOST        = 'unix:///var/run/docker.sock'

        // ====== CD Configuration ======
        AWS_REGION         = 'us-east-1'
        ECR_REGISTRY       = credentials('ecr-registry-url')
        EC2_HOST           = credentials('ec2-host')
        EC2_USER           = 'ec2-user'
        IMAGE_TAG          = "${env.BRANCH_NAME}-${env.BUILD_NUMBER}"
        DEPLOY_DIR         = '/home/ec2-user/finova'

        MICROSERVICES      = 'eureka,config,gateway,auth,user,account,transaction,notification'
    }

    stages {
        // ==================== CI ====================
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
                sh '''
                    # Verificar que Docker esté disponible
                    docker --version || exit 1

                    # Ejecutar tests
                    ./gradlew test jacocoTestReport
                '''
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
                        -Dsonar.java.binaries=**/build/classes \
                        -Dsonar.coverage.jacoco.xmlReportPaths=**/build/reports/jacoco/test/jacocoTestReport.xml
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

        // ==================== CD (solo en main) ====================
        stage('Install AWS CLI') {
            when {
                branch 'main'
            }
            steps {
                echo "🔧 Instalando AWS CLI..."
                sh '''
                    if ! command -v aws &> /dev/null; then
                        echo "AWS CLI no encontrada, instalando..."
                        curl -s "https://awscli.amazonaws.com/awscli-exe-linux-x86_64.zip" -o "awscliv2.zip"
                        unzip -qo awscliv2.zip
                        ./aws/install --update || sudo ./aws/install --update
                        rm -rf awscliv2.zip aws
                    fi
                    aws --version
                '''
            }
        }

        stage('Docker Build & Push to ECR') {
            when {
                branch 'main'
            }
            steps {
                echo "🐳 Construyendo imágenes Docker y subiendo a ECR..."
                withCredentials([[$class: 'AmazonWebServicesCredentialsBinding', credentialsId: 'aws-credentials']]) {
                    sh '''
                        aws ecr get-login-password --region $AWS_REGION | \
                            docker login --username AWS --password-stdin $ECR_REGISTRY
                    '''

                    script {
                        def services = MICROSERVICES.split(',')
                        for (svc in services) {
                            sh """
                                echo "📦 Building ${svc}-service..."

                                aws ecr describe-repositories --repository-names finova/${svc}-service --region \$AWS_REGION 2>/dev/null || \
                                    aws ecr create-repository --repository-name finova/${svc}-service --region \$AWS_REGION

                                docker build -t \$ECR_REGISTRY/finova/${svc}-service:\$IMAGE_TAG \
                                             -t \$ECR_REGISTRY/finova/${svc}-service:latest ./${svc}

                                docker push \$ECR_REGISTRY/finova/${svc}-service:\$IMAGE_TAG
                                docker push \$ECR_REGISTRY/finova/${svc}-service:latest

                                echo "✅ ${svc}-service pushed successfully"
                            """
                        }
                    }
                }
            }
        }

        stage('Deploy to EC2') {
            when {
                branch 'main'
            }
            options {
                timeout(time: 15, unit: 'MINUTES')
            }
            steps {
                echo "🚀 Desplegando en EC2..."
                withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    sh '''
                        chmod 600 $SSH_KEY

                        scp -o StrictHostKeyChecking=no -i $SSH_KEY docker-compose.yml $EC2_USER@$EC2_HOST:$DEPLOY_DIR/docker-compose.yml

                        ssh -o StrictHostKeyChecking=no -i $SSH_KEY $EC2_USER@$EC2_HOST bash -s <<DEPLOY_SCRIPT
                            set -e
                            cd $DEPLOY_DIR

                            echo "🔑 Login a ECR..."
                            aws ecr get-login-password --region $AWS_REGION | \
                                docker login --username AWS --password-stdin $ECR_REGISTRY

                            # Inyectar variables de ECR en el .env sin borrar las existentes
                            sed -i '/^ECR_REGISTRY=/d' .env 2>/dev/null || true
                            sed -i '/^IMAGE_TAG=/d' .env 2>/dev/null || true
                            echo "ECR_REGISTRY=$ECR_REGISTRY" >> .env
                            echo "IMAGE_TAG=$IMAGE_TAG" >> .env

                            echo "⬇️ Pulling nuevas imágenes..."
                            docker compose pull

                            echo "🔄 Desplegando..."
                            docker compose up -d auth-db user-db account-db transaction-db kafka redis
                            sleep 10

                            docker compose up -d eureka-service
                            sleep 15

                            docker compose up -d config-service
                            sleep 15

                            echo "🚀 Arrancando microservicios..."
                            docker compose up -d auth-service user-service account-service transaction-service notification-service
                            sleep 30

                            echo "🌐 Arrancando gateway..."
                            docker compose up -d gateway-service

                            echo "🧹 Limpiando imágenes antiguas..."
                            docker image prune -f

                            echo "📋 Estado de los servicios:"
                            docker compose ps
DEPLOY_SCRIPT
                    '''
                }
            }
        }

        stage('Health Check') {
            when {
                branch 'main'
            }
            options {
                timeout(time: 15, unit: 'MINUTES')
            }
            steps {
                echo "🏥 Verificando salud de los servicios..."
                withCredentials([sshUserPrivateKey(credentialsId: 'ec2-ssh-key', keyFileVariable: 'SSH_KEY', usernameVariable: 'SSH_USER')]) {
                    sh '''
                        ssh -o StrictHostKeyChecking=no -i $SSH_KEY $EC2_USER@$EC2_HOST bash -s <<'HEALTH_SCRIPT'
                            set -e

                            echo "⏳ Esperando 90s para que los servicios arranquen..."
                            sleep 90

                            MAX_RETRIES=20
                            RETRY_INTERVAL=15

                            check_service() {
                                local service_name=$1
                                local port=$2
                                for i in $(seq 1 $MAX_RETRIES); do
                                    HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost:${port}/actuator/health 2>/dev/null || echo "000")
                                    if [ "$HTTP_CODE" != "000" ]; then
                                        echo "✅ ${service_name} is running (HTTP ${HTTP_CODE})"
                                        return 0
                                    fi
                                    echo "⏳ Waiting for ${service_name}... ($i/$MAX_RETRIES)"
                                    sleep $RETRY_INTERVAL
                                done
                                echo "❌ ${service_name} failed health check!"
                                echo "📋 Logs de ${service_name}:"
                                cd /home/ec2-user/finova && docker compose logs --tail=50 ${service_name} 2>/dev/null || true
                                return 1
                            }

                            check_service "eureka-service"       8761
                            check_service "config-service"       8888
                            check_service "auth-service"         8081
                            check_service "user-service"         8082
                            check_service "account-service"      8083
                            check_service "transaction-service"  8084
                            check_service "notification-service" 8085
                            check_service "gateway-service"      8000

                            echo "🎉 Todos los servicios están saludables!"
HEALTH_SCRIPT
                    '''
                }
            }
        }
    }

    post {
        success {
            script {
                if (env.BRANCH_NAME == 'main') {
                    echo "✅ CI/CD completo - Desplegado en EC2 con tag: ${IMAGE_TAG}"
                } else {
                    echo "✅ Pipeline CI exitoso en ${env.BRANCH_NAME} - PR listo para mergear"
                }
            }
        }
        failure {
            script {
                if (env.BRANCH_NAME == 'main') {
                    echo "❌ Pipeline CD fallido - Revisar deployment"
                } else {
                    echo "❌ Pipeline CI fallido en ${env.BRANCH_NAME} - PR bloqueado"
                }
            }
        }
        always {
            cleanWs()
        }
    }
}