package com.tssoft.one.webservice.model;

import java.util.ArrayList;
import java.util.List;

public class ArticleBySubject {
	public String subject;
	public List<Article> articles = new ArrayList<Article>();

	public ArticleBySubject(String subject, List<Article> articles) {
		this.subject = subject;
		this.articles = articles;
	}
}
