/*****************************************************************/
/* Copyright 2009 avajava.com                                    */
/* This code may be freely used and distributed in any project.  */
/* However, please do not remove this credit if you publish this */
/* code in paper or electronic form, such as on a web site.      */
/*****************************************************************/

package com.hascode.tutorial;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.util.Version;

public class LuceneDemo {

	public static final String FILES_TO_INDEX_DIRECTORY = "filesToIndex";
	public static final String INDEX_DIRECTORY = "indexDirectory";

	public static final String FIELD_PATH = "path";
	public static final String FIELD_CONTENTS = "contents";

	public static void main(String[] args) throws Exception {

		createIndex();
		searchIndex("mushrooms");
		searchIndex("steak");
		searchIndex("steak AND cheese");
		searchIndex("steak and cheese");
		searchIndex("bacon OR cheese");

	}

	public static void createIndex() throws CorruptIndexException, LockObtainFailedException, IOException {
		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
//		IndexWriter indexWriter = new IndexWriter(INDEX_DIRECTORY, analyzer, recreateIndexIfExists);
		
		FSDirectory dirIndex = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexWriterConfig config = new IndexWriterConfig(Version.LUCENE_34, analyzer);
		IndexWriter indexWriter = new IndexWriter(dirIndex, config);
		
		File dirFiles = new File(FILES_TO_INDEX_DIRECTORY);
		File[] files = dirFiles.listFiles();
		for (File file : files) {
			Document document = new Document();

			String path = file.getCanonicalPath();
			document.add(new Field(FIELD_PATH, path, Field.Store.YES, Field.Index.ANALYZED_NO_NORMS));

			Reader reader = new FileReader(file);
			document.add(new Field(FIELD_CONTENTS, reader));

			indexWriter.addDocument(document);
		}
		indexWriter.optimize();
		indexWriter.close();
	}

	public static void searchIndex(String searchString) throws IOException, ParseException {
		System.out.println("Searching for '" + searchString + "'");
//		Directory directory = FSDirectory.  getDirectory(INDEX_DIRECTORY);
		FSDirectory dirIndex = FSDirectory.open(new File(INDEX_DIRECTORY));
		IndexReader indexReader = IndexReader.open(dirIndex);
		IndexSearcher indexSearcher = new IndexSearcher(indexReader);

		Analyzer analyzer = new StandardAnalyzer(Version.LUCENE_34);
		QueryParser queryParser = new QueryParser(Version.LUCENE_34, FIELD_CONTENTS, analyzer);
		Query query = queryParser.parse(searchString);
		
		TopDocs docs = indexSearcher.search(query, 100);
		
		System.out.println("Number of hits: " + docs.totalHits);
		
		for (ScoreDoc scoreDoc : docs.scoreDocs) {
			Document document = indexSearcher.doc(scoreDoc.doc);
			String path = document.get(FIELD_PATH);
			System.out.println("Hit: " + path);
		}
	}
}

/**
 *  
 * According to the search results, we both have "mushrooms" listed but only I have "steak". 
 * Additionally, notice the use of "AND" versus "and". 
 * The uppercase "AND" results in a boolean query, 
 * which requires "steak" and "cheese" to both be in a document in order for it to be a hit. 
 * The lowercase "and" is treated as an irrelevant word, 
 * so "steak and cheese" is like searching for "steak cheese", 
 * which returns 2 hits since "steak" is found in one text file 
 * and "cheese" is found in the other text file.
 * 
 * */
