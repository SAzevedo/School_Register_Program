package view;

import java.io.File;
import java.util.Optional;
import java.util.function.Predicate;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputDialog;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import model.Name;
import model.Person;
import model.PersonBag;
import model.Student;
import util.Backup;
import util.Utilities;

public class StudentView {
	private static PersonBag personBag;
	private static File file;
	ObservableList<Person> watcher;
	ChangeListener<Person> listener;
	private VBox studentPane;
	

	public StudentView(PersonBag pBag) {
		personBag = pBag;
		file = new File("backupFolder/PersonBag.dat");
		
		Button insertBtn = new Button("INSERT");
		insertBtn.setPrefSize(100, 30);

		Button searchBtn = new Button("SEARCH");
		searchBtn.setPrefSize(100, 30);

		Button updateBtn = new Button("UPDATE");
		updateBtn.setPrefSize(100, 30);

		Button removeBtn = new Button("REMOVE");
		removeBtn.setPrefSize(100, 30);

		Button exitBtn = new Button("EXIT");
		exitBtn.setPrefSize(100, 30);
		
		Button clearBtn = new Button("CLEAR");
		clearBtn.setPrefSize(100, 30);

		Text title = new Text("Student Database");
		title.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		title.setFill(Color.HONEYDEW);
		
		DropShadow ds = new DropShadow();
		title.setEffect(ds);
		insertBtn.setEffect(ds);
		searchBtn.setEffect(ds);
		updateBtn.setEffect(ds);
		removeBtn.setEffect(ds);
		exitBtn.setEffect(ds);
		clearBtn.setEffect(ds);

		HBox btnBox = new HBox(30);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.getChildren().addAll(insertBtn, searchBtn, updateBtn, removeBtn, clearBtn);

		TextField firstNameField = new TextField();
		firstNameField.setPromptText("FIRST NAME");
		firstNameField.setPrefSize(150, 30);
		firstNameField.setEffect(ds);
		
		TextField lastNameField = new TextField();
		lastNameField.setPromptText("LAST NAME");
		lastNameField.setPrefSize(150, 30);
		lastNameField.setEffect(ds);

		TextField gpaField = new TextField();
		gpaField.setPromptText("GPA");
		gpaField.setPrefSize(60, 30);
		gpaField.setEffect(ds);

		TextField majorField = new TextField();
		majorField.setPromptText("MAJOR");
		majorField.setPrefSize(60, 30);
		majorField.setEffect(ds);

		TextField idField = new TextField();
		idField.setPromptText("ID");
		idField.setPrefSize(60, 30);
		idField.setEffect(ds);

		HBox inputBox = new HBox(20);
		inputBox.setAlignment(Pos.CENTER);
		inputBox.getChildren().addAll(firstNameField, lastNameField, gpaField, majorField, idField);

		TextField outputField = new TextField();
		outputField.setMaxSize(700, 30);
		outputField.setEffect(ds);
		outputField.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

		ListView<Person> listView = new ListView<Person>();
		listView.setMaxSize(800, 200);
		listView.setEffect(ds);
				
		VBox outputBox = new VBox(10);
		outputBox.setAlignment(Pos.CENTER);
		outputBox.getChildren().addAll(outputField, listView, exitBtn);
		
//		Makes ListView Observable and creates a Listener that inputs selection to textFields
		
		watcher = FXCollections.observableArrayList(listView.getItems());
		listener = new ChangeListener<Person>() {
			@Override
			public void changed(ObservableValue<? extends Person> arg0, Person p1, Person p2) {
				if (p2 != null) {
					firstNameField.setText(p2.getName().getFirstName());
					lastNameField.setText(p2.getName().getLastName());
					gpaField.setText(((Student) p2).getGpa() + "");
					majorField.setText(((Student) p2).getMajor());
					idField.setText(p2.getId());
				}
			}
		};
		listView.getSelectionModel().selectedItemProperty().addListener(listener);

// 		Actions of the Insert Button

		insertBtn.setOnAction(e -> {
			
			outputField.clear();
			listView.getItems().clear();
			
			String firstName;
			String lastName;
			String major;
			double gpa;
						
			if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty() 
					&& !gpaField.getText().isEmpty() && !majorField.getText().isEmpty()) {
				firstName = Utilities.capitalize(firstNameField.getText());
				lastName = Utilities.capitalize(lastNameField.getText());
				gpa = Double.parseDouble(gpaField.getText());
				major = majorField.getText().toUpperCase();
				while (!Utilities.existsMajor(major)) {
					TextInputDialog dialog = new TextInputDialog("Major");
					dialog.setTitle("Major does not exist");
					dialog.setHeaderText("A valid Major is required");
					dialog.setContentText("Please enter a valid Major");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						major = result.get().toUpperCase();
					}
				}
				while (gpa < 0.0 || gpa > 4.0) {
					TextInputDialog dialog = new TextInputDialog("GPA");
					dialog.setTitle("Incorrect GPA value");
					dialog.setHeaderText("GPA should be between 0.0 and 4.0");
					dialog.setContentText("Please enter a correct GPA value");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						gpa = Double.parseDouble(result.get());
					}
				}
				Student s = new Student(new Name(firstName, lastName), gpa, major);
				personBag.insert(s);
				listView.getItems().add(s);
				personBag.display();
				outputField.setText("YOU HAVE ADDED THIS STUDENT.");
				Backup.backupPersonBag(personBag, file);
			}
			else outputField.setText("INPUT A FULL NAME, A GPA AND A MAJOR TO INSERT A STUDENT");
			
			firstNameField.clear();
			lastNameField.clear();
			gpaField.clear();
			majorField.clear();
			
		});

// 		Actions of the Search Button

		searchBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();
			Person[] results = null;

			if (!idField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class && p.getId().equals(idField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH ID # " + idField.getText() + " DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULT FOR STUDENT ID #: " + idField.getText());
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class
								&& p.getName().getFirstName().toLowerCase().contains(firstNameField.getText().toLowerCase())
								&& p.getName().getLastName().toLowerCase().contains(lastNameField.getText().toLowerCase());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH FULL NAME CONTAINING: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR STUDENTS WITH FULL NAME CONTAINING : \"" + firstNameField.getText() + " " + lastNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!lastNameField.getText().isEmpty() && firstNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class
								&& p.getName().getLastName().toLowerCase().contains(lastNameField.getText().toLowerCase());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH LAST NAME CONTAINING: \"" + lastNameField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR STUDENTS WITH LAST NAME CONTAINING: \"" + lastNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && lastNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class
								&& p.getName().getFirstName().toLowerCase().contains(firstNameField.getText().toLowerCase());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH FIRST NAME CONTAINING: \"" + firstNameField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR STUDENTS WITH FIRST NAME CONTAINING : \"" + firstNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!majorField.getText().isEmpty()) {
				if (!Utilities.existsMajor(majorField.getText())) {
					outputField.appendText("THE MAJOR OF: \"" + majorField.getText().toUpperCase() + "\" DOES NOT EXIST");
				} 
				else {
					results = personBag.search(new Predicate<Person>() {
						@Override
						public boolean test(Person p) {
							return p.getClass() == Student.class && ((Student) p).getMajor().equalsIgnoreCase(majorField.getText());
						}
					});
					if (results.length == 0) {
						outputField.appendText("STUDENT WITH MAJOR OF: \"" + majorField.getText().toUpperCase() + "\" DOES NOT EXIST");
					} 
					else {
						outputField.appendText("RESULTS FOR STUDENTS WITH MAJOR OF: \"" + majorField.getText().toUpperCase() + "\"");
						for (Person p : results) {
							listView.getItems().add(p);
						}
					}
				}
			} 
			
			else if (!gpaField.getText().isEmpty()) {
				double tempGpa = Double.parseDouble(gpaField.getText());
				if (tempGpa < 0.0 || tempGpa > 4.0) {
					outputField.appendText("THE GPA OF " + tempGpa + " IS OUT OF BOUNDS");
				} 
				else {
					results = personBag.search(new Predicate<Person>() {
						@Override
						public boolean test(Person p) {
							return p.getClass() == Student.class
									&& ((Student) p).getGpa() == tempGpa;
						}
					});
					if (results.length == 0) {
						outputField.appendText("STUDENT WITH GPA VALUE OF " + tempGpa + " DOES NOT EXIST");
					} 
					else {
						outputField.appendText("RESULTS FOR STUDENTS WITH GPA OF " + tempGpa);
						for (Person p : results) {
							listView.getItems().add(p);
						}
					}
				}
			} 
			
			else {
				outputField.appendText("ENTER A INPUT INTO A FIELD IN ORDER TO SEARCH");
			}
			
			idField.clear();
			firstNameField.clear();
			lastNameField.clear();
			majorField.clear();
			gpaField.clear();

		});

//		Actions of the Update Button
		updateBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();
			
			if (idField.getText().isEmpty()) {
				outputField.appendText("INPUT AN ID # TO UPDATE A STUDENT");
			} 
			
			else {
				Person p = personBag.findById(idField.getText());
				if (p == null || p.getClass() != Student.class) {
					outputField.appendText("THERE IS NO STUDENT WITH THIS ID");
				} 
				else {
					if (!firstNameField.getText().isEmpty()) {
						p.getName().setFirstName(Utilities.capitalize(firstNameField.getText()));
					}
					if (!lastNameField.getText().isEmpty()) {
						p.getName().setLastName(Utilities.capitalize(lastNameField.getText()));
					}
					if (!gpaField.getText().isEmpty()) {
						double tempGpa = Double.parseDouble(gpaField.getText());
						while (tempGpa < 0.0 || tempGpa > 4.0) {
							TextInputDialog dialog = new TextInputDialog("GPA");
							dialog.setTitle("Incorrect GPA value");
							dialog.setHeaderText("GPA should be between 0.0 and 4.0");
							dialog.setContentText("Please enter a correct GPA value");
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								tempGpa = Double.parseDouble(result.get());
							}
						}
						((Student) p).setGpa(tempGpa);
					}
					if (!majorField.getText().isEmpty()) {
						String tempMajor = majorField.getText().toUpperCase();
						while (!Utilities.existsMajor(tempMajor)) {
							TextInputDialog dialog = new TextInputDialog("Major");
							dialog.setTitle("Major does not exist");
							dialog.setHeaderText("A valid Major is required");
							dialog.setContentText("Please enter a valid Major");
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								tempMajor = result.get().toUpperCase();
							}
						}
						((Student) p).setMajor(tempMajor);
					}
					outputField.appendText("STUDENT WITH ID #: " + idField.getText() + " HAS BEEN UPDATED.");
					listView.getItems().add(p);
					Backup.backupPersonBag(personBag, file);
				}
			}

			idField.clear();
			firstNameField.clear();
			lastNameField.clear();
			majorField.clear();
			gpaField.clear();

		});

//		Actions of the Remove Button
		removeBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();
			Person[] results;

			if (!idField.getText().isEmpty()) {
				results = personBag.delete(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class && p.getId().equals(idField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH ID # " + idField.getText() + " DOES NOT EXIST");
				}
				else {
					outputField.appendText("STUDENT WITH ID #: " + idField.getText() + " HAS BEEN REMOVED");
					for (Person p : results) {
						listView.getItems().add(p);
					}
					Backup.backupPersonBag(personBag, file);
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty()) {
				results = personBag.delete(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Student.class
								&& p.getName().getFirstName().equalsIgnoreCase(firstNameField.getText())
								&& p.getName().getLastName().equalsIgnoreCase(lastNameField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("STUDENT WITH EXACT FULL NAME: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" DOES NOT EXIST");
				}
				else {
					outputField.appendText("STUDENTS WITH EXACT FULL NAME: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" HAVE BEEN REMOVED");
					for (Person p : results) {
						listView.getItems().add(p);
					}
					Backup.backupPersonBag(personBag, file);
				}
			} 
			
			else {
				outputField.appendText("INPUT A ID # OR A EXACT FULL NAME TO REMOVE");
			}

			idField.clear();
			firstNameField.clear();
			lastNameField.clear();
			majorField.clear();
			gpaField.clear();

		});
		
// 		Action of Clear Button
		
		clearBtn.setOnAction(e -> {
			firstNameField.clear();
			lastNameField.clear();
			gpaField.clear();
			majorField.clear();
			idField.clear();
			
		});

// 		Action of Exit Button
		
		exitBtn.setOnAction(e -> {
			Backup.backupPersonBag(personBag, file);
			Platform.exit();
		
		});

		
		studentPane = new VBox(30);
		studentPane.setAlignment(Pos.CENTER);
		studentPane.setStyle("-fx-background-color: #b2cdda");
		studentPane.getChildren().addAll(title, inputBox, btnBox, outputBox);
		
	}

	public VBox getStudentPane() {
		return studentPane;
	}
	
	public static void setPersonBag(PersonBag pBag) {
		personBag = pBag;
	}
	
	public static void setFile(File fileName) {
		file = fileName;
	}

}
