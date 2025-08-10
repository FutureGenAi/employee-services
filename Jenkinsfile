pipeline {
  agent any
  environment {
    AWS_ACCOUNT_ID = '203520860987'
    AWS_REGION = 'us-east-1'
    ECR_REPO = "${AWS_ACCOUNT_ID}.dkr.ecr.${AWS_REGION}.amazonaws.com/java_dev"
    IMAGE_TAG = "${env.BUILD_NUMBER}"
    EKS_CLUSTER = 'java_dev'
    K8S_NAMESPACE = 'default'
    KUBECONFIG = '/var/lib/jenkins/.kube/config'
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
        sh "aws ecr get-login-password --region ${AWS_REGION} | docker login --username AWS --password-stdin ${ECR_REPO}"
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
          mkdir -p \$(dirname ${KUBECONFIG})
          aws eks update-kubeconfig --name ${EKS_CLUSTER} --region ${AWS_REGION} --kubeconfig ${KUBECONFIG}

          echo "Verifying AWS identity..."
          aws sts get-caller-identity

          echo "Testing cluster access..."
          kubectl get nodes --kubeconfig=${KUBECONFIG}

          echo "Deploying image ${ECR_REPO}:${IMAGE_TAG} ..."
          kubectl set image deployment/employee employee=${ECR_REPO}:${IMAGE_TAG} -n ${K8S_NAMESPACE} --kubeconfig=${KUBECONFIG} || \
          kubectl apply -f kubernetes/deployment.yaml -n ${K8S_NAMESPACE} --kubeconfig=${KUBECONFIG}
        """
      }
    }
  }
}
