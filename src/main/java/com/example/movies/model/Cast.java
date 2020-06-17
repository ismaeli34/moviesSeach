package com.example.movies.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
public class Cast {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	private String name;
	@JsonProperty("profile_path")
	private String profilePath;
	@JsonProperty("character")
	@Column(length=512)
	private String castCharacter;
	
	
	public Cast() {
		super();
	}

	public Cast(String name, String castCharacter, String profilePath) {
		this.name=name;
		this.castCharacter=castCharacter;
		this.profilePath=profilePath;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public String getCastCharacter() {
		return castCharacter;
	}

	public void setCastCharacter(String castCharacter) {
		this.castCharacter = castCharacter;
	}

	@Override
	public String toString()
	{
		return this.castCharacter;
	}
	
}
