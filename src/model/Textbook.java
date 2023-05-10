package model;

import java.io.Serializable;

public class Textbook implements Serializable {
	private String title;
	private String isbn;
	private Name author;
	private double price;

	public Textbook(String title, String isbn, Name author, double price) {
		super();
		this.title = title;
		this.isbn = isbn;
		this.author = author;
		this.price = price;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getIsbn() {
		return isbn;
	}

	public void setIsbn(String isbn) {
		this.isbn = isbn;
	}

	public Name getAuthor() {
		return author;
	}

	public void setAuthor(Name author) {
		this.author = author;
	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "[" + title + "], [ISBN# = " + isbn + "], [Author = " + author + "], [Price = $" + price + "]";
	}

}
