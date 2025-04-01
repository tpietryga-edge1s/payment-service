package org.tobiaszpietryga.kafka_producer.controller;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.tobiaszpietryga.kafka_producer.sevice.PaymentService;

@RestController
@RequestMapping("orders")
@RequiredArgsConstructor
public class PaymentController {
	Logger logger = LoggerFactory.getLogger(PaymentController.class);
	private final PaymentService orderService;

	@PostMapping
	public void makeOrder(@RequestBody String orderName) {
		logger.info("Received an orderName {}", orderName);
		orderService.sendOrder(orderName);
	}
}
