// Pipeline de integración continua de EncuentraEPN.
// Cada vez que hay commits nuevos en main, Jenkins descarga el código, lo compila,
// ejecuta las pruebas unitarias y genera el WAR de la aplicación.
pipeline {
    agent any

    triggers {
        // Revisa el repositorio cada 2 minutos y construye si encuentra commits nuevos
        pollSCM('H/2 * * * *')
    }

    options {
        timeout(time: 15, unit: 'MINUTES')
    }

    stages {
        stage('Compilación') {
            steps {
                sh 'chmod +x mvnw'
                sh './mvnw -B clean compile'
            }
        }
        stage('Pruebas unitarias') {
            steps {
                sh './mvnw -B test'
            }
            post {
                always {
                    junit 'target/surefire-reports/*.xml'
                }
            }
        }
        stage('Empaquetado') {
            steps {
                sh './mvnw -B -DskipTests package'
                archiveArtifacts artifacts: 'target/*.war', fingerprint: true
            }
        }
    }

    post {
        success {
            echo 'Integración exitosa: el código compila, las pruebas pasan y el WAR está listo.'
        }
        failure {
            echo 'La integración falló: revise la etapa en rojo.'
        }
    }
}
