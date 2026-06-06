package com.barberService.auth_service;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.cloud.netflix.eureka.serviceregistry.EurekaAutoServiceRegistration;

@SpringBootTest
class AuthServiceApplicationTests {

	@MockBean
	private EurekaAutoServiceRegistration eurekaAutoServiceRegistration;
	@Test
	void contextLoads() {
	}

}
