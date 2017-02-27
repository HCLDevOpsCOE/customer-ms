package com.ewolff.microservice.customer;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.stereotype.Component;

@ComponentScan
@EnableAutoConfiguration
@EnableDiscoveryClient
@Component
public class CustomerApp {

	private final CustomerRepository customerRepository;

	@Autowired
	public CustomerApp(CustomerRepository customerRepository) {
		this.customerRepository = customerRepository;
	}

//	@PostConstruct
//	public void generateTestData() {
//		customerRepository.save(new Customer("Lionel", "Messi",
//				"lionel.messi@gmail.com", "Barcelona Ave", "Argentina"));
//		customerRepository.save(new Customer("Phunsuk", "Wangdu",
//				"phunsuk.wangdu@3idiots.com", "Leh Ave", "Leh"));
//		customerRepository.save(new Customer("Hrishikesh", "Deodhar",
//				"hr1sh1kesh.deodhar@devOpsEngineers.com", "Electronics City", "Bangalore"));
//		customerRepository.save(new Customer("Jessie", "James",
//				"Jessie.James@gmail.com", "Electronics City", "Bangalore"));
//	}

	public static void main(String[] args) {
		SpringApplication.run(CustomerApp.class, args);
	}

}
