package com.example.movies.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class TrailerLink {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long trailerId;
	@JsonProperty("key")
	private String trailerKey;
	@JsonProperty("name")
	private String trailerName;
	
	
	public TrailerLink() {
		super();
	}

	public String getKey() {
		return trailerKey;
	}
	public void setKey(String key) {
		this.trailerKey = key;
	}
	public String getName() {
		return trailerName;
	}
	public void setName(String name) {
		this.trailerName = name;
	}

	public long getTrailerId() {
		return trailerId;
	}

	public void setTrailerId(long trailerId) {
		this.trailerId = trailerId;
	}

	
}
