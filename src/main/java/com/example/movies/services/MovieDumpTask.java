package com.example.movies.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import com.example.movies.model.Cast;
import com.example.movies.model.CastResponse;
import com.example.movies.model.Crew;
import com.example.movies.model.MovieResponse;
import com.example.movies.model.Movies;
import com.example.movies.model.MoviesDB;
import com.example.movies.model.TrailerLink;
import com.example.movies.model.TrailerLinkResponse;

public class MovieDumpTask implements Callable<List<Movies>> {

	private static final Logger log = LoggerFactory.getLogger(MovieDumpTask.class);

	private String MOVIE_BASE_URL = "https://api.themoviedb.org/3/movie";
	private String MOVIE_API_KEY = "99542193a43f32719300131d920bfa56";
	private String POPULAR_MOVIE_API = MOVIE_BASE_URL+"/popular?api_key="+MOVIE_API_KEY+"&sort_by=release_date.desc&page=";
	private RestTemplate restTemplate;
	private int pageCount;
	Map<Integer, String> genreMap;

	public MovieDumpTask(int pageCount, Map<Integer, String> genreMap) {
		super();
		this.restTemplate = new RestTemplate();
		this.genreMap = genreMap;
		this.pageCount = pageCount;
	}

	@Override
	public List<Movies> call() throws Exception {

		List<Movies> moviesList = new ArrayList<Movies>();
		String FETCH_MOVIE_URL = POPULAR_MOVIE_API + pageCount;
		//log.info(Thread.currentThread().getName()+" "+FETCH_MOVIE_URL);
		ResponseEntity<MovieResponse> moviesResp = restTemplate.exchange(FETCH_MOVIE_URL, HttpMethod.GET, null,
				new ParameterizedTypeReference<MovieResponse>() {
				});
		List<MoviesDB> movieBody = moviesResp.getBody().getResults();

		log.debug("FETCH_MOVIE_URL Response Size: " + movieBody.size());

		movieBody.forEach(m -> {
			try {

				Movies movies = new Movies();
				movies.setTitle(m.getTitle());
				movies.setOverview(m.getOverview());
				movies.setBackdrop_path(m.getBackdrop_path());
				movies.setPoster_path(m.getPoster_path());
				movies.setLanguage(m.getOriginal_language());
				movies.setRelease_date(m.getRelease_date());
				movies.setRatings(m.getVote_average());

				String genreName = "";
				for (Integer id : m.getGenre_ids()) {
					try {

						if (genreName.length() > 1) {
							genreName += ",";
						}
						genreName += genreMap.get(id);
					} catch (Exception e) {
						log.error(e.getMessage());
						continue;
					}
				}
				
				movies.setGenre(genreName);
				movies.setCast(getMovieCast(m.getId()));
				movies.setTrailer(getTrailerLink(m.getId()));

				moviesList.add(movies);

			} catch (Exception e) {
				log.error(e.getMessage());

			}
		});

		return moviesList;
	}

	public List<Cast> getMovieCast(int movie_id) {
		ResponseEntity<CastResponse> castResponse = restTemplate.exchange(
				MOVIE_BASE_URL +"/" + movie_id + "/credits?api_key="+MOVIE_API_KEY,
				HttpMethod.GET, null, new ParameterizedTypeReference<CastResponse>() {
				});

		Optional<Crew> crew = castResponse.getBody().getCrew().stream().filter(c -> "Director".equals(c.getJob())).findFirst();
		
				
		List<Cast> castList = castResponse.getBody().getCast().stream().skip(0).limit(4)
				.map(c -> new Cast(c.getName(), c.getCastCharacter(), c.getProfilePath())).collect(Collectors.toList());
		crew.ifPresent(c->{
			castList.add(new Cast(c.getName(), "Director", c.getProfilePath()));
		});
		
		

		return castList;
	}

	public TrailerLink getTrailerLink(int movie_id) {

		ResponseEntity<TrailerLinkResponse> trailerResponse = restTemplate.exchange(
				MOVIE_BASE_URL+"/" + movie_id + "/videos?api_key="+MOVIE_API_KEY,
				HttpMethod.GET, null, new ParameterizedTypeReference<TrailerLinkResponse>() {
				});

		Optional<TrailerLink> trailer = trailerResponse.getBody().getResults().stream().findFirst();
		//System.out.println("mov id" + movie_id + " trailer" + trailer.isPresent());

		return trailer.orElseGet(() -> new TrailerLink());

	}

}
