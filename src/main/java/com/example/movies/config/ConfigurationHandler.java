package com.example.movies.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;


@Component
public class ConfigurationHandler {
        @Autowired
	private Environment env;
	
	/**
	 * document.index.dir is where the indexed data will be present
	 */
	public String getIndexFolder() {
		return env.getProperty("document.index.dir");
	}
    
}
