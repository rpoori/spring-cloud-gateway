package com.my.poc.gateway;

import com.my.poc.gateway.circuitbreaker.CircuitBreakerState;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.timelimiter.TimeLimiterConfig;
import lombok.Builder;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RequiredArgsConstructor
@RestController
@Slf4j
public class GatewayApplication {

	private final CircuitBreakerRegistry circuitBreakerRegistry;
	private final TimeLimiterConfig timeLimiterConfig;

	@org.springframework.beans.factory.annotation.Value("${circuit.breaker.identifier.default}")
	private String circuitBreakerIdentifier;

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

	@GetMapping("/fallback")
	public ResponseEntity fallback() {
		CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerIdentifier);
		String circuitBreakerState = CircuitBreakerState.getDesc(circuitBreaker.getState().getOrder());
		log.info("Executing fallback for circuit breaker: " + circuitBreakerIdentifier + ", circuit breaker state: " + circuitBreakerState);
		log.info("Circuit breaker sliding window size: " + circuitBreaker.getCircuitBreakerConfig().getSlidingWindowSize());
		log.info("Circuit breaker permitted calls half open state: " + circuitBreaker.getCircuitBreakerConfig().getPermittedNumberOfCallsInHalfOpenState());
		log.info("Circuit breaker failure rate threshold: " + circuitBreaker.getCircuitBreakerConfig().getFailureRateThreshold());
		log.info("Circuit breaker wait duration open state milliseconds: " + circuitBreaker.getCircuitBreakerConfig().getWaitDurationInOpenState());
		log.info("Circuit breaker timeout duration milliseconds: " + timeLimiterConfig.getTimeoutDuration());

		log.info("Circuit breaker metrics number of successful calls: " + circuitBreaker.getMetrics().getNumberOfSuccessfulCalls());
		log.info("Circuit breaker metrics failure rate: " + circuitBreaker.getMetrics().getFailureRate());
		log.info("Circuit breaker metrics number of buffered calls: " + circuitBreaker.getMetrics().getNumberOfBufferedCalls());
		log.info("Circuit breaker metrics number of failed calls: " + circuitBreaker.getMetrics().getNumberOfFailedCalls());
		log.info("Circuit breaker metrics number of not permitted calls: " + circuitBreaker.getMetrics().getNumberOfNotPermittedCalls());
		log.info("Circuit breaker metrics number of slow calls: " + circuitBreaker.getMetrics().getNumberOfSlowCalls());
		log.info("Circuit breaker metrics number of slow failed calls: " + circuitBreaker.getMetrics().getNumberOfSlowFailedCalls());
		log.info("Circuit breaker metrics number of slow successful calls: " + circuitBreaker.getMetrics().getNumberOfSlowSuccessfulCalls());
		log.info("Circuit breaker metrics slow call rate: " + circuitBreaker.getMetrics().getSlowCallRate());

		return new ResponseEntity(HttpStatus.FAILED_DEPENDENCY);
	}
}

@Value
@Builder
class Message {
	String msg;
}
