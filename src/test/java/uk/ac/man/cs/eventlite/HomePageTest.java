package uk.ac.man.cs.eventlite;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class HomePageTest {

	private MockMvc mvc;

	@Autowired
	private WebApplicationContext context;

	@Before
	public void setup() {
		mvc = MockMvcBuilders.webAppContextSetup(context).build();
	}

	@Test
	public void getRoot() throws Exception {
		mvc.perform(get("/").accept(MediaType.TEXT_HTML)).andExpect(status().isOk());
	}

	@Test
	public void getJsonRoot() throws Exception {
		mvc.perform(get("/").accept(MediaType.APPLICATION_JSON)).andExpect(status().isNotAcceptable());
	}

	@Test
	public void getApiRoot() throws Exception {
		mvc.perform(get("/api").accept(MediaType.APPLICATION_JSON)).andExpect(status().isOk());
	}
}
