package com.example.demo.client;

import java.util.Map;

import io.modelcontextprotocol.client.McpClient;
import io.modelcontextprotocol.client.transport.ServerParameters;
import io.modelcontextprotocol.client.transport.StdioClientTransport;
import io.modelcontextprotocol.json.McpJsonMapper;
import io.modelcontextprotocol.spec.McpSchema.CallToolRequest;
import io.modelcontextprotocol.spec.McpSchema.CallToolResult;
import io.modelcontextprotocol.spec.McpSchema.ListToolsResult;

public class ClientStdio {

	public static void main(String[] args) {

		var stdioParams = ServerParameters.builder("java")
				.args("-jar", "build/libs/spring-ai-demo-0.0.1-SNAPSHOT.jar")
				.build();

		var transport = new StdioClientTransport(stdioParams, McpJsonMapper.createDefault());
		var client = McpClient.sync(transport).build();

		client.initialize();

		// List and demonstrate tools
		ListToolsResult toolsList = client.listTools();
		System.out.println("Available Tools = " + toolsList);

		CallToolResult listResult = client.callTool(new CallToolRequest("listItems", Map.of()));
		System.out.println("All items: " + listResult);

		CallToolResult searchResult = client.callTool(
				new CallToolRequest("searchItemsByName", Map.of("name", "リンゴ")));
		System.out.println("Search response: " + searchResult);

		CallToolResult registerResult = client.callTool(new CallToolRequest("registerItem", Map.of("request",
				Map.of("name", "さくらんぼ", "price", 600, "description", "甘い佐藤錦"))));
		System.out.println("Register result: " + registerResult);

		CallToolResult updateResult = client.callTool(new CallToolRequest("updateItem", Map.of("change",
				Map.of("id", 1L, "name", "青森リンゴ", "price", 180))));
		System.out.println("Update result: " + updateResult);

		CallToolResult deleteResult = client.callTool(new CallToolRequest("removeItem", Map.of("id", 10L)));
		System.out.println("Delete result: " + deleteResult);

		client.closeGracefully();
	}

}
