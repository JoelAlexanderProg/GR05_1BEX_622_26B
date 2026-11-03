# Imagen de Jenkins del Grupo 5: la oficial con los plugins y la configuración ya incluidos.
# Al iniciar el contenedor, Jenkins ya tiene el usuario administrador y el pipeline del proyecto.
FROM jenkins/jenkins:lts-jdk21

# Sin asistente de instalación: la configuración viene de casc.yaml (Configuration as Code).
# Las fechas de las ejecuciones y de los commits se muestran con la hora de Ecuador.
ENV TZ=America/Guayaquil
ENV JAVA_OPTS="-Djenkins.install.runSetupWizard=false -Duser.timezone=America/Guayaquil"
ENV CASC_JENKINS_CONFIG=/usr/share/jenkins/casc.yaml

COPY plugins.txt /usr/share/jenkins/ref/plugins.txt
RUN jenkins-plugin-cli --plugin-file /usr/share/jenkins/ref/plugins.txt

COPY casc.yaml /usr/share/jenkins/casc.yaml
