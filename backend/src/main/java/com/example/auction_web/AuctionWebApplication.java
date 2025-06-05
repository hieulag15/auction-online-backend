package com.example.auction_web;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AuctionWebApplication {

	public static void main(String[] args) {
		Dotenv dotenv = Dotenv.load();
		// Azure Chat Completion
		System.setProperty("AZURE_API_KEY", dotenv.get("AZURE_API_KEY"));
		System.setProperty("AZURE_ENDPOINT", dotenv.get("AZURE_ENDPOINT"));
		System.setProperty("DEPLOYMENT_NAME", dotenv.get("DEPLOYMENT_NAME"));

		// Azure Embedding
		System.setProperty("AZURE_API_KEY_EMBEDDING", dotenv.get("AZURE_API_KEY_EMBEDDING"));
		System.setProperty("AZURE_ENDPOINT_EMBEDDING", dotenv.get("AZURE_ENDPOINT_EMBEDDING"));
		System.setProperty("DEPLOYMENT_NAME_EMBEDDING", dotenv.get("DEPLOYMENT_NAME_EMBEDDING"));

		// Database
		System.setProperty("DBMS_CONNECTION", dotenv.get("DBMS_CONNECTION"));
		System.setProperty("DBMS_USERNAME", dotenv.get("DBMS_USERNAME"));
		System.setProperty("DBMS_PASSWORD", dotenv.get("DBMS_PASSWORD"));

		// RabbitMQ
		System.setProperty("RABBIT_HOST", dotenv.get("RABBIT_HOST"));
		System.setProperty("RABBIT_USERNAME", dotenv.get("RABBIT_USERNAME"));
		System.setProperty("RABBIT_PASSWORD", dotenv.get("RABBIT_PASSWORD"));

		SpringApplication.run(AuctionWebApplication.class, args);
	}

}
