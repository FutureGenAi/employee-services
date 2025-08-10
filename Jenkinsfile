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
        withEnv([
          "AWS_ACCESS_KEY_ID=${env.AWS_ACCESS_KEY_ID}",
          "AWS_SECRET_ACCESS_KEY=${env.AWS_SECRET_ACCESS_KEY}",
          "AWS_SESSION_TOKEN=${env.AWS_SESSION_TOKEN}" // only if using temporary creds
        ]) {
          sh """
            mkdir -p $(dirname $KUBECONFIG)

            # Update kubeconfig
            aws eks update-kubeconfig \
              --name ${EKS_CLUSTER} \
              --region ${AWS_REGION} \
              --kubeconfig $KUBECONFIG

            # Optional sanity check
            aws sts get-caller-identity
            kubectl version --short

            # Update image or fallback to apply YAML
            kubectl set image deployment/employee-deployment \
              employee-container=${ECR_REPO}:${IMAGE_TAG} \
              -n ${K8S_NAMESPACE} || \
            kubectl apply -f kubernetes/deployment.yaml -n ${K8S_NAMESPACE}
          """
        }
      }
    }
  }
}
