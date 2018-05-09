package uk.ac.man.cs.eventlite.controllers;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.StringContains.containsString;
import static org.hamcrest.core.StringEndsWith.endsWith;
import static org.junit.Assert.assertThat;

import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.LocalServerPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.client.TestRestTemplate.HttpClientOption;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import uk.ac.man.cs.eventlite.EventLite;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@DirtiesContext
@ActiveProfiles("test")
public class VenueControllerIntegrationTest extends AbstractTransactionalJUnit4SpringContextTests {
	
	@LocalServerPort
	private int port;
	
	private String loginUrl;
	private String baseUrl;

	private HttpEntity<String> httpEntity;
	
	private TestRestTemplate stateful;

	@Autowired
	private TestRestTemplate template;
	
	private final TestRestTemplate anon = new TestRestTemplate();
	
	

	@Before
	public void setup() {
		
		this.loginUrl = "http://localhost:" + port + "/sign-in";
		this.baseUrl = "http://localhost:" + port + "/venues";
		HttpHeaders headers = new HttpHeaders();
		headers.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		httpEntity = new HttpEntity<String>(headers);
	}

	@Test
	public void testGetAllVenues() {
		ResponseEntity<String> response = template.exchange("/venues", HttpMethod.GET, httpEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.OK));
	}
	
	
	@Test
	public void postVenueNoLogin() {
		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("name", "Venue W");
		form.add("capacity", "2019");
		form.add("roadName", "Tong Avenue");
		form.add("postcode", "M1 5SX");
		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(form,
				postHeaders);

		ResponseEntity<String> response = anon.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		assertThat(response.getStatusCode(), equalTo(HttpStatus.FORBIDDEN));
		assertThat(4, equalTo(countRowsInTable("venues")));
	}
	
	@Test
	@DirtiesContext
	public void postVenueWithLogin() {
		stateful = new TestRestTemplate(HttpClientOption.ENABLE_COOKIES);

		// Set up headers for GETting and POSTing.
		HttpHeaders getHeaders = new HttpHeaders();
		getHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));

		HttpHeaders postHeaders = new HttpHeaders();
		postHeaders.setAccept(Collections.singletonList(MediaType.TEXT_HTML));
		postHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

		// GET the log in page so we can read the CSRF token and the session
		// cookie.
		HttpEntity<String> getEntity = new HttpEntity<>(getHeaders);
		ResponseEntity<String> formResponse = stateful.exchange(loginUrl, HttpMethod.GET, getEntity, String.class);
		String csrfToken = getCsrfToken(formResponse.getBody());
		String cookie = formResponse.getHeaders().getFirst("Set-Cookie").split(";")[0];

		// Set the session cookie and populate the log in form.
		postHeaders.set("Cookie", cookie);
		MultiValueMap<String, String> login = new LinkedMultiValueMap<>();
		login.add("_csrf", csrfToken);
		login.add("username", "Rob");
		login.add("password", "Haines");

		// Log in.
		HttpEntity<MultiValueMap<String, String>> postEntity = new HttpEntity<MultiValueMap<String, String>>(login,
				postHeaders);
		ResponseEntity<String> loginResponse = stateful.exchange(loginUrl, HttpMethod.POST, postEntity, String.class);
		assertThat(loginResponse.getStatusCode(), equalTo(HttpStatus.FOUND));

		// Set the session cookie and GET the new event form so we can read
		// the new CSRF token.
		getHeaders.set("Cookie", cookie);
		getEntity = new HttpEntity<>(getHeaders);
		formResponse = stateful.exchange(loginUrl, HttpMethod.GET, getEntity, String.class);
		csrfToken = getCsrfToken(formResponse.getBody());

		// Populate the new event form.
		MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
		form.add("_csrf", csrfToken);
		form.add("name", "Venue W");
		form.add("capacity", "2019");
		form.add("roadName", "Tong Avenue");
		form.add("postcode", "M1 5SX");
		postEntity = new HttpEntity<MultiValueMap<String, String>>(form, postHeaders);

		// POST the new event.
		ResponseEntity<String> response = stateful.exchange(baseUrl, HttpMethod.POST, postEntity, String.class);

		// Did it work?
		assertThat(response.getStatusCode(), equalTo(HttpStatus.FOUND));
		assertThat(response.getHeaders().getLocation().toString(), containsString(baseUrl));
		assertThat(5, equalTo(countRowsInTable("venues")));
	}
	
	private String getCsrfToken(String body) {
		Pattern pattern = Pattern.compile("(?s).*name=\"_csrf\".*?value=\"([^\"]+).*");
		Matcher matcher = pattern.matcher(body);

		// matcher.matches() must be called!
		assertThat(matcher.matches(), equalTo(true));

		return matcher.group(1);
	}
}