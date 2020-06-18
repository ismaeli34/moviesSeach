package com.example.movies.services;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.MultiFieldQueryParser;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.movies.model.Cast;
import com.example.movies.model.Movies;

/**
 * @author ronneyismael
 *
 */

@Service
public class SearchEngineService {
	private static final Logger log = LoggerFactory.getLogger(SearchEngineService.class);

	final String SRCH_FIELD = "content";
	final int TOP_HITS = 100;

	private IndexWriter indexWriter;

	@Value("${document.index.dir}")
	private String indexFolder;

	@Autowired
	public SearchEngineService(IndexWriter indexWriter) {
		super();
		this.indexWriter = indexWriter;

	}

	public List<Movies> query(String query) throws IOException, ParseException {

		IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexFolder)));

		IndexSearcher indexSearcher = new IndexSearcher(indexReader);
		MultiFieldQueryParser qp = new MultiFieldQueryParser(
				new String[] { "title", "id", "director", "language", "genre", "release_date", "ratings", "cast",
						"poster_path", "backdrop_path", "overview", "vote_average" },
				new StandardAnalyzer());

		qp.setDefaultOperator(MultiFieldQueryParser.Operator.AND);

		TopDocs results = indexSearcher.search(qp.parse(query), 100);

		log.info("Results   ---" + results.scoreDocs.length);
		ScoreDoc[] scoreDocs = results.scoreDocs;
		log.info("Score docs---" + scoreDocs);
		int result = 0;
		List<Movies> movies = new ArrayList<>();
		log.info("Results Document---" + result);

		for (ScoreDoc scoreDoc : scoreDocs) {
			log.info("Documents---" + scoreDoc.doc);
			Document doc = indexSearcher.doc(scoreDoc.doc);
			log.info("Doc---" + doc);

			Movies movie = new Movies();
			movie.setTitle(doc.get("title"));
			movie.setId(Integer.parseInt(doc.get("id")));
			movie.setDirector(doc.get("director"));
			movie.setLanguage(doc.get("language"));
			movie.setGenre(doc.get("genre"));
			movie.setRelease_date(doc.get("release_date"));
			movie.setRatings(doc.get("ratings"));
			movie.setPoster_path(doc.get("poster_path"));
			movie.setBackdrop_path(doc.get("backdrop_path"));
			movie.setOverview(doc.get("overview"));
//			movie.setVote_average(Integer.parseInt(doc.get("vote_average")));

			String castList[] = doc.get("cast").split(":");
			// doc.ge
			System.out.println(castList);
			for (String cast : castList) {
				String castObj[] = cast.split(",");
				System.out.println(castObj.length);
				movie.getCast().add(new Cast(castObj[0], castObj[1], castObj[2]));
			}

			movies.add(movie);
			// log.info("Doc title---" + movies);

		}

//	    Dictionary dictionary = new LuceneDictionary(indexReader, "content");
//	    AnalyzingInfixSuggester analyzingSuggester = new AnalyzingInfixSuggester(index, new StandardAnalyzer());
//	    analyzingSuggester.build(dictionary);
//	    List<LookupResult> lookupResultList = analyzingSuggester.lookup(c, false, 10);
//        log.info("\"Look up result size ::---" + lookupResultList);
//	    for (LookupResult lookupResult : lookupResultList) {
//	         log.info("Analyzing suggestor key---" + lookupResult.key);
//	         log.info("Analyzing suggestor value---" + lookupResult.value);
//
//	    }
		return movies;
	}

	public void deleteFromIndex(Movies movie) throws IOException {
		boolean isDeleted;
		Term idTerm = new Term("id", String.valueOf(movie.getId()));
		log.info("Before Deleted " + movie.getId());
		indexWriter.deleteDocuments(idTerm);
//		indexWriter.deleteAll();
		indexWriter.commit();
		isDeleted = indexWriter.hasDeletions();
		log.info("Is Deleted" + isDeleted);
		log.info("num docs" + indexWriter.numDocs());
		log.info("Deleted " + movie.getId());

	}

	public void addToIndex(Movies movie) throws IOException {
		if (indexWriter.isOpen())
//			indexWriter.deleteUnusedFiles();

			log.info("adding movies to index" + movie);
		try {
			Document doc = new Document();
			Field titleField = new TextField("title", movie.getTitle(), Field.Store.YES);
			doc.add(titleField);
			doc.add(new TextField("id", String.valueOf(movie.getId()), Field.Store.YES));
			// Field directorField = new TextField("director", movie.getDirector(),
			// Field.Store.YES);
			// doc.add(directorField);

			Field languageField = new TextField("language", movie.getLanguage(), Field.Store.YES);
			doc.add(languageField);

			Field genreField = new TextField("genre", movie.getGenre(), Field.Store.YES);
			doc.add(genreField);

			Field releaseDateField = new TextField("release_date",
					movie.getRelease_date() == null ? "" : movie.getRelease_date(), Field.Store.YES);
			doc.add(releaseDateField);

			Field ratingsField = new TextField("ratings", movie.getRatings(), Field.Store.YES);
			doc.add(ratingsField);

			Field posterPathField = new TextField("poster_path",
					movie.getPoster_path() == null ? "" : movie.getPoster_path(), Field.Store.YES);
			doc.add(posterPathField);

			Field backDropPathField = new TextField("backdrop_path",
					movie.getBackdrop_path() == null ? "" : movie.getBackdrop_path(), Field.Store.YES);
			doc.add(backDropPathField);

			Field overviewField = new TextField("overview", movie.getOverview(), Field.Store.YES);
			doc.add(overviewField);

			String castList = "";
			for (Cast cast : movie.getCast()) {
				if (castList.length() > 1) {
					castList += " : ";
				}
				castList += cast.getName() + "," + cast.getCastCharacter() + "," + cast.getProfilePath();

			}
			doc.add(new TextField("cast", castList, Field.Store.YES));

			StringBuilder movieContent = new StringBuilder();
			movieContent.append(movie.getTitle() + " ");
			movieContent.append(movie.getLanguage() + " ");
			movieContent.append(movie.getGenre() + " ");
			movieContent.append(movie.getRelease_date() + " ");
			movieContent.append(castList + " ");

			doc.add(new TextField(SRCH_FIELD, movieContent.toString(), Field.Store.YES));

			indexWriter.addDocument(doc);

		} catch (IOException e) {
			log.error("Failed to index movie" + e.getMessage());
		} finally {
			try {
				indexWriter.commit();
//					indexWriter.close();
			} catch (IOException e) {
				log.error("" + e.getMessage());
			}
		}

	}

	public List<Movies> search(String srchTxt) {

		List<Movies> movies = new ArrayList<>();

		try {
			IndexReader indexReader = DirectoryReader.open(FSDirectory.open(Paths.get(indexFolder)));

			IndexSearcher indexSearcher = new IndexSearcher(indexReader);

			QueryParser qp = new QueryParser(SRCH_FIELD, new StandardAnalyzer());

			TopDocs topDocs = indexSearcher.search(qp.parse(srchTxt), TOP_HITS);

			for (ScoreDoc scoreDoc : topDocs.scoreDocs) {
				Document doc = indexSearcher.doc(scoreDoc.doc);
				Movies movie = new Movies();
				movie.setTitle(doc.get("title"));
				movie.setId(Integer.parseInt(doc.get("id")));
				movie.setDirector(doc.get("director"));
				movie.setLanguage(doc.get("language"));
				movie.setGenre(doc.get("genre"));
				movie.setRelease_date(doc.get("release_date"));
				movie.setRatings(doc.get("ratings"));
				movie.setPoster_path(doc.get("poster_path"));
				movie.setBackdrop_path(doc.get("backdrop_path"));
				movie.setOverview(doc.get("overview"));

				String castList[] = doc.get("cast").split(":");

				for (String cast : castList) {
					String castObj[] = cast.split(",");
					movie.getCast().add(new Cast(castObj[0], castObj[1], castObj[2]));
				}

				movies.add(movie);
			}

		} catch (IOException e) {
			log.error("Index DIR not found: " + e.getMessage());
		} catch (ParseException e) {
			log.error("search query parse failed. " + e.getMessage());
		}
		return movies;
	}

	public void deleteAllFromIndex() {

		try {
			indexWriter.deleteAll();
			indexWriter.commit();
			log.info("Is Deleted" + indexWriter.hasDeletions() + " num docs" + indexWriter.numDocs());
		} catch (IOException e) {
			log.error(e.getMessage());
		}

	}

}
