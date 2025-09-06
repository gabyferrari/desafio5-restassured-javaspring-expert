package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieControllerRA {
	
	private String movieTitle;
	private Long existingId, nonExistingId;
	
	@BeforeEach
	private void setUp() {
		baseURI = "http://localhost:8080";
		
		movieTitle = "Vingadores";
		
		existingId = 1L;
		nonExistingId = 100L;
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
	}
	
	@Test
	public void insertShouldReturnForbiddenWhenClientLogged() throws Exception {
	}
	
	@Test
	public void insertShouldReturnUnauthorizedWhenInvalidToken() throws Exception {
	}
}
