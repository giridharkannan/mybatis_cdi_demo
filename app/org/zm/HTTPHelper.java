package org.zm;

import play.libs.Json;
import play.mvc.Results;
import play.mvc.Results.Status;

import com.fasterxml.jackson.databind.node.ObjectNode;

public class HTTPHelper {
	
	private static final Status SUCCESS = Results.ok(Json.parse("{\"status\": \"success\"}"));
	private static final Status FAILURE = Results.badRequest(Json.parse("{\"status\": \"failed\"}"));
	private static final Status INVALID_JSON_STATUS = Results.badRequest
			(Json.parse("{\"status\": \"failed\", \"message\":\"invalid json data\"}"));
	
	public static Status http_200() {
		return SUCCESS;
	}
	
	public static ObjectNode getSuccJson() {
		ObjectNode node = Json.newObject();
		node.put("status", "success");
		return node;
	}
	
	public static Status http_200(String key, Long value) {
		ObjectNode node = Json.newObject();
		node.put("status", "success");
		node.put(key, value);
		return Results.ok(node);
	}
	
	public static Status http_200(String key, String value) {
		ObjectNode node = Json.newObject();
		node.put("status", "success");
		node.put(key, value);
		return Results.ok(node);
	}
	
	public static Status http_200(String message) {
		return http_200("message", message);
	}
	
	public static Status http_400() {
		return FAILURE;
	}
	
	public static ObjectNode getFailureJson() {
		ObjectNode node = Json.newObject();
		node.put("status", "failed");
		return node;
	}
	
	public static Status http_400_invalid() {
		return INVALID_JSON_STATUS;
	}
	
	public static Status http_400(String message) {
		ObjectNode node = Json.newObject();
		node.put("status", "failed");
		node.put("message", message);
		return Results.badRequest(node);
	}
}
