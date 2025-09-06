package com.devsuperior.dsmovie.controllers;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import org.json.JSONException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class MovieControllerRA {
	
	private String movieTitle;
	
	@BeforeEach
	private void setUp() {
		baseURI = "http://localhost:8080";
		
		movieTitle = "Vingadores";
	}
	
	@Test
	public void findAllShouldReturnOkWhenMovieNoArgumentsGiven() {
		
		given()
			.when()
				.get("/movies")
			.then()
				.statusCode(200)
					.body("content.title", hasItems("The Witcher", "Titanic"));
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
	}
	
	@Test
	public void findByIdShouldReturnNotFoundWhenIdDoesNotExist() {	
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
