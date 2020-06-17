package com.example.movies.services;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.movies.dao.MoviesDao;
import com.example.movies.model.Genre;
import com.example.movies.model.GenreResponse;
import com.example.movies.model.Movies;

/**
 * @author ronneyismael
 *
 */

@Service
public class MoviesServiceImpl implements MovieService {
	private static final Logger log = LoggerFactory.getLogger(SearchEngineService.class);

//	List<Employee> empList=new ArrayList<>((Arrays.asList(
//				new Employee(1,"Ronney","Informatik"),
//				new Employee(2,"Prajjwal","Mechatronics"),
//				new Employee(3,"Shivam","Mechanical"))));

	@Autowired
	private RestTemplate restTemplate;

	int pageCount = 1;

	@Autowired
	private MoviesDao moviesRepo;
	@Autowired
	private SearchEngineService searchEngine;

	@Override
	public List<Movies> getAllMovies() {
		// TODO Auto-generated method stud
		return moviesRepo.findAll();
	}

	@Override
	public Movies getMoviesById(int moviesId) {
		// TODO Auto-generated method stub
		return moviesRepo.getOne(moviesId);
	}

	@Override
	public void addMovies(Movies movies) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("cast" + movies.getCast().get(0).getId());
		moviesRepo.save(movies);
		searchEngine.addToIndex(movies);
	}

	@Override
	public void updateMovies(Movies movies, int moviesId) throws IOException {
		// TODO Auto-generated method stub

		moviesRepo.save(movies);
		searchEngine.deleteFromIndex(movies);
		searchEngine.addToIndex(movies);

	}

	@Override
	public void deleteMovies(int moviesId) throws IOException {
		// TODO Auto-generated method stub

		Movies movies = moviesRepo.findById(moviesId).get();
		moviesRepo.deleteById(moviesId);
		searchEngine.deleteFromIndex(movies);

	}

	@Override
	public List<Movies> search(String query) throws IOException, ParseException {
		// TODO Auto-generated method stub
		return searchEngine.query(query);
	}

	public Page<Movies> findAllByPage(Pageable page) {
		return moviesRepo.findAll(page);

	}

	public Map<Integer, String> getGenre() {

		ResponseEntity<GenreResponse> genreList = restTemplate.exchange(
				"https://api.themoviedb.org/3/genre/movie/list?api_key=99542193a43f32719300131d920bfa56",
				HttpMethod.GET, null, new ParameterizedTypeReference<GenreResponse>() {
				});

		Map<Integer, String> genresResult = genreList.getBody().getGenres().stream()
				.collect(Collectors.toMap(Genre::getId, Genre::getName));

		return genresResult;

	}

	public void dumpMovies() {
		log.info("Fetching Movies from movie DB API");
		try {
			moviesRepo.saveAll(getMoviesFromAPI());
		} catch (Exception e) {
			log.error(e.getMessage());
		}

		log.info("ReIndexing Movies from local DB");
		reIndexMovies();
	}

	private void reIndexMovies() {
		List<Movies> movieList = moviesRepo.findAll();
		searchEngine.deleteAllFromIndex();
		for (Movies movie : movieList) {
			try {
				searchEngine.addToIndex(movie);
			} catch (IOException e) {
				log.error(e.getMessage());
			}
		}
	}

	private List<Movies> getMoviesFromAPI() {

		List<Movies> moviesList = new ArrayList<Movies>();

		// Thread Pool
		ExecutorService pool = Executors.newFixedThreadPool(200);

		// Callable Job to fetch movie
		List<Callable<List<Movies>>> futureList = new ArrayList<Callable<List<Movies>>>();

		// Fetch Movie Genre
		Map<Integer, String> genreMap = getGenre();

		// Create MovieDumpTask
		for (int i = 1; i < 200; i++) {
			try {
				futureList.add(new MovieDumpTask(i, genreMap));
			} catch (Exception e) {
				log.error(e.getMessage());
			}
		}
		

		try {
			List<Future<List<Movies>>> results = pool.invokeAll(futureList);
			for (Future<List<Movies>> f : results) {
				try {
					moviesList.addAll(f.get());
				} catch (ExecutionException e) {
					log.error(e.getMessage());
				}
			}
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		}

		// shut down the executor service now
		pool.shutdown();
		log.info("TOTAL Movie" + moviesList.size());
		return moviesList;
	}

}