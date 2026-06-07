package com.org.dream.barberService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;

@SpringBootTest
class BarberServiceApplicationTests {

	@MockBean
	private EurekaAutoServiceRegistration eurekaAutoServiceRegistration;
	@Test
	void contextLoads() {
	}

}
