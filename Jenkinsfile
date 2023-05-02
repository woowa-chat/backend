pipeline {
  agent any
  stages {
    stage('build and test') {
      steps {
        sh './gradlew clean build'
      }
    }

    stage('zip') {
      steps {
        sh 'mv ./build/libs/woowachat.jar .'
        sh 'zip -r tecochat.zip .platform tecochat.jar Procfile'
      }
    }

    stage('upload') {
      steps {
        sh 'aws s3 cp tecochat.zip s3://woowa-chat/tecochat.zip --region ap-northeast-2'
      }
    }

    stage('deploy') {
      steps {
        sh 'aws elasticbeanstalk create-application-version --region ap-northeast-2 --application-name woowachat --version-label ${BUILD_TAG} --source-bundle S3Bucket="woowa-chat",S3Key="tecochat.zip"'
        sh 'aws elasticbeanstalk update-environment --region ap-northeast-2 --environment-name Woowachat-env --version-label ${BUILD_TAG}'
      }
    }

  }
}
