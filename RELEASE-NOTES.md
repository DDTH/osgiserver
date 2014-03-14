OSGi Server release notes
=========================

OSGi Server With Start/Stop Scripts

2.0.0 - 2014-03-14
------------------

**General**

- Use [logback](http://logback.qos.ch/) instead of [log4j](http://logging.apache.org/log4j/).
- Configuration fix: temp folder is now at `${osgiserver.home}/tmp`; and its content is cleanup everytime OSGiServer starts.
- Replace Apache `commons-pool` by `commons-pool2`.
- Add `Jackson-2.3.2` and `Spring Framework-3.2.4-RELEASE` libraries.
- Bundles/Libraries review & update

**00.logging**

- jcl-over-slf4j-1.7.6.jar
- jul-to-slf4j-1.7.6.jar
- log4j-over-slf4j-1.7.6.jar
- osgi-over-slf4j-1.7.6.jar

**01.apachecommons**

- com.springsource.org.apache.commons.logging-1.1.1.jar
- commons-codec-1.9.jar
- commons-collections-3.2.1.jar
- commons-email-1.3.2.jar
- commons-fileupload-1.3.1.jar
- commons-io-2.4.jar
- commons-lang3-3.3.jar
- commons-math3-3.2.jar
- commons-net-3.3.jar
- commons-pool2-2.2.jar

**02.apachefelix**

- org.apache.felix.configadmin-1.8.0.jar
- org.apache.felix.http.api-2.2.2.jar
- org.apache.felix.http.base-2.2.2.jar
- org.apache.felix.http.jetty-2.2.2.jar
- org.apache.felix.http.whiteboard-2.2.2.jar
- org.apache.felix.log-1.0.1.jar
- org.apache.felix.scr-1.8.2.jar
- org.apache.felix.shell-1.4.3.jar
- org.apache.felix.shell.remote-1.1.2.jar
- org.apache.felix.webconsole-4.2.2.jar
- org.apache.felix.webconsole.plugins.ds-1.0.0.jar
- org.apache.felix.webconsole.plugins.event-1.1.0.jar
- org.apache.felix.webconsole.plugins.memoryusage-1.0.4.jar
- org.apache.felix.webconsole.plugins.obr-1.0.0.jar
- org.apache.felix.webconsole.plugins.packageadmin-1.0.0.jar
- org.apache.felix.webconsole.plugins.upnp-1.0.2.jar
- org.json-osgi-20080701.jar
- org.osgi.compendium-5.0.0.jar

**03.google**

- guava-16.0.1.jar

**04.jackson**

- jackson-annotations-2.3.2.jar
- jackson-core-2.3.2.jar
- jackson-databind-2.3.2.jar

**10.springframework**

- com.springsource.net.sf.cglib-2.2.0.jar
- com.springsource.org.aopalliance-1.0.0.jar
- org.springframework.aop-3.2.4.RELEASE.jar
- org.springframework.beans-3.2.4.RELEASE.jar
- org.springframework.context-3.2.4.RELEASE.jar
- org.springframework.context.support-3.2.4.RELEASE.jar
- org.springframework.core-3.2.4.RELEASE.jar
- org.springframework.expression-3.2.4.RELEASE.jar
- spring-osgi-core-1.2.1.jar
- spring-osgi-io-1.2.1.jar

**99.others**

- javax.mail-api-1.5.1.jar
- mail-1.4.5.jar
- org.apache.felix.fileinstall-3.2.8.jar


1.0.0 - 2014-02-18
------------------
Public release.


0.1.0-SNAPSHOT - 2013-10-19
---------------------------
Under development.