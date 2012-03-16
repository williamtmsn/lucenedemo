package com.hascode.tutorial;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.Query;

import org.apache.lucene.index.Term;
import org.apache.lucene.search.TermQuery;
import org.hibernate.search.jpa.FullTextEntityManager;

public class Library {
	public static void main(final String... args) {
		// creating persistence context
		final EntityManagerFactory emf = Persistence
				.createEntityManagerFactory("hascode-local");
		final EntityManager em = emf.createEntityManager();
		final EntityTransaction tx = em.getTransaction();

		tx.begin();
		// creating some books to be indexed and persisted
		Book book1 = new Book();
		book1.setTitle("The big book of nothing");
		book1.setSummary("This is a book without any content");
		book1.setAuthor("fred");

		Book book2 = new Book();
		book2.setTitle("Exciting stories I");
		book2.setSummary("A compilation of exciting stories - part 1.");
		book2.setAuthor("selma");

		Book book3 = new Book();
		book3.setTitle("My life");
		book3.setSummary("A book about Fred's life.");
		book3.setAuthor("fred");

		em.persist(book1);
		em.persist(book2);
		em.persist(book3);
		tx.commit();

		// search using lucene
		FullTextEntityManager fullTextEntityManager = org.hibernate.search.jpa.Search
				.getFullTextEntityManager(em);

		org.apache.lucene.search.Query titleQuery = new TermQuery(new Term(
				"author", "fred"));
		javax.persistence.Query fullTextQuery = fullTextEntityManager
				.createFullTextQuery(titleQuery);

		System.out.println("searching using lucene..");
		List<Book> result = fullTextQuery.getResultList();
		printResults(result);

		// JPA search
		Query query = em
				.createQuery("SELECT b FROM Book b WHERE b.author=:author");
		query.setParameter("author", "fred");
		System.out.println("searching using JPA/JPQL..");
		result = query.getResultList();
		printResults(result);

		em.close();
		emf.close();
	}

	private static void printResults(final List<Book> result) {
		System.out.println(String.format("%s items found for author:fred",
				result.size()));
		for (Book b : result) {
			System.out.println("title: " + b.getTitle() + ", summary: "
					+ b.getSummary() + "(id: " + b.getId() + ")");
		}
	}
}
