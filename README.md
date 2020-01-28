# keycloak-event-listener-http

A Keycloak SPI that publishes events to an HTTP Webhook.
A (largely) adaptation of @mhui mhuin/keycloak-event-listener-mqtt SPI

# Build

```
mvn clean install
```

# Deploy

* Copy target/event-listener-http-jar-with-dependencies.jar to {KEYCLOAK_HOME}/standalone/deployments
* Edit standalone.xml to configure the Webhook settings. Find the following
  section in the configuration:

```
<subsystem xmlns="urn:jboss:domain:keycloak-server:1.1">
    <web-context>auth</web-context>
```

And add below:

```
<spi name="eventsListener">
    <provider name="mqtt" enabled="true">
        <properties>
            <property name="serverUri" value="http://127.0.0.1:8080/webhook"/>
            <property name="username" value="auth_user"/>
            <property name="password" value="auth_password"/>
            <property name="topic" value="my_topic"/>
        </properties>
    </provider>
</spi>
```
Leave username and password out if the service allows anonymous access.
If unset, the default message topic is "keycloak/events".

* Restart the keycloak server.

# Use
Add/Update a user, your webhook should be called, looks at the keycloak syslog for debug

Request example
```
{
    "type": "REGISTER",
    "realmId": "myrealm",
    "clientId": "heva",
    "userId": "bcee5034-c65f-4d7c-9036-034042f0a054",
    "ipAddress": "172.21.0.1", 
    "details": {
        "auth_method": "openid-connect",
        "auth_type": "code",
        "register_method": "form",
        "redirect_uri": "http://nginx:8000/",
        "code_id": "98bfe6b2-b8c2-4b82-bc85-9cd033324ec9",
        "email": "fake.email@service.com",
        "username": "username"
    }
}
```