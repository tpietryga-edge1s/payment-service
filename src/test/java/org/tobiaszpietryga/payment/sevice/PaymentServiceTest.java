package org.tobiaszpietryga.payment.sevice;

import java.util.Optional;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.util.ReflectionTestUtils;
import org.tobiaszpietryga.order.common.model.Order;
import org.tobiaszpietryga.payment.doman.Customer;
import org.tobiaszpietryga.payment.repository.CustomerRepository;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

	public static final String TOPIC_NAME = "payment-orders";
	@Mock
	KafkaTemplate<Long, Order> kafkaTemplate;
	@InjectMocks
	PaymentService underTest;
	@Mock
	CustomerRepository customerRepository;

	@Captor
	ArgumentCaptor<Order> orderCaptor;
	@Captor
	ArgumentCaptor<Customer> customerCaptor;

	@BeforeEach
	void setUp() {
	}

	@Test
	void shouldReservePayment_whenNoPendingPaymentIsPresent() {
		//given
		Mockito.when(customerRepository.findById(1L)).thenReturn(Optional.of(Customer.builder()
				.amountAvailable(10)
				.amountReserved(0)
				.id(1L)
				.build());
		ReflectionTestUtils.setField(underTest, "topicName", TOPIC_NAME);

		//when
		underTest.reservePayment(Order.builder()
				.id(1L)
				.customerId(1L)
				.price(4)
				.build());

		//then
		Mockito.verify(kafkaTemplate).send(TOPIC_NAME, 1L, orderCaptor.capture());
		Order sentOrder = orderCaptor.getValue();
		Assertions.assertThat(sentOrder.isPaymentStarted()).isTrue();

		Mockito.verify(customerRepository).save(customerCaptor.capture());
		Customer savedCustomer = customerCaptor.getValue();
		Assertions.assertThat(savedCustomer.getAmountAvailable()).isEqualTo(6);
		Assertions.assertThat(savedCustomer.getAmountReserved()).isEqualTo(4);
	}
}
