package com.replay.replayservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replay.replayservice.dto.ReplayRequest;
import com.replay.replayservice.repository.ReplayRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class ReplayServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.6");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ReplayRepository replayRepository;

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dpr){
		dpr.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateReplay() throws Exception {
		ReplayRequest replayRequest = getReplayRequest();
		String replayRequestString = objectMapper.writeValueAsString(replayRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/replay")
				.contentType(MediaType.APPLICATION_JSON)
				.content(replayRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, replayRepository.findAll().size());
	}

	private ReplayRequest getReplayRequest() {
		return ReplayRequest.builder()
				.embedUrl("https://www.youtube.com/watch?v=mLW35YMzELE")
				.p1Username("Akira")
				.p2Username("Toriyama")
				.p1CharacterId("1")
				.p2CharacterId("2")
				.gameId("1")
				.views(0)
				.build();
	}

}
