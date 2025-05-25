/home/sathvika/Downloads/sonar-scanner-cli-6.2.1.4610-linux-x64/sonar-scanner-6.2.1.4610-linux-x64/bin/sonar-scanner/mvn clean verify sonar:sonar \
  -Dsonar.projectKey=seproject \
  -Dsonar.projectName='seproject' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_a7e35319587b0393144676208f79ee4f03bcf7dc




/home/sathvika/Downloads/sonar-scanner-cli-6.2.1.4610-linux-x64/sonar-scanner-6.2.1.4610-linux-x64/bin/mvn clean verify sonar:sonar \
  -Dsonar.projectKey=newtry \
  -Dsonar.projectName='newtry' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_d2210911fea04be963807967fcedc01fc8b53c5a -->


mvn clean verify sonar:sonar \
  -Dsonar.projectKey=newtry \
  -Dsonar.projectName='newtry' \
  -Dsonar.host.url=http://localhost:9000 \
  -Dsonar.token=sqp_d2210911fea04be963807967fcedc01fc8b53c5a



$> docker run -d --name sonarqube \
    -p 9000:9000 \
    -e SONAR_JDBC_URL=... \
    -e SONAR_JDBC_USERNAME=... \
    -e SONAR_JDBC_PASSWORD=... \
    -v sonarqube_data:/opt/sonarqube/data \
    -v sonarqube_extensions:/opt/sonarqube/extensions \
    -v sonarqube_logs:/opt/sonarqube/logs \
    sonarqube












sathvika@sathvika-Latitude-3420:~$ docker run -d --name sonarqube -p 9000:9000 --link sonarqube-db:db -e SONAR_JDBC_URL=jdbc:postgresql://db:5432/sonarqube -e SONAR_JDBC_USERNAME=sonar -e SONAR_JDBC_PASSWORD=sonar sonarqube
docker: Error response from daemon: Conflict. The container name "/sonarqube" is already in use by container "fe0f2dd1b624d1827c0b47898725afbcb46525ad882c57d0aed28718f4cd2936". You have to remove (or rename) that container to be able to reuse that name.
See 'docker run --help'.
sathvika@sathvika-Latitude-3420:~$ docker run -d --name sonarqube -p 9000:9000 --link sonarqube-db:db -e SONAR_JDBC_URL=jdbc:postgresql://db:5432/sonarqube -e SONAR_JDBC_USERNAME=sonar -e SONAR_JDBC_PASSWORD=sonar sonarqube
docker: Error response from daemon: Conflict. The container name "/sonarqube" is already in use by container "fe0f2dd1b624d1827c0b47898725afbcb46525ad882c57d0aed28718f4cd2936". You have to remove (or rename) that container to be able to reuse that name.
See 'docker run --help'.
sathvika@sathvika-Latitude-3420:~$ docker rm -f sonarqube
sonarqube
sathvika@sathvika-Latitude-3420:~$ docker run -d --name sonarqube -p 9000:9000 --link sonarqube-db:db -e SONAR_JDBC_URL=jdbc:postgresql://db:5432/sonarqube -e SONAR_JDBC_USERNAME=sonar -e SONAR_JDBC_PASSWORD=sonar sonarqube
aaf453a88051060cfcdd720f1fc0b3854a386a8081803d5e65be3be6a2f2623a
sathvika@sathvika-Latitude-3420:~$ docker ps
CONTAINER ID   IMAGE             COMMAND                  CREATED         STATUS         PORTS                                       NAMES
aaf453a88051   sonarqube         "/opt/sonarqube/dock…"   2 minutes ago   Up 2 minutes   0.0.0.0:9000->9000/tcp, :::9000->9000/tcp   sonarqube
c5e2843d499c   postgres:alpine   "docker-entrypoint.s…"   8 minutes ago   Up 8 minutes   5432/tcp
