/*
 * Copyright 2019 Red Hat, Inc. and/or its affiliates
 * and other contributors as indicated by the @author tags.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.softwarefactory.keycloak.providers.events.http;

import org.keycloak.Config;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventListenerProviderFactory;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.OperationType;
import org.keycloak.models.KeycloakSession;
import org.keycloak.models.KeycloakSessionFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author <a href="mailto:jessy.lenne@stadline.com">Jessy Lennee</a>
 */
public class HTTPEventListenerProviderFactory implements EventListenerProviderFactory {

    private Set<EventType> excludeUserEvents;
    private Set<EventType> includeUserEvents;
    private Set<OperationType> excludeAdminEvents;
    private Set<OperationType> includedAdminEvents;
    private String[] adminEventResourcePathPrefixes;
    private Boolean includeAdminEventRepresentation;
    private String serverUri;    
    private String username;
    private String password;
    private String topic;

    @Override
    public EventListenerProvider create(KeycloakSession session) {
        return new HTTPEventListenerProvider(excludeUserEvents, excludeAdminEvents, includeUserEvents, includedAdminEvents, adminEventResourcePathPrefixes, includeAdminEventRepresentation, serverUri, username, password, topic);
    }

    @Override
    public void init(Config.Scope config) {
        String[] excludeUserEventsConfig = config.getArray("exclude-user-events");
        if (excludeUserEventsConfig != null) {
            excludeUserEvents = new HashSet<>();
            for (String e : excludeUserEventsConfig) {
                excludeUserEvents.add(EventType.valueOf(e));
            }
        }

        String[] excludeAdminEventsConfig = config.getArray("exclude-admin-events");
        if (excludeAdminEventsConfig != null) {
            excludeAdminEvents = new HashSet<>();
            for (String e : excludeAdminEventsConfig) {
                excludeAdminEvents.add(OperationType.valueOf(e));
            }
        }

        String[] includeUserEventsConfig = config.getArray("include-user-events");
        if (includeUserEventsConfig != null) {
            includeUserEvents = new HashSet<>();            
            if (!(includeUserEventsConfig.length == 1 && includeUserEventsConfig[0].equals("none"))) {
                for (String i : includeUserEventsConfig) {
                    includeUserEvents.add(EventType.valueOf(i));
                }
            }
        }

        String[] includeAdminEventsConfig = config.getArray("include-admin-events");
        if (includeAdminEventsConfig != null) {
            includedAdminEvents = new HashSet<>();
            if (!(includeAdminEventsConfig.length == 1 && includeAdminEventsConfig[0].equals("none"))) {
                for (String i : includeAdminEventsConfig) {
                    includedAdminEvents.add(OperationType.valueOf(i));
                }
            }
        }

        adminEventResourcePathPrefixes = config.getArray("admin-event-resource-path-prefixes");
        includeAdminEventRepresentation = config.getBoolean("include-admin-event-representation", false);

        serverUri = config.get("server-uri", "http://nginx/frontend_dev.php/webhook/keycloak");
        username = config.get("username", null);
        password = config.get("password", null);
        topic = config.get("topic", "keycloak/events");
    }

    @Override
    public void postInit(KeycloakSessionFactory factory) {

    }
    @Override
    public void close() {
    }

    @Override
    public String getId() {
        return "http";
    }

}
