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


import org.keycloak.Config.Scope;

/**
 * @author <a href="mailto:traore_a@outlook.com">Abdoulaye Traore</a>
 */
public class HTTPEventConfiguration {

    private String serverUri;
	private String username;
	private String password;
	
	public static HTTPEventConfiguration createFromScope(Scope config) {
		HTTPEventConfiguration configuration = new HTTPEventConfiguration();
		
		configuration.serverUri = resolveConfigVar(config, "serverUri", "http://127.0.0.1:8080/webhook");
		configuration.username = resolveConfigVar(config, "username", "keycloak");
		configuration.password = resolveConfigVar(config, "password", "keycloak");

		return configuration;
		
	}
	
	private static String resolveConfigVar(Scope config, String variableName, String defaultValue) {
		
		String value = defaultValue;
		if(config != null && config.get(variableName) != null) {
			value = config.get(variableName);
		} else {
			//try from env variables eg: HTTP_EVENT_:
			String envVariableName = "HTTP_EVENT_" + variableName.toUpperCase();
			if(System.getenv(envVariableName) != null) {
				value = System.getenv(envVariableName);
			}
		}
		System.out.println("HTTPEventListener configuration: " + variableName + "=" + value);
		return value;
		
	}
	
	
	public String getServerUri() {
		return serverUri;
	}
	public void setServerUri(String serverUri) {
		this.serverUri = serverUri;
	}

	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	

}
