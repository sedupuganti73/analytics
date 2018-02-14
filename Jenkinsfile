pipeline {
    agent any
    triggers { pollSCM 'H/5 * * * *'  }
    environment {
     JAVA_HOME = "${tool 'jdk1.8.0_45'}"
     PATH = "${env.JAVA_HOME}/bin:${env.PATH}"
     crqNumber = ""
     ARTIFACTORY_UPLOAD_JOB = "cw-artifactory-upload-job"
     ARTIFACTORY_UPLOAD_JOB_NUMBER = ""
   }
    parameters { 
     string(defaultValue: 'https://github.bnsf.com/RS/rmtui.git', description: 'enter git url', name: 'gitURL')
     string(defaultValue: 'master', description: 'enter git brach', name: 'gitBranch')
     string(defaultValue: 'rmtui', description: 'app name used by chef and bluemix', name: 'app_name')
     string(defaultValue: 'dev', description: 'chef environment, skips chef deployment if empty', name: 'chef_environment')
     string(defaultValue: 'development', description: 'bluemix space, skips bluemix deployment if empty', name: 'bmx_environment')
     string(defaultValue: '81278', description: 'bluemix organization id, skips bluemix deployment if empty', name: 'bmx_org_id')
     string(defaultValue: 'rmt', description: 'bluemix host name, skips bluemix deployment if empty', name: 'bmx_host_name')
     string(defaultValue: '0', description: 'Min backend test coverage', name: 'backend_coverage')
     string(defaultValue: '0', description: 'Min ui test coverage, skips if empty or 0', name: 'ui_coverage')
     string(defaultValue: 'rahul.bhattacharya@bnsf.com', description: 'Email Addresses to notify', name: 'emails')
     string(defaultValue: 'c841456', description: 'List of approvars', name: 'approvars')
     string(defaultValue: '', description: 'Load balancer url, will be pinged to check if app deployed', name: 'load_balancer_url')
     string(defaultValue: '', description: 'Test job name, skips if empty', name: 'test_job')
     //string(defaultValue: '7', description: 'Wait time before executing test job ideally to handle chef client intervals', name: 'wait_before_test')
    }


    //deleteDir()
    stages {
        

        stage ('Checkout Source') {
            steps{
                echo "checking out ${params.gitURL}/${params.gitBranch} "
                git url: "${params.gitURL}", branch: "${params.gitBranch}", poll:true
                
            }
            
        }
    
        stage ('UI unit test and build') {
            steps {
                script {
                        
                            dir('frontend') {
                                withEnv(
                                    [   "COVERAGE_LIMIT=${params.ui_coverage}"
                                    ]) {
                                        sh "npm install"
                                        if(params.ui_coverage.trim()!='' && params.ui_coverage.trim().toInteger() > 0) {
                                            sh "npm install karma-phantomjs-launcher@1.0.4  --phantomjs_cdnurl=http://artifactory.bnsf.com:8081/artifactory/ext-release-local/org/phantomjs/phantomjs/2.1.1"
                                            sh "node_modules/@angular/cli/bin/ng test --single-run --code-coverage --browser PhantomJS"
                                            publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'coverage', reportFiles: 'index.html', reportName: 'Jasmine Report'])
                                        }
                                        if(params.chef_environment.trim()=='dev') {
                                             sh "npm run buildDev"
                                        } else {
                                            sh "npm run build"
                                        }
                                       
                                    }
                                
                            }
                        }
                }
   
            
        }
    
        stage ('Backend unit test and build') {
            steps {
                 
                    sh 'chmod +x gradlew'
                    sh "./gradlew clean build  jacocoTestReport createTar --full-stacktrace -Pcoverage=${params.backend_coverage}"    
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/findbugs', reportFiles: 'main.html', reportName: 'FindBugs Report'])
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/tests', reportFiles: 'index.html', reportName: 'jUnit Report'])
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/jacoco/test/html', reportFiles: 'index.html', reportName: 'JaCoCo Report'])
                    publishHTML([allowMissing: true, alwaysLinkToLastBuild: false, keepAll: true, reportDir: 'build/reports/checkstyle', reportFiles: 'main.html', reportName: 'Checkstyle Report'])
                    dir('./build/libs') {
                        archiveArtifacts artifacts: "*.tar", fingerprint: true
                        script{
                            def deploymentTar = findFiles(glob: '**/*.tar')
                            echo "Tar file: " + deploymentTar[0].name
                        }
                    }
                                  
            }
        }
        stage("Run Fortify scan") {
            steps {
                withEnv(
                        [   "PROMOTED_GIT_URL=${params.gitURL}",
                            //"PROMOTED_GIT_COMMIT=${GIT_COMMIT}",
                            "PROMOTED_JOB_NAME=${env.JOB_NAME}","PROMOTED_NUMBER=${env.BUILD_NUMBER}"
                        ]) {
                            wrap(
                                [   $class: 'ConfigFileBuildWrapper',
                                    managedFiles: [
                                            [   fileId: 'org.jenkinsci.plugins.managedscripts.ScriptConfig1469561066300',
                                                replaceTokens: false,
                                                targetLocation: './submit_fortify.sh',
                                                variable: 'SUBMIT_FORTIFY'
                                            ]
                                    ]
                                ]) {
                                    sh "chmod +x ${SUBMIT_FORTIFY}"
                                    sh "${SUBMIT_FORTIFY}"
                                }
                        }
            }
        }
        stage("Upload to Artifactory") {
            steps {
                script{
                    def uploadJob = build job: "${env.ARTIFACTORY_UPLOAD_JOB}",
                        parameters: [
                                [$class: 'StringParameterValue', name: 'parent_job_name', value: "${env.JOB_NAME}"],
                                [$class: 'StringParameterValue', name: 'parent_build_number', value: "${env.BUILD_NUMBER}"],
                                //[$class: 'StringParameterValue', name: 'ci_build_number', value: appBuildNumber]
                        ]
                    echo "Upload job ID:" + uploadJob.getNumber()
                    def uploadJobNumber = uploadJob.getNumber()
                    sh "echo ${uploadJobNumber} > job_number.txt"
                    
                }
                
            }
        }
    
        stage("Update Chef Databag") {
            steps {
                echo "Deploying to ${params.chef_environment}"
                script {
                    if(params.app_name.trim()!='' && params.chef_environment.trim()!='') {
                        try{
                            def uploadJobNumber  = readFile('job_number.txt').trim()
                            echo "this is the upload job number ${uploadJobNumber} trimmed"
                            // Set data bag item so that chef-client will pick latest URL
                            sh "/opt/collabnet/bnsf/apps/integrations/chefDeployment/ChefDeployment.sh Jenkins ${params.app_name} ${params.chef_environment}   \"/var/lib/jenkins/jobs/${env.ARTIFACTORY_UPLOAD_JOB}/builds/${uploadJobNumber}\""
                        } catch(err) {
                            println("${err.message}")
                            println("sending error email to ${params.emails}")
                            emailext subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - FAILURE!", to: "${params.emails}",body: "${err.message}"
                            //throw err; // rethrow so the build is considered failed 
                        } 
                    } 
                }     
            }
        }

        stage("Deploy to Bluemix") {
            steps {
                echo "Deploying to ${params.chef_environment}"
                script {
                    if(params.app_name.trim()!='' && params.bmx_environment.trim()!='') {
                        try{
                            def uploadJobNumber  = readFile('job_number.txt').trim()
                            echo "this is the upload job number ${uploadJobNumber} trimmed"
                            // Set data bag item so that chef-client will pick latest URL
                            sh "/opt/collabnet/bnsf/apps/integrations/chefDeployment/BNSFDeployment.sh BlueMix ${params.app_name} ${params.bmx_environment}  \"/var/lib/jenkins/jobs/${env.ARTIFACTORY_UPLOAD_JOB}/builds/${uploadJobNumber}\"  ${params.bmx_org_id} java ${params.bmx_host_name}"
                        } catch(err) {
                            println("${err.message}")
                            println("sending error email to ${params.emails}")
                            emailext subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - FAILURE!", to: "${params.emails}",body: "${err.message}"
                            //throw err; // rethrow so the build is considered failed 
                        } 
                    } 
                }     
            }
        }

        stage ('Check app version deployed on VIP') {
            agent none 
            steps {
                echo 'Checking app deployed'
                
                        dir('./build/libs') {
                                archiveArtifacts artifacts: "*.jar", fingerprint: true
                                script{
                                    if(params.load_balancer_url.trim()!='') {
                                            def deploymentJar = findFiles(glob: '**/*.jar')
                                            echo "Jar file: " + deploymentJar[0].name
                                            
                                            def versionNumberToBeDeployed = "0";
                                            def versionNumberActuallyDeployed = "1";
                                            timeout(time: 20, unit: 'MINUTES') {
                                                    while(versionNumberToBeDeployed != versionNumberActuallyDeployed) {
                                                        sleep time: 1, unit: 'MINUTES'
                                                        versionNumberToBeDeployed = deploymentJar[0].name.substring(6, deploymentJar[0].name.indexOf('.jar'))
                                                        sh "curl -k -s ${params.load_balancer_url}/info > output.txt"
                                                        sh "cat output.txt"
                                                        versionNumberActuallyDeployed = sh (
                                                                                        script: "cat output.txt | grep -o -P '(?<=\"version\":\")(.*)(?=\",\"artifact)'",
                                                                                        returnStdout: true
                                                                                    ).trim();
                                                        echo "versionNumberToBeDeployed: " + versionNumberToBeDeployed  
                                                        echo "versionNumberActuallyDeployed: " + versionNumberActuallyDeployed                           
                                                    }
                                            }
                                    }

                                    
                                }
                        }
                
                
            }
            
        }


        stage ('Trigger e2e Test') {
            agent none 
            steps {
                echo 'triggering smart bear'
                script {
                        if(params.test_job.trim()!='') {
                            //sleep time: params.wait_before_test.toInteger(), unit: 'MINUTES'
                            //crete a test job look at customer-web-smartbear-test for sample
                            def testJob = build job: params.test_job, wait: true
                            echo "triggered job ${testJob.getNumber()} click on ${testJob.getUrl()} to view results."
                        } else {
                            echo "skipping as test job is not set"
                        }
                }
                
                
            }
            
        }

        
    
       /* stage("Push to Prod:Enter CRQ") {
            agent none 
            steps {
                script{
                    emailext subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} waiting for prod approval!", to: "${params.emails}",body: "Build is waiting for your approval to deploy to prod."    
                    timeout(time: 2, unit: 'DAYS') {
                        // Parameters must be outside of node block to prevent unnecessary locking of nodes
                        def crqNumber = input id: 'crqInput',
                                message: "BNSF Continuous Deployment: Please enter CRQ number for production deployment",
                                submitterParameter: 'approval',
                                ok: 'Deploy to production',
                                parameters: [
                                        [
                                                $class      : 'StringParameterDefinition',
                                                defaultValue: "CRQ000000000000",
                                                name        : 'CRQ Number'
                                        ]
                                ]
                        
                        
                        if(!"${params.approvars}".contains("${crqNumber.approval}")) {
                            error("Build failed because ${crqNumber.approval} is not an approver")
                        } else if(!crqNumber['CRQ Number']) {
                            error "CRQ number is mandatory for production deployment. Deployment cannot continue without it!"
                        } else {
                            try{
                                def uploadJobNumber  = readFile('job_number.txt').trim()
                                echo "this is the upload job number ${uploadJobNumber} trimmed"
                                def CRQ_NUMBER = crqNumber['CRQ Number'];
                                // Set data bag item so that chef-client will pick latest URL
                                //sh "/opt/collabnet/bnsf/apps/integrations/chefDeployment/BNSFDeployment.sh  Chef  rmtui development \"/var/lib/jenkins/jobs/${env.JOB_NAME}/builds/${env.BUILD_NUMBER}\""
                                sh "/opt/collabnet/bnsf/apps/integrations/chefDeployment/ChefDeployment.sh Jenkins rmtui trial \"/var/lib/jenkins/jobs/${env.ARTIFACTORY_UPLOAD_JOB}/builds/${uploadJobNumber}\" ${CRQ_NUMBER}"
                            } catch(err) {
                                println("${err.message}")
                                println("sending error email to ${params.emails}")
                                emailext subject: "${env.JOB_NAME} - Build # ${env.BUILD_NUMBER} - FAILURE!", to: "${params.emails}",body: "${err.message}"
                                //throw err; // rethrow so the build is considered failed 
                            }
                        }
                        
                    }
                }
            }
        }*/
    }
    
   

}
