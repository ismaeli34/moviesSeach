/**
 * 
 */
package com.example.movies.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.apache.lucene.queryparser.classic.ParseException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


import com.example.movies.repository.MoviesRepository;
import com.example.movies.services.MovieService;
import com.example.movies.services.SearchEngineService;
import com.example.movies.exception.ResourceNotFoundException;
import com.example.movies.model.*;

import java.io.IOException;
import java.util.*;

import javax.validation.Valid;
/**
 * @author ronneyismael
 *
 */
@CrossOrigin(origins = "http://localhost:4200")
@RestController
@RequestMapping("/api/v1")
public class MoviesController {
	
	@Autowired
	MovieService movieServiceRef;
	@RequestMapping("/welcome")
	public String welcomeMovies() {
		return  "Hello Movies";
	}
	@RequestMapping("/movies")
	public List<Movies> getAllMovies(){
		return movieServiceRef.getAllMovies();
		
	}
	@RequestMapping("/movies/{moviesId}")
	public Movies getMoviesById(@PathVariable int moviesId) {
		
		return movieServiceRef.getMoviesById(moviesId);
	}
	
	@RequestMapping(method=RequestMethod.POST, value ="/movies")
	public void addMovies(@RequestBody Movies movies) throws IOException {
		movieServiceRef.addMovies(movies);
	}
	
	
	@RequestMapping(method=RequestMethod.PUT, value ="/movies/{moviesId}")
	public void updateMovies(@RequestBody Movies movies,@PathVariable int moviesId) throws IOException {
			movieServiceRef.updateMovies(movies,moviesId);
	}
	@RequestMapping(method= RequestMethod.DELETE,value="/movies/{moviesId}")
	public void deleteMovies(@PathVariable int moviesId) throws IOException {
		movieServiceRef.deleteMovies(moviesId);
	}
	
	@RequestMapping("/movies/search")
	public List<Movies> getMovies(@RequestParam(value = "query") String query) throws IOException, ParseException  {
		List<Movies> results = movieServiceRef.search(query);
		return results;
	}
	
	@RequestMapping(value="/movies/query",method = RequestMethod.GET)
	public Page<Movies> fetchByPage(Pageable page) {
		return movieServiceRef.findAllByPage(page);
	}
	
	@RequestMapping("/movies/dump")
	public String dumpMovies(){
		 movieServiceRef.dumpMovies();
		 
		 return "Success";
		
	}
	
	
	


}
