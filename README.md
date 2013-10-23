OSGi RESTful WebServices
========================

Writting RESTful web services is as simple as writting servlets.

By Thanh Ba Nguyen (btnguyen2k (at) gmail.com)

Project home:
[https://github.com/DDTH/orestws](https://github.com/DDTH/orestws)

//TODO


## License ##

See LICENSE.txt for details. Copyright (c) 2013 Thanh Ba Nguyen.

Third party libraries are distributed under their own license(s).


## Release-notes ##

Latest stable release: N/A.

Latest SNAPSHOT release: v0.1.1-SNAPSHOT.

See [RELEASE-NOTES.md](RELEASE-NOTES.md).


## Installation ##

- Clone github project.
- Build th package with maven: `mvn clean package`.
- The package is available at `orest-distribution/orestws-distribution-<version>-bin/dist`. You may copy it to your favourite location, e.g. `C:\ORESTWS`.

Under directory `orest-distribution/orestws-distribution-<version>-bin/dist` there are several files and directories:

- `README.md`: this file.
- [`RELEASE-NOTES.md`](RELEASE-NOTES.md): release notes.
- [`LICENSE.txt`](LICENSE.txt): license information.
- `bin`: directory contains start/stop scripts.
- `lib`: directory contains the bootstrapper and its dependencies.
- `log`: directory contains log files.
- `runtime_bundles`: during runtime, bundles copied into this folder are auto-deployed; on the other hand, bundles deleted from this folder are auto-uninstalled.
- `startup_bundles`: bundles under this directory are auto-deployed upon startup.
- `var`: used internally by Apache Felix.


## Start/Stop OREST Server ##

### Windows ###

Start server with default JVM memory limit (64mb)
> `C:\ORESTWS\>bin\server_start.bat`

Start server with 1G memory limit
> `C:\ORESTWS\>bin\server_start.bat 1024`

Press `Ctrl-C` to stop the running server.


### Linux ###

Start server with default JVM memory limit (64mb)
> `/usr/local/ORESTWS/bin/server.sh start`

Start server with remote debugging and 2G memory limit
> `/usr/local/ORESTWS/bin/server.sh jpda 2048`

Stop the running server
> `/usr/local/ORESTWS/bin/server.sh stop`

Upon successful startup, ORESTWS server listens for HTTP requests on port 8080 and remote debuging connections on port 8888 (both ports are configurable).

The Admin Console can be accessed via [http://localhost:8080/system/console](http://localhost:8080/system/console) (login with default account `root/password`).
