package util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import model.PersonBag;
import model.TextbookBag;

public class Backup {
	
	public static void backupPersonBag(PersonBag personBag, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(personBag);
			oos.writeObject(personBag.getnElems());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void backupTextbookBag(TextbookBag textbookBag, File file) {
		try {
			FileOutputStream fos = new FileOutputStream(file);
			ObjectOutputStream oos = new ObjectOutputStream(fos);
			oos.writeObject(textbookBag);
			oos.writeObject(textbookBag.getTextnElems());
			oos.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
