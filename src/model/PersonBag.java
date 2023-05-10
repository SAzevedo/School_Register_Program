package model;

import java.io.Serializable;
import java.util.Arrays;
import java.util.function.Predicate;

public class PersonBag implements Serializable {
	private Person[] arr;
	private int nElems;
		
	public PersonBag(int maxSize) {
		arr = new Person[maxSize];
	}
	
	public void insert(Person person) {
		arr[nElems++] = person;
	}
	
	public void display() {
		for (int i = 0; i < nElems; i++) {
			System.out.println(arr[i]);
		}
		System.out.println();
	}
	
	public int getnElems() {
		return nElems;
	}
	
	public Person[] search(Predicate<Person> predicate) {
		Person[] foundArray = new Person[nElems];
		int count = 0;
		for (int i = 0; i < nElems; i++) {
			if (predicate.test(arr[i])) {
				foundArray[count++] = arr[i];
			} 
		}
		return Arrays.copyOf(foundArray, count);
	}
	
	public Person[] delete(Predicate<Person> predicate) {
		Person[] toDelete = new Person[nElems];
		int count = 0;
		for (int i = 0; i < nElems; i++) {
			if (predicate.test(arr[i])) {
				toDelete[count++] = arr[i];
				for (int j = i; j < nElems - 1; j++) {
					arr[j] = arr[j+1];
				}
				nElems--;
				i--;
			} 
		}
		return Arrays.copyOf(toDelete, count);
	}
	
	public Person findById(String idString) {
		Person p = null;
		for (int i = 0; i < nElems; i++) {
			if (arr[i].getId().equals(idString)) {
				return p = arr[i];
			}
		}
		return p;
	}
		

}
