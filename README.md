# ![PETEP - PEnetration TEsting Proxy](https://petep.warxim.com/img/social/logo.png)
[![Release](https://img.shields.io/github/release/Warxim/petep?labelColor=383b53&color=737dde)](https://github.com/Warxim/petep/releases)
[![License: GPL v3](https://img.shields.io/github/license/Warxim/petep?labelColor=383b53&color=98a0e3)](https://www.gnu.org/licenses/gpl-3.0)
![Java: 177](https://img.shields.io/badge/java-11-11?labelColor=383b53&color=737dde)
[![Release Date](https://img.shields.io/github/release-date/Warxim/petep?labelColor=383b53&color=98a0e3)](https://github.com/Warxim/petep/releases)

**PETEP** (**PEnetration TEsting Proxy**) is an open-source Java application for creating proxies and traffic analysis & modification. 
Main goal of PETEP is to provide a useful tool for performing penetration tests of applications with various protocols (on TCP/UDP) 
by setting up proxies and interceptors to manage the traffic transmitted between the client and the server.

![PETEP](https://petep.warxim.com/img/social/screen.png)

## Links
- **[🌐 Official Website](https://petep.warxim.com/)**
- **[🔽 Download](https://github.com/Warxim/petep/releases)**
- **[📖 User Guide](https://petep.warxim.com/user-guide/)**
- **[📚 Methodology](https://petep.warxim.com/methodology/)**
- **[👩‍💻 Developer Guide](https://petep.warxim.com/dev-guide/)**
- **[🎬 Youtube PETEP - Tutorial (TCP Proxy for Hacking)](https://www.youtube.com/watch?v=DPXEPLYttgQ)**

## Installation & Usage
**Requirements:** Java 11+ *(for M1, M2 Mac use Java 17+)*

1. [Download latest PETEP release](https://github.com/Warxim/petep/releases/latest)
2. Extract PETEP zip file
3. Run PETEP using `petep.sh` (Linux, Mac) or `petep.bat` (Windows)
    ```shell
    # Linux / Mac
    chmod +x petep.sh
    ./petep.sh
    
    # Windows
    petep.bat
    ```
***Note:** Provided run scripts contain useful variables,
including working directory (for `petep.json` file), and path to Java executable.
You might need to change it if you do not have it in PATH or you use multiple Java versions
on your machine.*

## Features
Latest PETEP version has the following protocol support:
- **TCP proxy with SSL/TLS support**
- **UDP proxy**

And there are the following functionalities:
- **External HTTP proxy** - support for tunneling the TCP traffic through HTTP proxies like Zaproxy, Burp Suite, etc
- **Logger** - basic text file logging of intercepted traffic
- **Tagger** - rules for tagging protocol data units for easier modification rules and history filtering
- **Modifier** - rules for automatic traffic modification (e.g. replacing bytes)
- **Catcher** - manual traffic interception and modification
- **History** - history of traffic that was intercepted by PETEP with filtering support
- **Repeater** - manual sending and editing of protocol data units
- **Scripter** - basic support for JS scripts for custom traffic processing
- **Connections** - management of active connections

### Scripter extension
In order to use Scripter extension, it is recommended to use GraalVM, since the implementation is built
using GraalVM Polyglot. Since GraalVM 22.2, you might need to install the scripting language in the GraalVM using
`gu install js`.

### Tunnel TCP through HTTP proxy like Burp or Zaproxy
In order to test TCP communication using existing HTTP proxies like Burp Suite, OWASP Zap etc.),
you can use PETEP external HTTP proxy module, which allows you tunnel the TCP communication through these proxies.

External HTTP proxy module wraps the TCP communication inside HTTP, which is sent through the proxy.
Repeating the packets is also supported as long as the connection is alive, so Burp Intruder/Repeater and Zaproxy Requester/Fuzzer
can also be used for the TCP communication.

![External HTTP Proxy Schema](https://petep.warxim.com/img/social/petep-external-http-proxy.png)

For more information, see https://petep.warxim.com/user-guide/external-http-proxy/.

***Note:** If you only want to use PETEP as the tunnel for sending the TCP through HTTP proxies,
I would recommend setting it up in GUI mode and then running it in NO-GUI mode (`PETEP [project_path] --nogui`).*

### SSL/TLS, STARTTLS proxy support
PETEP supports TCP proxy with SSL/TLS and STARTTLS. In order to use these, you have to provide certificate,
since the application does not generate it itself. For certificate generation, there are many tools that can be used,
but one of them is part of Java binaries (`%JAVA_HOME%/bin/keytool`).

To generate a certificate in JKS keystore, you can use the following command:

```shell
keytool -genkey -alias petep -keyalg RSA -validity 3650 -keysize 4096 -keystore server.jks
```

***Note:** It is recommended to store these certificates alongside the project (project_dir/conf/server.jks)*

## Guides/Tutorials
There are three different guides that will help you use PETEP to its full potential:
- [User Guide](https://petep.warxim.com/user-guide/) - explanation of PETEP components for basic users
- [Methodology](https://petep.warxim.com/methodology/) - methodology with example usage (with video example)
- [Development Guide](https://petep.warxim.com/dev-guide/) - guide for extending PETEP using own Java extensions
  (to support custom protocols or create custom modules for traffic interception)

## Extensibility
It is possible to develop extensions using Java to implement support for new protocols and/or to implement new functionality. 

For more information about extension development, please see https://petep.warxim.com/dev-guide/.

## License
PETEP is licensed under GNU GPL 3.0.
