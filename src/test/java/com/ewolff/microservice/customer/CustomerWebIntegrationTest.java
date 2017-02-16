package com.ewolff.microservice.customer;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.stream.StreamSupport;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = CustomerApp.class)
@IntegrationTest
@WebAppConfiguration
@ActiveProfiles("test")
public class CustomerWebIntegrationTest {

	@Autowired
	private CustomerRepository customerRepository;

	@Value("${server.port}")
	private int serverPort;

	private RestTemplate restTemplate;

	private <T> T getForMediaType(Class<T> value, MediaType mediaType,
			String url) {
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Arrays.asList(mediaType));

		HttpEntity<String> entity = new HttpEntity<String>("parameters",
				headers);

		ResponseEntity<T> resultEntity = restTemplate.exchange(url,
				HttpMethod.GET, entity, value);

		return resultEntity.getBody();
	}

	@Test
	public void IsCustomerReturnedAsHTML() {

		Customer customerWolff = customerRepository.findByName("Messi").get(0);

		String body = getForMediaType(String.class, MediaType.TEXT_HTML,
				customerURL() + customerWolff.getId() + ".html");

		assertThat(body, containsString("Messi"));
		assertThat(body, containsString("<div"));
	}

	@Before
	public void setUp() {
		restTemplate = new RestTemplate();
	}

	@Test
	public void IsCustomerReturnedAsJSON() {

		Customer customer = customerRepository.findByName("Messi").get(0);

		String url = customerURL() + "customer/" + customer.getId();
		Customer body = getForMediaType(Customer.class,
				MediaType.APPLICATION_JSON, url);

		assertThat(body, equalTo(customer));
	}

	@Test
	public void IsCustomerListReturned() {

		Iterable<Customer> customers = customerRepository.findAll();
		
		assertTrue(StreamSupport.stream(customers.spliterator(), false)
				.noneMatch(c -> (c.getName().equals("Tendulkar"))));
		
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				customerURL() + "/list.html", String.class);
		
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		
		String customerList = resultEntity.getBody();
		assertFalse(customerList.contains("Mandela"));
		customerRepository.save(new Customer("Mohandas", "Gandhi",
				"mkgandhi@twitter.com", "Banaras", "Gujarat"));

		customerList = restTemplate.getForObject(customerURL() + "/list.html",
				String.class);
		assertTrue(customerList.contains("Gandhi"));

	}

	private String customerURL() {
		return "http://localhost:" + serverPort + "/";
	}

	@Test
	public void IsCustomerFormDisplayed() {
		ResponseEntity<String> resultEntity = restTemplate.getForEntity(
				customerURL() + "/form.html", String.class);
		assertTrue(resultEntity.getStatusCode().is2xxSuccessful());
		assertTrue(resultEntity.getBody().contains("<form"));
	}

	@Test
	@Transactional
	public void IsSubmittedCustomerSaved() {
		assertEquals(0, customerRepository.findByName("Gandhi").size());
		MultiValueMap<String, String> map = new LinkedMultiValueMap<String, String>();
		map.add("firstname", "Mohandas");
		map.add("name", "Gandhi");
		map.add("street", "Banaras");
		map.add("city", "Gujarat");
		map.add("email", "mkgandhi@twitter.com");

		restTemplate.postForObject(customerURL() + "form.html", map,
				String.class);
		assertEquals(1, customerRepository.findByName("Gandhi").size());
	}

}
