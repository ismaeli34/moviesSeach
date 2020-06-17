package com.example.movies.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Crew {
	private String job;
	private String name;
	@JsonProperty("profile_path")
	private String profilePath;
	
	public String getJob() {
		return job;
	}
	public void setJob(String job) {
		this.job = job;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getProfilePath() {
		return profilePath;
	}
	public void setProfilePath(String profilePath) {
		this.profilePath = profilePath;
	}
	
}
