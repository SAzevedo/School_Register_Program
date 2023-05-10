package app;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import java.io.File;
import model.PersonBag;
import model.TextbookBag;
import view.InstructorView;
import view.PaneView;
import view.StudentView;
import view.TextbookView;
import util.Restore;
import util.Backup;
import util.Utilities;

public class Demo extends Application {

	public static void main(String[] args) {
		launch(args);
						
	}

	@Override
	public void start(Stage primaryStage) {
		
		PersonBag personBag;
		TextbookBag textbookBag;
		File personFile = new File("backupFolder/PersonBag.dat");
		File textbookFile = new File("backupFolder/TextbookBag.dat");
		
		
//		Automatic Import/Backup Or Restore
		if (!new File("backupFolder/TextbookBag.dat").exists()) {
			textbookBag = new TextbookBag(50000);
			Utilities.importBooks(textbookBag);
			Backup.backupTextbookBag(textbookBag, textbookFile);
			textbookBag.display();
		} else {
			textbookBag = Restore.restoreTextbookBag(textbookFile);
			textbookBag.display();
		}
		if (!new File("backupFolder/PersonBag.dat").exists()) {
			personBag = new PersonBag(20000);
			Utilities.importStudents(personBag);
			Utilities.importInstructors(personBag);
			Backup.backupPersonBag(personBag, personFile);
			personBag.display();
		}
		else {
			personBag = Restore.restorePersonBag(personFile);
			personBag.display();
		}
		
//		Creating the Scene
		StudentView sView = new StudentView(personBag);
		InstructorView iView = new InstructorView(personBag);
		TextbookView tView = new TextbookView(textbookBag);
		BorderPane bView = new BorderPane();
		PaneView pView = new PaneView(bView, personBag, textbookBag, sView, iView, tView);
		Scene scene = new Scene(pView.getBorderPane(), 900, 600);
				
//		Performing on Stage
		primaryStage.setTitle("Azevedo Final Project");
		primaryStage.setScene(scene);
		primaryStage.show();

	}

}