@ECHO OFF

REM ==================================================
REM OSGiSERVER start script for Windows
REM Note: Assuming Windows is for development,
REM       JPDA debugging is on
REM ==================================================

REM ==================================================
REM Uncomment only one of the two lines below
REM ==================================================
REM SET ENV_NAME=production
SET ENV_NAME=development

SET JPDA_PORT=8888

SET OSGiSERVER_HOME=%~dp0
SET OSGiSERVER_HOME=%OSGiSERVER_HOME%\..

IF [%1]==[] GOTO default
IF [%1]==[/?] GOTO help
IF [%1]==[-?] GOTO help
IF [%1]==[/h] GOTO help
IF [%1]==[/H] GOTO help
IF [%1]==[-h] GOTO help
IF [%1]==[-H] GOTO help
SET JAVA_MEM_MB=%1
GOTO exec

:help
ECHO Usage:
ECHO     server_start.bat [memory in Mb]
ECHO     server_start.bat /?
GOTO end

:default
SET JAVA_MEM_MB=64

:exec
REM -XX:-UseGCOverheadLimit
SET OPTS_JVM=-server -Xms%JAVA_MEM_MB%m -Xmx%JAVA_MEM_MB%m -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -XX:+UseParNewGC -XX:+UseConcMarkSweepGC
SET OPTS_GC_LOG=-XX:PrintFLSStatistics=1 -XX:PrintCMSStatistics=1 -XX:+PrintTenuringDistribution -XX:+PrintGCDetails -XX:+PrintGCDateStamps -verbose:gc -Xloggc:%OSGiSERVER_HOME%\logs\garbage.log
SET OPTS_OSGiSERVER=-Dosgiserver.home=%OSGiSERVER_HOME% -Dosgiserver.osgi.properties=%OSGiSERVER_HOME%\bin\osgi-felix.properties
SET OPTS_CLASSPATH=-classpath "%OSGiSERVER_HOME%\lib;%OSGiSERVER_HOME%\lib\*"

java -Dspring.profiles.active=%ENV_NAME% -Dlogback.configurationFile=%OSGiSERVER_HOME%\bin\logback-%ENV_NAME%.xml -agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=%JPDA_PORT% %OPTS_JVM% %OPTS_GC_LOG% %OPTS_OSGiSERVER% %OPTS_CLASSPATH% com.github.ddth.osgiserver.bootstrap.StandaloneBootstrap

:end
