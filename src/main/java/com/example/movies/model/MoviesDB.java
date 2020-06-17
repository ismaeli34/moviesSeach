package com.example.movies.model;

public class MoviesDB {
	
	private int id;
	private String title;
	public String getTitle() {
		return title;
	}


	public void setTitle(String title) {
		this.title = title;
	}
	private String poster_path;
	private String overview;
	private String release_date;
	private String original_language;
	private String backdrop_path;
	private int [] genre_ids;
	private String vote_average;
	
	
	
	
	public MoviesDB() {
		super();
	}
	
	
	public MoviesDB(int id, String poster_path, String title,
			String overview, String release_date, String original_language,
			String backdrop_path, int[] genre_ids) {
		super();
		this.id = id;
		this.poster_path = poster_path;
		this.overview = overview;
		this.release_date = release_date;
		this.original_language = original_language;
		this.backdrop_path = backdrop_path;
		this.genre_ids = genre_ids;
		this.title= title;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getPoster_path() {
		return poster_path;
	}
	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}
	public String getOverview() {
		return overview;
	}
	public void setOverview(String overview) {
		this.overview = overview;
	}
	public String getRelease_date() {
		return release_date;
	}
	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}
	public String getOriginal_language() {
		return original_language;
	}
	public void setOriginal_language(String original_language) {
		this.original_language = original_language;
	}
	public String getBackdrop_path() {
		return backdrop_path;
	}
	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}
	public int[] getGenre_ids() {
		return genre_ids;
	}
	public void setGenre_ids(int[] genre_ids) {
		this.genre_ids = genre_ids;
	}


	public String getVote_average() {
		return vote_average;
	}


	public void setVote_average(String vote_average) {
		this.vote_average = vote_average;
	}

	


}
