package uk.ac.man.cs.eventlite.dao;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;
import org.springframework.test.context.junit4.SpringRunner;

import uk.ac.man.cs.eventlite.EventLite;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = EventLite.class)
@DirtiesContext
@ActiveProfiles("test")
@Ignore
public class EventServiceTest extends AbstractTransactionalJUnit4SpringContextTests {

	@Autowired
	private EventService eventService;

	// This class is here as a starter for testing any custom methods within the
	// EventService. Note: It is currently @Ignored!
}
