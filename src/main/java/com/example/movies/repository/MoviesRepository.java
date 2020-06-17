package com.example.movies.repository;

import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import com.example.movies.model.Movies;

@Repository
public interface MoviesRepository extends JpaRepository<Movies, Integer>,PagingAndSortingRepository<Movies, Integer> {

	@Override
	 Page<Movies> findAll(Pageable pageable);
}
