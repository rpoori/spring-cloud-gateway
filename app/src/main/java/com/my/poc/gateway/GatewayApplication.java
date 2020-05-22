package com.my.poc.gateway;

import lombok.Builder;
import lombok.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class GatewayApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayApplication.class, args);
	}

	@GetMapping(value = "/api/hello")
	public ResponseEntity<Message> helloWorld() {
		return ResponseEntity.ok(Message.builder()
				.msg("Hello World")
				.build());
	}

	@GetMapping(value = "/api/secure-endpoint")
	public ResponseEntity<Message> secureEndpoint() {
		return ResponseEntity.ok(Message.builder()
				.msg("You are an authorized user.  Thats why you are seeing this message.")
				.build());
	}

}

@Value
@Builder
class Message {
	String msg;
}
