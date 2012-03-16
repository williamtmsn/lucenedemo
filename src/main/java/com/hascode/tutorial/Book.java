package com.hascode.tutorial;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Lob;

import org.hibernate.search.annotations.Analyze;
import org.hibernate.search.annotations.Field;
import org.hibernate.search.annotations.Indexed;
import org.hibernate.search.annotations.Store;

@Entity
@Indexed(index = "indexes/books")
public class Book {
	private Long id;
	private String title;
	private String summary;
	private String author;

	@Id
	@GeneratedValue
	public Long getId() {
		return id;
	}

	@Field(name = "title", analyze = Analyze.YES, store = Store.NO)
	public String getTitle() {
		return title;
	}

	@Lob()
	@Field(name = "summary", analyze = Analyze.YES, store = Store.NO)
	public String getSummary() {
		return summary;
	}

	public void setTitle(final String title) {
		this.title = title;
	}

	public void setSummary(final String summary) {
		this.summary = summary;
	}

	public void setId(final Long id) {
		this.id = id;
	}

	public void setAuthor(final String author) {
		this.author = author;
	}

	@Field(name = "author", analyze = Analyze.NO, store = Store.NO)
	public String getAuthor() {
		return author;
	}

}
