package com.example.movies.services;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
import com.example.movies.model.Cast;
import com.example.movies.model.CastResponse;
import com.example.movies.model.Genre;
import com.example.movies.model.GenreResponse;
import com.example.movies.model.MovieResponse;
import com.example.movies.model.Movies;
import com.example.movies.model.MoviesDB;
import com.example.movies.model.TrailerLink;
import com.example.movies.model.TrailerLinkResponse;

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
	
	@Autowired
	private MoviesDao daoRef;
	@Autowired
	private SearchEngineService searchEngine;

	@Override
	public List<Movies> getAllMovies() {
		// TODO Auto-generated method stud
		return daoRef.findAll();
	}

	@Override
	public Movies getMoviesById(int moviesId) {
		// TODO Auto-generated method stub
		return daoRef.getOne(moviesId);
	}

	@Override
	public void addMovies(Movies movies) throws IOException {
		// TODO Auto-generated method stub
		System.out.println("cast"+movies.getCast().get(0).getId());
		daoRef.save(movies);
		searchEngine.addToIndex(movies);
	}

	@Override
	public void updateMovies(Movies movies, int moviesId) throws IOException {
		// TODO Auto-generated method stub

		daoRef.save(movies);
		searchEngine.deleteFromIndex(movies);
		searchEngine.addToIndex(movies);



	}

	@Override
	public void deleteMovies(int moviesId) throws IOException {
		// TODO Auto-generated method stub

		Movies movies=daoRef.findById(moviesId).get();
		daoRef.deleteById(moviesId);
		searchEngine.deleteFromIndex(movies);



	}

	@Override
	public List<Movies> search(String query) throws IOException, ParseException {
		// TODO Auto-generated method stub
		return searchEngine.query(query);
	}
	
	public Page<Movies> findAllByPage(Pageable page){
		return daoRef.findAll(page);
		
	}
	
	
	public Map<Integer, String> getGenre(){

		ResponseEntity<GenreResponse> genreList= 	restTemplate.exchange(
				"https://api.themoviedb.org/3/genre/movie/list?api_key=99542193a43f32719300131d920bfa56", 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<GenreResponse>() {});
		
		Map<Integer, String> genresResult = genreList.getBody().getGenres().stream().collect(
                Collectors.toMap(Genre::getId, Genre::getName));
		
		return genresResult;
		
		
	}
	
	
	public List<Cast> getMovieCast(int movie_id) {
		ResponseEntity<CastResponse> castList= 	restTemplate.exchange(
				"https://api.themoviedb.org/3/movie/"+movie_id+"/credits?api_key=99542193a43f32719300131d920bfa56", 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<CastResponse>() {});
		
	
		return castList.getBody().getCast().stream().skip(0).limit(4)
				.map(c->new Cast(c.getName(),c.getCastCharacter(),c.getProfilePath()))
				.collect(Collectors.toList());
	}
	
	
	public TrailerLink getTrailerLink(int movie_id) {
		
		ResponseEntity<TrailerLinkResponse> trailerResponse= 	restTemplate.exchange(
				"http://api.themoviedb.org/3/movie/"+movie_id+"/videos?api_key=99542193a43f32719300131d920bfa56", 
				HttpMethod.GET,
				null,
				new ParameterizedTypeReference<TrailerLinkResponse>() {});
		
		Optional<TrailerLink> trailer = trailerResponse.getBody().getResults().stream().findFirst();
		System.out.println("mov id"+movie_id+" trailer"+trailer.isPresent());
	
		return trailer.orElseGet(()-> new TrailerLink());
		
	}
	
	
	
	
	
	
	public void dumpMovies() {
	
			Map<Integer, String> genreMap = getGenre();
			for(int i=1;i<500;i++) {
				
				try {
					
				
				
			
		ResponseEntity<MovieResponse> moviesList= 	restTemplate.exchange(
					"https://api.themoviedb.org/3/movie/popular?api_key=99542193a43f32719300131d920bfa56&sort_by=release_date.desc&page="+i, 
					HttpMethod.GET,
					null,
					new ParameterizedTypeReference<MovieResponse>() {});
			List<MoviesDB> movieBody=moviesList.getBody().getResults();
			log.info("request movie Body"+ movieBody);
			
			movieBody.forEach(m->{
				try {
					log.info("title"+ m.getTitle());
					log.info("poster_path"+ m.getPoster_path());

					log.info("backdrop_path"+ m.getBackdrop_path());

					Movies movies = new Movies();
					movies.setTitle(m.getTitle());
					movies.setOverview(m.getOverview());
					movies.setBackdrop_path(m.getBackdrop_path());
					movies.setPoster_path(m.getPoster_path());
					movies.setLanguage(m.getOriginal_language());
					movies.setRelease_date(m.getRelease_date());
					movies.setRatings(m.getVote_average());
		
					
					String genreName="";
					for(Integer id: m.getGenre_ids()) {
						try {
							
						
						if(genreName.length()>1)
						{
							genreName+=",";
						}
						 genreName+= genreMap.get(id);
						}catch(Exception e) {
							continue;
						}
					}
					log.info("Genre Name"+ genreName);

					movies.setGenre(genreName);
					movies.setCast(getMovieCast(m.getId()));
					movies.setTrailer(getTrailerLink(m.getId()));
					

					addMovies(movies);
					
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
//					continue;

				}
			});
				}catch(Exception e) {
					continue;
				}
		
		}

	
		//hasmap for

	}
	
	
	
}
