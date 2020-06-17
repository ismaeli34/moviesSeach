package com.example.movies.model;

import java.util.List;

public class CastResponse {
	private int id;
	private List<Cast> cast;
	private List<Crew> crew;
	
	
	public CastResponse() {
		super();
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public List<Cast> getCast() {
		return cast;
	}
	public void setCast(List<Cast> cast) {
		this.cast = cast;
	}
	public List<Crew> getCrew() {
		return crew;
	}
	public void setCrew(List<Crew> crew) {
		this.crew = crew;
	}
	
	

}
