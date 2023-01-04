# keycloak-event-listener-http

A KeyCloak SPI that publishes events to an HTTP Webhook.
A (largely) adaptation of @mhui mhuin/keycloak-event-listener-mqtt SPI.
Extended by @darrensapalo to [enable building the JAR files from docker images](https://sapalo.dev/2021/06/16/send-keycloak-webhook-events/).

Built for KeyCloak 20 or above.

## Build

### Build on your local machine

```sh
mvn clean install
```

### Build using docker

Alternatively, you can [build the JAR files from a docker image](https://sapalo.dev/2021/06/16/send-keycloak-webhook-events/). You must have `docker` installed.

1. Run `make package-image`.
2. The JAR files should show up on your `mvn-output` folder.

If you encounter the following issue:

```
open {PATH}/mvn-output/event-listener-http-jar-with-dependencies.jar: permission denied
```

Simply add write permissions to the `mvn-output` folder:

```sh
sudo chown $USER:$USER mvn-output
```

## Deploy

* Copy `mvn-output/event-listener-http-jar-with-dependencies.jar` to `{KEYCLOAK_HOME}/providers`
* Configure the webhook settings (see below) using the configuration way you would normally use (see https://www.keycloak.org/server/configuration).
* Restart the KeyCloak server.

## Configuration

You can set these configuration values for the "SPI Events Listeners" "http" object:

- `server-uri`: the webhook to call
- `username`: For authentication of the webhook. Leave username and password out if the service allows anonymous access.
- `password`: For authentication of the webhook. Leave username and password out if the service allows anonymous access.
- `topic`: unknown. If unset, the default message topic is "keycloak/events".
- `include-user-events`: A comma separated list of user events you want to get notifications for. Enter `none` of you want to disable notification for user events altogether.
- `exclude-user-events`: A comma separated list of user events you don't want to get notifications for.
- `include-admin-events`: A comma separated list of admin events you want to get notifications for. Enter `none` of you want to disable notification for user events altogether.
- `exclude-admin-events`: A comma separated list of admin events you don't want to get notifications for.
- `admin-event-resource-path-prefixes`: A comma separated list of path prefixes. Allows you to filter resource paths. Will send notifications for paths that begin with these prefixes.
- `include-admin-event-representation`: If true, the HTTP webhook request will include the data of CREATE and UPDATE admin events (username etc.)

Example with environment variables:

```
KC_SPI_EVENTS_LISTENER_HTTP_SERVER_URI=http://127.0.0.1:8080/webhook
KC_SPI_EVENTS_LISTENER_HTTP_USERNAME=auth_user
KC_SPI_EVENTS_LISTENER_HTTP_PASSWORD=auth_password
KC_SPI_EVENTS_LISTENER_HTTP_TOPIC=my_topic
KC_SPI_EVENTS_LISTENER_HTTP_INCLUDE_USER_EVENTS=none
KC_SPI_EVENTS_LISTENER_HTTP_EXCLUDE_USER_EVENTS=LOGIN
KC_SPI_EVENTS_LISTENER_HTTP_INCLUDE_ADMIN_EVENTS=DELETE
KC_SPI_EVENTS_LISTENER_HTTP_EXCLUDE_ADMIN_EVENTS=ACTION
KC_SPI_EVENTS_LISTENER_HTTP_ADMIN_EVENT_RESOURCE_PATH_PREFIXES=users/
KC_SPI_EVENTS_LISTENER_HTTP_INCLUDE_ADMIN_EVENT_REPRESENTATION=true
```

Example with option to `kc.sh start`:

```
--spi-events-listener-http-server-uri=http://127.0.0.1:8080/webhook
--spi-events-listener-http-exclude-user-events=none
...
```

### Valid user event names

LOGIN, LOGIN_ERROR, REGISTER, REGISTER_ERROR, LOGOUT, LOGOUT_ERROR, CODE_TO_TOKEN, CODE_TO_TOKEN_ERROR, CLIENT_LOGIN, CLIENT_LOGIN_ERROR, REFRESH_TOKEN, REFRESH_TOKEN_ERROR, INTROSPECT_TOKEN, INTROSPECT_TOKEN_ERROR,FEDERATED_IDENTITY_LINK, FEDERATED_IDENTITY_LINK_ERROR, REMOVE_FEDERATED_IDENTITY, REMOVE_FEDERATED_IDENTITY_ERROR, UPDATE_EMAIL, UPDATE_EMAIL_ERROR, UPDATE_PROFILE, UPDATE_PROFILE_ERROR, UPDATE_PASSWORD, UPDATE_PASSWORD_ERROR,UPDATE_TOTP, UPDATE_TOTP_ERROR, VERIFY_EMAIL, VERIFY_EMAIL_ERROR, VERIFY_PROFILE, VERIFY_PROFILE_ERROR, REMOVE_TOTP, REMOVE_TOTP_ERROR, GRANT_CONSENT, GRANT_CONSENT_ERROR, UPDATE_CONSENT, UPDATE_CONSENT_ERROR, REVOKE_GRANT,REVOKE_GRANT_ERROR, SEND_VERIFY_EMAIL, SEND_VERIFY_EMAIL_ERROR, SEND_RESET_PASSWORD, SEND_RESET_PASSWORD_ERROR, SEND_IDENTITY_PROVIDER_LINK, SEND_IDENTITY_PROVIDER_LINK_ERROR, RESET_PASSWORD, RESET_PASSWORD_ERROR,RESTART_AUTHENTICATION, RESTART_AUTHENTICATION_ERROR, INVALID_SIGNATURE, INVALID_SIGNATURE_ERROR, REGISTER_NODE, REGISTER_NODE_ERROR, UNREGISTER_NODE, UNREGISTER_NODE_ERROR, USER_INFO_REQUEST, USER_INFO_REQUEST_ERROR,IDENTITY_PROVIDER_LINK_ACCOUNT, IDENTITY_PROVIDER_LINK_ACCOUNT_ERROR, IDENTITY_PROVIDER_LOGIN, IDENTITY_PROVIDER_LOGIN_ERROR, IDENTITY_PROVIDER_FIRST_LOGIN, IDENTITY_PROVIDER_FIRST_LOGIN_ERROR, IDENTITY_PROVIDER_POST_LOGIN,IDENTITY_PROVIDER_POST_LOGIN_ERROR, IDENTITY_PROVIDER_RESPONSE, IDENTITY_PROVIDER_RESPONSE_ERROR, IDENTITY_PROVIDER_RETRIEVE_TOKEN, IDENTITY_PROVIDER_RETRIEVE_TOKEN_ERROR, IMPERSONATE, IMPERSONATE_ERROR, CUSTOM_REQUIRED_ACTION,CUSTOM_REQUIRED_ACTION_ERROR, EXECUTE_ACTIONS, EXECUTE_ACTIONS_ERROR, EXECUTE_ACTION_TOKEN, EXECUTE_ACTION_TOKEN_ERROR, CLIENT_INFO, CLIENT_INFO_ERROR, CLIENT_REGISTER, CLIENT_REGISTER_ERROR, CLIENT_UPDATE, CLIENT_UPDATE_ERROR,CLIENT_DELETE, CLIENT_DELETE_ERROR, CLIENT_INITIATED_ACCOUNT_LINKING, CLIENT_INITIATED_ACCOUNT_LINKING_ERROR, TOKEN_EXCHANGE, TOKEN_EXCHANGE_ERROR, OAUTH2_DEVICE_AUTH, OAUTH2_DEVICE_AUTH_ERROR, OAUTH2_DEVICE_VERIFY_USER_CODE,OAUTH2_DEVICE_VERIFY_USER_CODE_ERROR, OAUTH2_DEVICE_CODE_TO_TOKEN, OAUTH2_DEVICE_CODE_TO_TOKEN_ERROR, AUTHREQID_TO_TOKEN, AUTHREQID_TO_TOKEN_ERROR, PERMISSION_TOKEN, PERMISSION_TOKEN_ERROR, DELETE_ACCOUNT, DELETE_ACCOUNT_ERROR,PUSHED_AUTHORIZATION_REQUEST, PUSHED_AUTHORIZATION_REQUEST_ERROR

### Valid admin event names

CREATE, UPDATE, DELETE, ACTION

## Use

When you add or update a user, your webhook should be called. Check the KeyCloak syslog for debugging information.

Request example

```json
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
