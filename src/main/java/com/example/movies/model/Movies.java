package com.example.movies.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

/**
 * @author ronneyismael
 *
 */

/*
Entities in JPA are nothing but POJOs representing data that can be persisted to the database.
An entity represents a table stored in a database. Every instance of an entity represents a row in the table.
*/
@Entity
public class Movies {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String title;
	private String director;
	@Column(length=100)
	private String language;
	@Column(length=100)
	private String genre;
	@Column(length=12)
	private String release_date;
	@Column(length=5)
	private String ratings;
	@Column(name="overview", length=1024)
	private String overview;
	@Column(name="poster_path", length=1024)

	private String poster_path;
	@Column(name="backdrop_path", length=1024)
	private String backdrop_path;
	@OneToMany(cascade = CascadeType.ALL,targetEntity=Cast.class)
	//@JoinColumn(name = "movie_cast_id", nullable = false)
	//@JoinTable(name = "movies_cast", joinColumns = @JoinColumn(name = "id"), inverseJoinColumns = @JoinColumn(name = "movie_cast_id"))
	@JoinColumn(name = "movie_cast_id")
	private List<Cast>  cast = new ArrayList<>();
	@OneToOne(cascade = CascadeType.ALL)
	@JoinColumn(name="movie_trailer_id")
	private TrailerLink trailer;
	@Column(length=5)
	private int vote_average;





	public Movies(int id, String title, String director,
		String language, String genre, String release_date,
		
		String ratings,String overview,String poster_path,String backdrop_path,int vote_average) {
		this.id = id;
		this.title = title;
		this.director = director;
		this.language = language;
		this.genre = genre;
		this.release_date = release_date;
		this.ratings = ratings;
		this.overview = overview;
		this.poster_path = poster_path;
		this.backdrop_path = backdrop_path;
		this.vote_average = vote_average;

	}
	
	

	public  Movies(){

	}
	
	public String getBackdrop_path() {
		return backdrop_path;
	}

	public void setBackdrop_path(String backdrop_path) {
		this.backdrop_path = backdrop_path;
	}

	public int getVote_average() {
		return vote_average;
	}

	public void setVote_average(int vote_average) {
		this.vote_average = vote_average;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public String getOverview() {
		return overview;
	}

	public void setOverview(String overview) {
		this.overview = overview;
	}

	public String getPoster_path() {
		return poster_path;
	}

	public void setPoster_path(String poster_path) {
		this.poster_path = poster_path;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getDirector() {
		return director;
	}

	public void setDirector(String director) {
		this.director = director;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public String getGenre() {
		return genre;
	}

	public void setGenre(String genre) {
		this.genre = genre;
	}

	public String getRelease_date() {
		return release_date;
	}

	public void setRelease_date(String release_date) {
		this.release_date = release_date;
	}

	public String getRatings() {
		return ratings;
	}

	public void setRatings(String ratings) {
		this.ratings = ratings;
	}

	
	public List<Cast> getCast() {
		if(cast==null)
		{
			cast = new ArrayList<>();
		}
		return cast;
	}



	public void setCast(List<Cast> cast) {
		this.cast = cast;
	}



	public TrailerLink getTrailer() {
		return trailer;
	}



	public void setTrailer(TrailerLink trailer) {
		this.trailer = trailer;
	}



	public String toString(){
	return "Movies [id="+id+",title="
			+title+",director="
			+director+",language="
			+language+",genre="+genre+",release_date="+release_date+",ratings="+ratings+"]";
	}
}
