#!/usr/bin/env groovy

import java.text.SimpleDateFormat

def buildNumber = BUILD_NUMBER
def buildDateStr = new SimpleDateFormat("yyyy-MM-dd-HH-mm").format(new Date())
def buildTag = "${buildDateStr}-${buildNumber}"

println "\n Job name: ${JOB_NAME}\n Build date: ${buildDateStr}\n Build number: ${buildNumber}\n Build tag: ${buildTag}"

pipeline {
    agent {
        label "ods-dev"
    }

    environment {
        // Creates ODS_DOCKER_USR, ODS_DOCKER_PSW implicitly
		ODS_DOCKER = credentials('ods-docker')
	}

    stages {
        stage('Commit Stage') {
            steps {
                sh "docker login -u $ODS_DOCKER_USR -p $ODS_DOCKER_PSW https://mojo-docker.cs.fau.de"
                sh "./gradlew clean build -x test"
            }
        }

        stage('Acceptance Stage: Unit Tests') {
            steps {
                sh "./gradlew test"
            }
        }

        stage('Acceptance Stage: Build Docker Image') {
            steps {
                sh "./gradlew dockerBuild"
            }
        }

        stage('Acceptance Stage') {
            steps {
                parallel(
					'Run ODS Container': {
						sh "docker-compose -f docker/docker-compose.yml -f docker/docker-compose.local.yml up -d"
					},
					'Run Integration Tests': {
						sh "./gradlew integrationTest"
					}
                )
            }
        }

        stage('Release Stage: Push Docker Image') {
            steps {
                sh "./gradlew dockerPush"
            }
        }

    }

    post {
        always {
			junit '*/build/test-results/**/*.xml'

			sh "docker-compose logs -f docker/docker-compose.yml -f docker/docker-compose.local.yml ods > integration-test-ods.log"
			archive 'integration-test-ods.log'

			sh "docker-compose logs -f docker/docker-compose.yml -f docker/docker-compose.local.yml couchdb > integration-test-couchdb.log"
			archive 'integration-test-couchdb.log'

			sh "docker-compose -f docker/docker-compose.yml -f docker/docker-compose.local.yml stop"
			deleteDir()
        }
    }
}

