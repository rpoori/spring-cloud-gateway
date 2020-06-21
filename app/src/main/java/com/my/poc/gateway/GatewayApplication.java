package com.my.poc.gateway;

import lombok.Builder;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.discovery.GatewayDiscoveryClientAutoConfiguration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	// Unsecure endpoint
	@GetMapping(value = "/api/hello")
	public ResponseEntity<Message> helloWorld() {
		return ResponseEntity.ok(Message.builder()
				.msg("Hello World")
				.build());
	}

	// Secure endpoint
	@GetMapping(value = "/api/secure-endpoint")
	public ResponseEntity<Message> secureEndpoint() {
		return ResponseEntity.ok(Message.builder()
				.msg("You are an authorized user.  Thats why you are seeing this message.")
				.build());
	}

	@PostMapping(value = "/api/document")
	public ResponseEntity<Resource> getDocument() {
		return ResponseEntity.ok(new ClassPathResource("hello-world.pdf"));
	}

}

@Value
@Builder
class Message {
	String msg;
}
