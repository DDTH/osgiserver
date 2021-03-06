OSGi Server
===========

OSGi Server With Start/Stop Scripts.

Project home:
[https://github.com/DDTH/osgiserver](https://github.com/DDTH/osgiserver)


## License ##

See [LICENSE.txt](LICENSE.txt) for details. Copyright (c) 2013-2014 Thanh Ba Nguyen.

Third party libraries are distributed under their own license(s).


## Release-notes ##

Latest stable release: `2.0.0`.

See [RELEASE-NOTES.md](RELEASE-NOTES.md).


## Installation ##

Note: Java 6 or higher is required!

### Install from binary ###

- Download binary package from [project release workspace](https://github.com/DDTH/osgiserver/releases).
- Unzip the binary package and copy it to your favourite location, e.g. `C:\OSGiServer`.

### Install from source ###

- Download application's source, either cloning github project or download the source package from [project release site](https://github.com/DDTH/osgiserver/releases).
- Build with maven: `mvn clean package`.
- The built binary package is available at `osgiserver-distribution/osgiserver-distribution-<version>-bin/dist`. You may copy it to your favourite location, e.g. `/usr/local/osgiserver`.

The binary package contains several files and directories:

- `README.md`: this file.
- [`RELEASE-NOTES.md`](RELEASE-NOTES.md): release notes.
- [`LICENSE.txt`](LICENSE.txt): license information.
- `bin`: directory contains start/stop scripts.
- `lib`: directory contains the bootstrapper and its dependencies.
- `logs`: directory contains log files.
- `runtime_bundles`: during runtime, bundles copied into this folder are auto-deployed; on the other hand, bundles deleted from this folder are auto-uninstalled.
- `startup_bundles`: bundles under this directory are auto-deployed upon startup.
- `tmp`: directory to store temporary files.


## Start/Stop OSGi Server ##

### Windows ###

Start server with default JVM memory limit (64mb)
> `C:\OSGiServer\>bin\server_start.bat`

Start server with 1G memory limit
> `C:\OSGiServer\>bin\server_start.bat 1024`

Start server with remote debugging and 2G memory limit
> `C:\OSGiServer\>bin\server_start.bat jpda 2048`

Press `Ctrl-C` to stop the running server.


### Linux ###

Start server with default JVM memory limit (64mb)
> `/usr/local/osgiserver/bin/server.sh start`

Start server with remote debugging and 2G memory limit
> `/usr/local/osgiserver/bin/server.sh jpda 2048`

Stop the running server
> `/usr/local/osgiserver/bin/server.sh stop`

Upon successful startup, OSGi server listens for HTTP requests on port 8080 and remote debuging connections on port 8888 (both ports are configurable).

The Admin Console can be accessed via [http://localhost:8080/system/console](http://localhost:8080/system/console) (login with default account `root/password`).


## Configurations ##

### Java & Startup Script Configurations ###

> Windows: file `bin\server_start.bat`. Linux: file `/bin/server.sh`.

Production/Development environment:
> Look for line starts with `ENV_NAME=`, change the setting to either `production` or `development`.

Remote debugging port:
> Loong for line `JPDA_PORT=8888`, change the port number as you need.

### OSGi/Felix Configurations ###

File `bin/osgi-felix.properties`.
