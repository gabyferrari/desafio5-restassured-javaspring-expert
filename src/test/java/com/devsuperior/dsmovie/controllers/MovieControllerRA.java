package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.simple.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.devsuperior.dsmovie.tests.TokenUtil;

import io.restassured.http.ContentType;

public class MovieControllerRA {
	
	private String movieTitle;
	private Long existingId, nonExistingId;
	private String adminUsername, adminPassword, clientUsername, clientPassword;
	private String adminToken, clientToken, invalidToken;
	
	private Map<String, Object> postMovieInstance;
	
	@BeforeEach
	private void setUp() throws Exception {
		baseURI = "http://localhost:8080";
		
		movieTitle = "Vingadores";
		
		existingId = 1L;
		nonExistingId = 100L;
		
		adminUsername = "maria@gmail.com";
		adminPassword = "123456";
		clientUsername = "alex@gmail.com";
		clientPassword = "123456";
		
		adminToken = TokenUtil.obtainAccessToken(adminUsername, adminPassword);
		clientToken = TokenUtil.obtainAccessToken(clientUsername, clientPassword);
		invalidToken = adminToken + "xpto";
		
		postMovieInstance = new HashMap<>();
		postMovieInstance.put("title", "Test Movie");
		postMovieInstance.put("score", 0.0);
		postMovieInstance.put("count", 0);
		postMovieInstance.put("image", "https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg");
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()
			.when()
				.get("/movies")
			.then()
				.statusCode(200)
					.body("content.title", hasItems("Harry Potter e a Pedra Filosofal", "Titanic"));
	}
	
	@Test
	public void findAllShouldReturnPagedMoviesWhenMovieTitleParamIsNotEmpty() {	
		
		given()
			.when()
				.get("/movies?title={movieTitle}", movieTitle)
			.then()
				.statusCode(200)
					.body("content.id[0]", is(13))
					.body("content.title[0]", equalTo("Vingadores: Ultimato"))
					.body("content.image[0]", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/7RyHsO4yDXtBv1zUU3mTpHeQ0d5.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnMovieWhenIdExists() {	
		
		given()
			.when()
				.get("/movies/{id}", existingId)
			.then()
				.statusCode(200)
					.body("id", is(1))
					.body("title", equalTo("The Witcher"))
					.body("score", is(4.5F))
					.body("count", is(2))
					.body("image", equalTo("https://www.themoviedb.org/t/p/w533_and_h300_bestv2/jBJWaqoSCiARWtfV0GlqHrcdidd.jpg"));
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
		
		given()
			.when()
				.get("/movies/{id}", nonExistingId)
			.then()
				.statusCode(404)
				.body("error", equalTo("Recurso não encontrado"));
	}
	
	@Test
	public void insertShouldReturnUnprocessableEntityWhenAdminLoggedAndBlankTitle() throws JSONException {	
		postMovieInstance.put("title", "  ");
		
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + adminToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
				.when()
					.post("/movies")
				.then()
					.statusCode(422)
					.body("errors.message", hasItems("Title must be between 5 and 80 characters", "Required field"));
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
		
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + clientToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
				.when()
					.post("/movies")
				.then()
					.statusCode(403);
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
		
		JSONObject newProduct = new JSONObject(postMovieInstance);
		
		given()
			.header("Content-type", "application/json")
			.header("Authorization", "Bearer " + invalidToken)
			.body(newProduct)
			.contentType(ContentType.JSON)
			.accept(ContentType.JSON)
				.when()
					.post("/movies")
				.then()
					.statusCode(401);
	}
}
