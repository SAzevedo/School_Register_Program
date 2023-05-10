package view;

import java.io.File;
import javafx.application.Platform;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.InnerShadow;
import javafx.scene.layout.BorderPane;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import model.NoFileSelectedException;
import model.Person;
import model.PersonBag;
import model.TextbookBag;
import util.Backup;
import util.Restore;
import util.Utilities;

public class PaneView {
	private BorderPane borderPane;
	private PersonBag personBag;
	private TextbookBag textbookBag;
	private StudentView studentView;
	private InstructorView instructorView;
	private TextbookView textbookView;
	private FileChooser datFileChooser;
	
	public PaneView(BorderPane bP, PersonBag pB, TextbookBag tB, StudentView sV, 
			InstructorView iV, TextbookView tV) {
		this.borderPane = bP;
		this.personBag = pB;
		this.textbookBag = tB;
		this.studentView = sV;
		this.instructorView = iV;
		this.textbookView = tV;

		MenuBar menuBar = new MenuBar();

		Menu fileMenu = new Menu("File");
		Menu editMenu = new Menu("Change View");
		
		MenuItem studentMenu = new MenuItem("Student View");
		MenuItem instructorMenu = new MenuItem("Instructor View");
		MenuItem textbookMenu = new MenuItem("Textbook View");
		MenuItem exitItem = new MenuItem("Exit");
		MenuItem importTextbookBag = new MenuItem("Import a TextBookBag");
		MenuItem importPersonBag = new MenuItem("Import a PersonBag");
		MenuItem backupPersonBag = new MenuItem("Backup PersonBag");
		MenuItem backupTextbookBag = new MenuItem("Backup TextbookBag");
		MenuItem restorePersonBag = new MenuItem("Restore a PersonBag");
		MenuItem restoreTextbookBag = new MenuItem("Restore a TextbookBag");
		
		SeparatorMenuItem separator1 = new SeparatorMenuItem();
		SeparatorMenuItem separator2 = new SeparatorMenuItem();
		DropShadow ds = new DropShadow();
		InnerShadow is = new InnerShadow();
		menuBar.setEffect(ds);
		menuBar.setEffect(is);
		
		editMenu.getItems().addAll(studentMenu, instructorMenu, textbookMenu);
		fileMenu.getItems().addAll(importPersonBag, importTextbookBag, separator1, backupPersonBag,
				backupTextbookBag, restorePersonBag, restoreTextbookBag, separator2, exitItem);
		menuBar.getMenus().addAll(fileMenu, editMenu);

		importPersonBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select file location to save the PersonBag");
			datFileChooser.setInitialDirectory(new File("backupFolder/"));
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter(".dat", "*.dat"));
			File selectedFile = datFileChooser.showSaveDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Create a new File or Select a File to save Import"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			this.personBag = new PersonBag(2000);
			Person.setIdCount(1);
			Utilities.importStudents(this.personBag);
			Utilities.importInstructors(this.personBag);
			StudentView.setPersonBag(this.personBag);
			StudentView.setFile(selectedFile);
			InstructorView.setPersonBag(this.personBag);
			InstructorView.setFile(selectedFile);
			this.personBag.display();
			Backup.backupPersonBag(this.personBag, selectedFile);
		});
		
		importTextbookBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select file location to save the TextbookBag");
			datFileChooser.setInitialDirectory(new File("backupFolder/"));
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter(".dat", "*.dat"));
			File selectedFile = datFileChooser.showSaveDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Create a new File or Select a File to save Import"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			this.textbookBag = new TextbookBag(40000);
			Utilities.importBooks(this.textbookBag);
			TextbookView.setTextbookBag(this.textbookBag);
			TextbookView.setFile(selectedFile);
			textbookBag.display();
			Backup.backupTextbookBag(this.textbookBag, selectedFile);
		});
		
		backupPersonBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select a file location to backup PersonBag");
			datFileChooser.setInitialDirectory(new File("backupFolder/"));
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter(".dat", "*.dat"));
			File selectedFile = datFileChooser.showSaveDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Create a new File or Select a File for the Backup"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			Backup.backupPersonBag(this.personBag, selectedFile);
			System.out.println("PersonBag has been backed up.");
		});
		
		backupTextbookBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select a file location to backup TextbookBag");
			datFileChooser.setInitialDirectory(new File("backupFolder/"));
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter("TextbookBag.dat", "*.dat"));
			File selectedFile = datFileChooser.showSaveDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Create a new File or Select a File for the Backup"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			Backup.backupTextbookBag(this.textbookBag, selectedFile);
			System.out.println("TextbookBag has been backed up.");
		});
		
		restorePersonBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select a file to restore a PersonBag from.");
			File initialDir = new File("backupFolder/");
			datFileChooser.setInitialDirectory(initialDir);
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter("PersonBag.dat", "*.dat"));
			File selectedFile = datFileChooser.showOpenDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Select a File to Restore a PersonBag"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			this.personBag = Restore.restorePersonBag(selectedFile);
			StudentView.setPersonBag(this.personBag);
			StudentView.setFile(selectedFile);
			InstructorView.setPersonBag(this.personBag);
			InstructorView.setFile(selectedFile);
			personBag.display();
			System.out.println("PersonBag has been restored");
		});
		
		restoreTextbookBag.setOnAction(e -> {
			datFileChooser = new FileChooser();
			datFileChooser.setTitle("Select a file to restore a TextbookBag from.");
			File initialDir = new File("backupFolder/");
			datFileChooser.setInitialDirectory(initialDir);
			datFileChooser.getExtensionFilters().addAll(new ExtensionFilter("TextbookBag.dat", "*.dat"));
			File selectedFile = datFileChooser.showOpenDialog(null);
			while (true) {
				try {
					if (selectedFile == null) {
						throw new NoFileSelectedException("Select a File to Restore a TextbookBag"); 
					}
					break;
				}
				catch (NoFileSelectedException x) {
					System.out.println(x.getMessage());
					selectedFile = datFileChooser.showSaveDialog(null);
				}
			}
			
			this.textbookBag = Restore.restoreTextbookBag(selectedFile);
			TextbookView.setTextbookBag(this.textbookBag);
			TextbookView.setFile(selectedFile);
			textbookBag.display();
			System.out.println("TextbookBag has been restored");
		});
		
		studentMenu.setOnAction(e -> {
			borderPane.setCenter(studentView.getStudentPane());
		});
		
		instructorMenu.setOnAction(e -> {
			borderPane.setCenter(instructorView.getInstructorPane());
		});
		
		textbookMenu.setOnAction(e -> {
			borderPane.setCenter(textbookView.getTextbookPane());
		});

		exitItem.setOnAction(e -> {
			Platform.exit();
		});

		borderPane.setTop(menuBar);
		borderPane.setCenter(studentView.getStudentPane());

	}

	public BorderPane getBorderPane() {
		return borderPane;
	}
	
}