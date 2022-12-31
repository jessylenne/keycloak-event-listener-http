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

import org.keycloak.events.Event;
import org.keycloak.events.EventListenerProvider;
import org.keycloak.events.EventType;
import org.keycloak.events.admin.AdminEvent;
import org.keycloak.events.admin.OperationType;

import java.util.Map;
import java.util.Set;
import java.lang.Exception;

import okhttp3.*;
import okhttp3.OkHttpClient.Builder;

import java.io.IOException;

/**
 * @author <a href="mailto:jessy.lenne@stadline.com">Jessy Lenne</a>
 */
public class HTTPEventListenerProvider implements EventListenerProvider {
	private final OkHttpClient httpClient = new OkHttpClient();
    private Set<EventType> excludedUserEvents;
    private Set<EventType> includedUserEvents;
    private Set<OperationType> excludedAdminEvents;
    private Set<OperationType> includedAdminEvents;
    private String[] adminEventResourcePathPrefixes;
    private Boolean includeAdminEventRepresentation;
    private String serverUri;
    private String username;
    private String password;
    public static final String publisherId = "keycloak";
    public String TOPIC;

    public HTTPEventListenerProvider(Set<EventType> excludedUserEvents, Set<OperationType> excludedAdminEvents, Set<EventType> includedUserEvents, Set<OperationType> includedAdminEvents, String[] adminEventResourcePathPrefixes, Boolean includeAdminEventRepresentation, String serverUri, String username, String password, String topic) {
        this.excludedUserEvents = excludedUserEvents;
        this.excludedAdminEvents = excludedAdminEvents;
        this.includedUserEvents = includedUserEvents;
        this.includedAdminEvents = includedAdminEvents;
        this.adminEventResourcePathPrefixes = adminEventResourcePathPrefixes;
        this.includeAdminEventRepresentation = includeAdminEventRepresentation;
        this.serverUri = serverUri;
        this.username = username;
        this.password = password;
        this.TOPIC = topic;
    }

    @Override
    public void onEvent(Event event) {        
        if (includedUserEvents != null && !includedUserEvents.contains(event.getType())){
            return;
        }

        // Ignore excluded events
        if (excludedUserEvents != null && excludedUserEvents.contains(event.getType())) {
            return;
        } else {
            String stringEvent = toString(event);
            try {
            	RequestBody formBody = new FormBody.Builder()
                        .add("json", stringEvent)
                        .build();

                okhttp3.Request.Builder builder = new Request.Builder()
                        .url(this.serverUri)
                        .addHeader("User-Agent", "KeycloakHttp Bot");
            	

                if (this.username != null && this.password != null) {
                	builder.addHeader("Authorization", "Basic " + this.username + ":" + this.password.toCharArray());
                }
                
                Request request = builder.post(formBody)
                        .build();
                
            	Response response = httpClient.newCall(request).execute();
            	
            	if (!response.isSuccessful()) {
            		throw new IOException("Unexpected code " + response);
            	}

                // Get response body
                System.out.println(response.body().string());
            } catch(Exception e) {
                // ?
                System.out.println("UH OH!! " + e.toString());
                e.printStackTrace();
                return;
            }
        }
    }

    @Override
    public void onEvent(AdminEvent event, boolean includeRepresentation) {
        if (includedAdminEvents != null && !includedAdminEvents.contains(event.getOperationType())){
            return;
        }

        if (adminEventResourcePathPrefixes != null && adminEventResourcePathPrefixes.length > 0) {
            boolean found = false;
            for(String prefix :  adminEventResourcePathPrefixes) {
                String resourcePath = event.getResourcePath();
                if (resourcePath != null && resourcePath.startsWith(prefix)) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return;
            }
        }

        // Ignore excluded operations
        if (excludedAdminEvents != null && excludedAdminEvents.contains(event.getOperationType())) {
            return;
        } else {
            String stringEvent = toString(event);
            try {
            	RequestBody formBody = new FormBody.Builder()
                        .add("json", stringEvent)
                        .build();

                okhttp3.Request.Builder builder = new Request.Builder()
                        .url(this.serverUri)
                        .addHeader("User-Agent", "KeycloakHttp Bot");
            	

                if (this.username != null && this.password != null) {
                	builder.addHeader("Authorization", "Basic " + this.username + ":" + this.password.toCharArray());
                }
                
                Request request = builder.post(formBody)
                        .build();
                
            	Response response = httpClient.newCall(request).execute();
            	
            	if (!response.isSuccessful()) {
            		throw new IOException("Unexpected code " + response);
            	}

                // Get response body
                System.out.println(response.body().string());
            } catch(Exception e) {
                // ?
                System.out.println("UH OH!! " + e.toString());
                e.printStackTrace();
                return;
            }
        }
    }


    private String toString(Event event) {
        StringBuilder sb = new StringBuilder();

        sb.append("{\"type\": \"");
        sb.append(event.getType());
        sb.append("\", \"realmId\": \"");
        sb.append(event.getRealmId());
        sb.append("\", \"clientId\": \"");
        sb.append(event.getClientId());
        sb.append("\", \"userId\": \"");
        sb.append(event.getUserId());
        sb.append("\", \"ipAddress\": \"");
        sb.append(event.getIpAddress());
        sb.append("\"");

        if (event.getError() != null) {
            sb.append(", \"error\": \"");
            sb.append(event.getError());
            sb.append("\"");
        }
        sb.append(", \"details\": {");
        if (event.getDetails() != null) {
            for (Map.Entry<String, String> e : event.getDetails().entrySet()) {
                sb.append("\"");
                sb.append(e.getKey());
                sb.append("\": \"");
                sb.append(e.getValue());
                sb.append("\", ");
            }
        }
        sb.append("}}");

        return sb.toString();
    }
    
    
    private String toString(AdminEvent adminEvent) {
        StringBuilder sb = new StringBuilder();

        OperationType type = adminEvent.getOperationType();

        sb.append("{\"type\": \"");
        sb.append(type);
        sb.append("\", \"realmId\": \"");
        sb.append(adminEvent.getAuthDetails().getRealmId());
        sb.append("\", \"clientId\": \"");
        sb.append(adminEvent.getAuthDetails().getClientId());
        sb.append("\", \"userId\": \"");
        sb.append(adminEvent.getAuthDetails().getUserId());
        sb.append("\", \"ipAddress\": \"");
        sb.append(adminEvent.getAuthDetails().getIpAddress());        
        sb.append("\", \"resourcePath\": \"");
        sb.append(adminEvent.getResourcePath());
        sb.append("\"");

        if(includeAdminEventRepresentation && (type == OperationType.CREATE 
                                            || type == OperationType.UPDATE )) {
            String representation = adminEvent.getRepresentation();
            if(representation != null) {
                sb.append(", \"representation\": ");
                sb.append(representation);
            }
        }

        if (adminEvent.getError() != null) {
            sb.append(", \"error\": \"");
            sb.append(adminEvent.getError());
            sb.append("\"");
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public void close() {
    }

}
