package com.example.movies.model;

import java.util.List;

public class MovieResponse {
	
	public List<MoviesDB> results;

	public List<MoviesDB> getResults() {
		return results;
	}

	public void setResults(List<MoviesDB> results) {
		this.results = results;
	}

}
