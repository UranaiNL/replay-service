package com.replay.replayservice;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.replay.replayservice.controller.ReplayController;
import com.replay.replayservice.dto.ReplayRequest;
import com.replay.replayservice.repository.ReplayRepository;
import com.replay.replayservice.service.ReplayService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.web.multipart.MultipartFile;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.io.*;
import java.util.Objects;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(MockitoExtension.class)
class ReplayServiceApplicationTests {

	@Container
	static MongoDBContainer mongoDBContainer = new MongoDBContainer("mongo:7.0.6");
	@Autowired
	private MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private ReplayRepository replayRepository;
	@Mock
	private ReplayService replayService;
	@InjectMocks
	private ReplayController replayController;

	static {
		mongoDBContainer.start();
	}

	@DynamicPropertySource
	static void setProperties(DynamicPropertyRegistry dpr){
		dpr.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
	}

	@Test
	void shouldCreateReplay() throws Exception {
		// Making sure the MP4Upload functionality isn't triggered
		when(replayService.handleFileUpload(any(MultipartFile.class))).thenReturn("test");

		ReplayRequest replayRequest = getReplayRequest();
		String replayRequestString = objectMapper.writeValueAsString(replayRequest);
		mockMvc.perform(MockMvcRequestBuilders.post("/api/replay")
				.contentType(MediaType.APPLICATION_JSON)
				.content(replayRequestString))
				.andExpect(status().isCreated());
		Assertions.assertEquals(1, replayRepository.findAll().size());
	}

	private ReplayRequest getReplayRequest() {
		//MockMultipartFile videoFile = new MockMultipartFile("videoFile", "filename.txt", "text/plain", "some text".getBytes());
		// Create byte array representing file content
		byte[] fileContent = "some text".getBytes();

		return ReplayRequest.builder()
				.uploaderId("111")
				.p1Username("Akira")
				.p2Username("Toriyama")
				.p1CharacterId("1")
				.p2CharacterId("2")
				.gameId("1")
				.build();
	}

}
