package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Predicate;

public class TextbookBag implements Serializable {
	private Textbook[] bookArr;
	private int textnElems;
	
	public TextbookBag(int maxSize) {
		bookArr = new Textbook[maxSize];
	}
	
	public void insert(Textbook textbook) {
		bookArr[textnElems++] = textbook;
	}
	
	public void display() {
		for (int i = 0; i < textnElems; i++) {
			System.out.println(bookArr[i]);
		}
	}
	
	public int getTextnElems() {
		return textnElems;
	}
	
	public Textbook[] search(Predicate<Textbook> predicate) {
		Textbook[] foundArray = new Textbook[textnElems];
		int count = 0;
		for (int i = 0; i < textnElems; i++) {
			if (predicate.test(bookArr[i])) {
				foundArray[count++] = bookArr[i];
			}
		}
		return Arrays.copyOf(foundArray, count);
	}
	
	public Textbook[] delete(Predicate<Textbook> predicate) {
		Textbook[] toDelete = new Textbook[textnElems];
		int count = 0;
		for (int i = 0; i < textnElems; i++) {
			if (predicate.test(bookArr[i])) {
				toDelete[count++] = bookArr[i];
				for (int j = i; j < textnElems - 1; j++) {
					bookArr[j] = bookArr[j+1];
				}
				textnElems--;
				i--;
			} 
		}
		return Arrays.copyOf(toDelete, count);
	}
	
	public Textbook findByIsbn(String isbnString) {
		Textbook t = null;
		for (int i = 0; i < textnElems; i++) {
			if (bookArr[i].getIsbn().equals(isbnString)) {
				 return t = bookArr[i];
			}
		}
		return t;
	}

}
