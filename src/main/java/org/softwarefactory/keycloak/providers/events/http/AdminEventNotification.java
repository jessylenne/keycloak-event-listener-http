package org.softwarefactory.keycloak.providers.events.http;

import java.io.Serializable;

import org.keycloak.events.admin.AdminEvent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeInfo.Id;

@JsonIgnoreProperties(ignoreUnknown = true)
@JsonTypeInfo(use = Id.CLASS)
public class AdminEventNotification extends AdminEvent implements Serializable  {

	private static final long serialVersionUID = -7367949289101799624L;

	public static AdminEventNotification create(AdminEvent adminEvent) {
		AdminEventNotification msg = new AdminEventNotification();
		msg.setAuthDetails(adminEvent.getAuthDetails());
		msg.setError(adminEvent.getError());
		msg.setOperationType(adminEvent.getOperationType());
		msg.setRealmId(adminEvent.getRealmId());
		msg.setRepresentation(adminEvent.getRepresentation());
		msg.setResourcePath(adminEvent.getResourcePath());
		msg.setResourceType(adminEvent.getResourceType());
		msg.setTime(adminEvent.getTime());
		return msg;
	}

	
}
