package util;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Random;
import java.util.Scanner;
import model.Instructor;
import model.Majors;
import model.Name;
import model.PersonBag;
import model.Ranks;
import model.Student;
import model.Textbook;
import model.TextbookBag;

public class Utilities {
	private static Random random = new Random();
	private static String firstNames = "dataFolder/FirstNames.txt";
	private static String lastNames = "dataFolder/LastNames.txt";
	private static String isbns = "dataFolder/textbook_isbns.txt";
	private static String titles = "dataFolder/textbook_titles.txt";

	private static String[] firstNameArr = makeArray(firstNames);
	private static String[] lastNameArr = makeArray(lastNames);
	private static String[] isbnArr = makeArray(isbns);
	private static String[] titleArr = makeArray(titles);
	public static String[][] titleAndIsbnArr = makeBookTitleIsbnArr(titleArr, isbnArr);
	private static Majors[] majorArr = Majors.values();
	private static Ranks[] ranksArr = Ranks.values();
	
	public static Name emitName() {
		String randomFirst = firstNameArr[random.nextInt(firstNameArr.length)];
		String randomLast = lastNameArr[random.nextInt(lastNameArr.length)];
		return new Name(randomFirst, randomLast);
	}
	
	public static String emitMajor() {
		String randomMajor = majorArr[random.nextInt(majorArr.length)] + "";
		return randomMajor;
	}
	
	public static double emitPrice() {
		double temp =  random.nextDouble(200);
		return Math.round(temp * 100) / 100.0;
	}
	
	public static double emitGpa() {
		double temp = random.nextDouble(4.0);
		return Math.round(temp * 100.0) / 100.0;
	}
	
	public static String emitRank() {
		String randomRank = ranksArr[random.nextInt(ranksArr.length)] + "";
		return randomRank;
	}
	
	public static Boolean existsMajor(String major) {
	    Boolean exists = false;
	    for (Majors m : majorArr) {
	        if (m.name().equalsIgnoreCase(major)) {
	            exists = true;
	            break;
	        }
	    }
	    return exists;
	}
	
	public static Boolean existsRank(String rank) {
	    Boolean exists = false;
	    for (Ranks r : ranksArr) {
	        if (r.name().equalsIgnoreCase(rank)) {
	            exists = true;
	            break;
	        }
	    }
	    return exists;
	}
	
	public static double emitSalary() {
		double temp = random.nextDouble(90000) + 10000;
		return Math.round(temp * 100.0) / 100.00;
	}
	
	public static String capitalize(String str) {
	    if(str == null || str.isEmpty()) {
	        return str;
	    }
	    return str.substring(0, 1).toUpperCase() + str.substring(1);
	}
	
	public static Boolean possibleIsbn(String string) {
		boolean valid = false;
		if (string.length() < 10 || string.length() > 17) {
			return valid = false;			
		}
		for (int i = 0; i < string.length(); i++) {
			if (Character.isDigit(string.charAt(i)) || string.charAt(i) == '-') {
				valid = true;
			}
			else {return valid = false;}
		}
		return valid;
	}
	
	public static String[][] makeBookTitleIsbnArr(String[] titles1, String[] isbns1) {
		String[][] arr = new String[titles1.length][2];
		for (int i = 0; i < titles1.length; i++) {
			arr[i][0] = titles1[i];
			arr[i][1] = isbns1[i];
		}
		return arr;
	}
	
	public static String[] emitTitleAndIsbn() {
		int rando = (int) (Math.random() * titleAndIsbnArr.length);
		String[] book = titleAndIsbnArr[rando];
		return book;		
	}
	
	public static void importBooks(TextbookBag textbookBag) {
		for (int i = 0; i < titleAndIsbnArr.length; i++) {
			Textbook book = new Textbook(titleAndIsbnArr[i][0], titleAndIsbnArr[i][1], emitName(), emitPrice());
			textbookBag.insert(book);
		}
	}
	
	public static void importStudents(PersonBag personBag) {
		for (int i = 0; i < 1000; i++) {
			Student student = new Student(emitName(), emitGpa(), emitMajor());
			personBag.insert(student);
		}
	}
	
	public static void importInstructors(PersonBag personBag) {
		for (int i = 0; i < 500; i++) {
			Instructor instructor = new Instructor(emitName(), emitRank(), emitSalary());
			personBag.insert(instructor);
		}
	}
	
	public static String[] makeArray(String fileName) {
		File file = new File(fileName);
		Scanner scanner = null;
		try {
			scanner = new Scanner(file, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		int lineCount = 0;
		while (scanner.hasNextLine()) {
			scanner.nextLine();
			lineCount++;
		}
		scanner.close();
		String[] arr = new String[lineCount];
		try {
			scanner = new Scanner(file, "UTF-8");
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		
		lineCount = 0;
		while(scanner.hasNextLine()) {
			arr[lineCount++] = scanner.nextLine();
		}
		scanner.close();
		return arr;
	}
	
}
