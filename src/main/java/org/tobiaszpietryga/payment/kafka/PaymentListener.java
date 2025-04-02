package org.tobiaszpietryga.payment.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.tobiaszpietryga.order.common.model.Order;
import org.tobiaszpietryga.order.common.model.Status;
import org.tobiaszpietryga.payment.sevice.PaymentService;

@KafkaListener(id = "orders", topics = "${orders.topic.name}", groupId = "payment")
@Slf4j
@RequiredArgsConstructor
public class PaymentListener {
	private final PaymentService paymentService;
	public void onEvent(Order order) {
		log.info("Received: {}", order);
		if (order.getStatus().equals(Status.NEW)) {
			paymentService.reservePayment(order);
		} else {
			paymentService.confirmOrRollbackPayment(order);
		}
	}
}
