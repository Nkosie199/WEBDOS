@echo off
set "JAVA_HOME=C:\Program Files\Java\jdk-23"
set "PATH=%JAVA_HOME%\bin;%PATH%"
set "MVN=C:\Users\Administrator\.m2\wrapper\dists\apache-maven-3.8.6-bin\1ks0nkde5v1pk9vtc31i9d0lcd\apache-maven-3.8.6\bin\mvn.cmd"
cd /d "C:\Users\Administrator\Documents\CodingProjects\WEBDOS"
"%JAVA_HOME%\bin\java.exe" -version
"%MVN%" clean package -DskipTests
echo BUILD_EXIT=%ERRORLEVEL%
