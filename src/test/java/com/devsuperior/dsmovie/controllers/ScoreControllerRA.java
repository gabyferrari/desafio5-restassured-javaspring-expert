package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class ScoreControllerRA {
	
	private Long existingId, nonExistingId;
	
	private String adminClientUsername, adminClientPassword;
	private String adminClientToken;
	
	private Map<String, Object> putScoreInstance;
	
	@BeforeEach
	private void setUp() throws Exception {
		baseURI = "http://localhost:8080";
		
		existingId = 1L;
		nonExistingId = 100L;
		
		adminClientUsername = "maria@gmail.com";
		adminClientPassword = "123456";	
		
		adminClientToken = TokenUtil.obtainAccessToken(adminClientUsername, adminClientPassword);
	}
	
	@Test
	public void saveScoreShouldReturnNotFoundWhenMovieIdDoesNotExist() throws Exception {
		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", nonExistingId);
		putScoreInstance.put("score", 4);
		
		JSONObject newProduct = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminClientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(newProduct)
				.when()
					.put("/scores")
				.then()
					.statusCode(404)
					.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenMissingMovieId() throws Exception {
		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", existingId);
		putScoreInstance.put("score", 4);
		
		putScoreInstance.put("movieId", null);
		
		JSONObject newProduct = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminClientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(newProduct)
				.when()
					.put("/scores")
				.then()
					.statusCode(422)
					.body("errors.message[0]", equalTo("Required field"));
	}
	
	@Test
	public void saveScoreShouldReturnUnprocessableEntityWhenScoreIsLessThanZero() throws Exception {
		putScoreInstance = new HashMap<>();
		putScoreInstance.put("movieId", existingId);
		putScoreInstance.put("score", 4);
		
		putScoreInstance.put("score", -4);
		
		JSONObject newProduct = new JSONObject(putScoreInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminClientToken)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
			.body(newProduct)
				.when()
					.put("/scores")
				.then()
					.statusCode(422)
					.body("errors.message[0]", equalTo("Score should be greater than or equal to zero"));
	}
}
