package util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import model.Person;
import model.PersonBag;
import model.TextbookBag;

public class Restore {
	
	public static PersonBag restorePersonBag(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			PersonBag personBag = (PersonBag) ois.readObject();
			Person.setIdCount((Integer)ois.readObject());
			ois.close();
			return personBag;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static TextbookBag restoreTextbookBag(File file) {
		try {
			FileInputStream fis = new FileInputStream(file);
			ObjectInputStream ois = new ObjectInputStream(fis);
			TextbookBag textbookBag = (TextbookBag) ois.readObject();
			ois.close();
			return textbookBag;
		} catch (IOException | ClassNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
