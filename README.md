# ![PETEP - PEnetration TEsting Proxy](https://petep.warxim.com/img/logo.png)
**PETEP** (**PEnetration TEsting Proxy**) is open-source Java application for network communication proxying for the purpose of penetration testing. It allows penetration testers to setup proxies and interceptors to manage the traffic transmitted between client and server.

Currently PETEP supports primarily TCP (with SSL/TLS support).

You can find out more about PETEP on the following website: https://petep.warxim.com/

## Requirements
You need **Java 11** or newer version to run PETEP.

## Using PETEP with tools for HTTP
PETEP allows you to use your favorite tools for web penetration testing (like Burp Suite, OWASP Zap etc.) for non-HTTP traffic. 

See https://petep.warxim.com/user-guide/external-http-proxy/.

## Extensibility
It is possible to develop extensions using Java to implement support for new protocols and/or to implement new functionality. 

For more information about extension development, please see https://petep.warxim.com/dev-guide/.

## License
PETEP is licensed under GNU GPL 3.0.
