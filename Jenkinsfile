pipeline {
  agent any
  environment {
    AWS_ACCOUNT_ID = '203520860987'
    AWS_REGION = 'us-east-1'
    ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/java_dev"
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    EKS_CLUSTER = 'java_dev'
    K8S_NAMESPACE = 'default'
  }
  stages {
    stage('Checkout') {
      steps { checkout scm }
    }
    stage('Build jar') {
      steps {
        sh 'mvn -B -DskipTests clean package'
      }
    }
    stage('Docker build') {
      steps {
        sh "docker build -t employee:${IMAGE_TAG} ."
      }
    }
    stage('Login to ECR') {
      steps {
        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com"
      }
    }
    stage('Tag & Push') {
      steps {
        sh """
          docker tag employee:${IMAGE_TAG} ${ECR_REPO}:${IMAGE_TAG}
          docker push ${ECR_REPO}:${IMAGE_TAG}
        """
      }
    }
    stage('Deploy to EKS') {
      steps {
        sh """
          aws eks update-kubeconfig --name ${EKS_CLUSTER} --region ${AWS_REGION}
          # Option A: apply new manifest that references the image tag
          kubectl set image deployment/employee-deployment employee-container=${ECR_REPO}:${IMAGE_TAG} -n ${K8S_NAMESPACE} || \
            kubectl apply -f k8s/deployment.yaml -n ${K8S_NAMESPACE}
        """
      }
    }
  }
}
