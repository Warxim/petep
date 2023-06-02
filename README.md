# ![PETEP - PEnetration TEsting Proxy](https://petep.warxim.com/img/logo.png)
**PETEP** (**PEnetration TEsting Proxy**) is open-source Java application for network communication proxying for the purpose of penetration testing. It allows penetration testers to setup proxies and interceptors to manage the traffic transmitted between client and server.

Currently, PETEP supports primarily TCP (with SSL/TLS support) and has basic UDP support.

You might find the following links useful:
- [Official PETEP website](https://petep.warxim.com/)
- [Youtube PETEP - Tutorial (TCP Proxy for Hacking)](https://www.youtube.com/watch?v=DPXEPLYttgQ)

![PETEP](https://petep.warxim.com/img/screenshots/7.png)

## Requirements
You need **Java 11** or newer version to run PETEP.

***Note:** For Mac with ARM64 architecture (M1, M2 chips), use special build for Java 17.*

## Supported functionality
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

## Tunnel TCP through HTTP proxy like Burp or Zaproxy
In order to test TCP communication using existing HTTP proxies like Burp Suite, OWASP Zap etc.),
you can use PETEP external HTTP proxy module, which allows you tunnel the TCP communication through these proxies.

External HTTP proxy module wraps the TCP communication inside HTTP, which is sent through the proxy.
Repeating the packets is also supported as long as the connection is alive, so Burp Intruder/Repeater and Zaproxy Requester/Fuzzer
can also be used for the TCP communication.

![External HTTP Proxy Schema](https://petep.warxim.com/img/user-guide/ehttpp.png)

For more information, see https://petep.warxim.com/user-guide/external-http-proxy/.

***Note:** If you only want to use PETEP as the tunnel for sending the TCP through HTTP proxies,
I would recommend setting it up in GUI mode and then running it in NO-GUI mode (`PETEP [project_path] --nogui`).*

## Extensibility
It is possible to develop extensions using Java to implement support for new protocols and/or to implement new functionality. 

For more information about extension development, please see https://petep.warxim.com/dev-guide/.

## SSL/TLS, STARTTLS proxy support
PETEP supports TCP proxy with SSL/TLS and STARTTLS. In order to use these, you have to provide certificate,
since the application does not generate it itself. For certificate generation, there are many tools that can be used,
but one of them is part of Java binaries (`%JAVA_HOME%/bin/keytool`).

To generate a certificate in JKS keystore, you can use the following command:

```shell
keytool -genkey -alias petep -keyalg RSA -validity 3650 -keysize 4096 -keystore C:/server.jks
```

***Note:** It is recommended to store these certificates alongside the project (project_dir/conf/server.jks)*

## Guides/Tutorials
There are three different guides that will help you use PETEP to its full potential:
- [User Guide](https://petep.warxim.com/user-guide/) - explanation of PETEP components for basic users
- [Methodology](https://petep.warxim.com/methodology/) - methodology with example usage (with video example)
- [Development Guide](https://petep.warxim.com/dev-guide/) - guide for extending PETEP using own Java extensions 
  (to support custom protocols or create custom modules for traffic interception)

## License
PETEP is licensed under GNU GPL 3.0.
