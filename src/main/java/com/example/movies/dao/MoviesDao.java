package com.example.movies.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.movies.model.Movies;

@Repository
public interface MoviesDao extends JpaRepository<Movies, Integer> {

}
