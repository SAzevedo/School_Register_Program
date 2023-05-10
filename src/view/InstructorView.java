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
import model.Instructor;
import model.Name;
import model.Person;
import model.PersonBag;
import util.Backup;
import util.Utilities;

public class InstructorView {
	private static PersonBag personBag;
	private static File file;
	ObservableList<Person> watcher;
	ChangeListener<Person> listener;
	private VBox instructorPane;
	
	
	public InstructorView(PersonBag pBag) {
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
		clearBtn.setPrefSize(100,30);
		
		Text title = new Text("Instructor Database");
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

		TextField rankField = new TextField();
		rankField.setPromptText("RANK");
		rankField.setPrefSize(150, 30);
		rankField.setEffect(ds);

		TextField salaryField = new TextField();
		salaryField.setPromptText("SALARY");
		salaryField.setPrefSize(80, 30);
		salaryField.setEffect(ds);

		TextField idField = new TextField();
		idField.setPromptText("ID");
		idField.setPrefSize(60, 30);
		idField.setEffect(ds);

		HBox inputBox = new HBox(20);
		inputBox.setAlignment(Pos.CENTER);
		inputBox.getChildren().addAll(firstNameField, lastNameField, rankField, salaryField, idField);

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
					rankField.setText(((Instructor) p2).getRank());
					salaryField.setText(((Instructor) p2).getSalary() + "");
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
			String rank;
			double salary;
			
			if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty() &&
					!rankField.getText().isEmpty() && !salaryField.getText().isEmpty()) {
				firstName = Utilities.capitalize(firstNameField.getText());
				lastName = Utilities.capitalize(lastNameField.getText());
				rank = rankField.getText().toUpperCase();
				salary = Double.parseDouble(salaryField.getText());
				
				while (!Utilities.existsRank(rank)) {
					TextInputDialog dialog = new TextInputDialog("Rank");
					dialog.setTitle("Rank does not exist");
					dialog.setHeaderText("A valid Rank is required");
					dialog.setContentText("Please enter a valid Rank");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						rank = result.get().toUpperCase();
					}
				}
				
				while (salary < 10000.00 || salary > 100000.00) {
					TextInputDialog dialog = new TextInputDialog("Salary");
					dialog.setTitle("Salary is out of Range");
					dialog.setHeaderText("Salary should be between 10,000 and 100,000");
					dialog.setContentText("Please enter a valid Salary");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						salary = Double.parseDouble(result.get());
					}
				}
				
				Instructor i = new Instructor(new Name(firstName, lastName), rank, salary);
				personBag.insert(i);
				personBag.display();
				outputField.setText("YOU HAVE ADDED THIS INSTRUCTOR.");
				listView.getItems().add(i);
				Backup.backupPersonBag(personBag, file);
			}
			
			else {
				outputField.appendText("INPUT FULL NAME, RANK, AND SALARY TO INSERT A INSTRUCTOR");
			}
			

			firstNameField.clear();
			lastNameField.clear();
			rankField.clear();
			salaryField.clear();
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
						return p.getClass() == Instructor.class && p.getId().equals(idField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH ID # " + idField.getText() + " DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULT FOR INSTRUCTOR ID # " + idField.getText());
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class
								&& p.getName().getFirstName().contains(firstNameField.getText())
								&& p.getName().getLastName().contains(lastNameField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH FULL NAME CONTAINING: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" DOES NOT EXIST");
					} 
				else {
					outputField.appendText("RESULTS FOR INSTRUCTORS WITH FULL NAME CONTAINING : \"" + firstNameField.getText() + " " + lastNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!lastNameField.getText().isEmpty() && firstNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class
								&& p.getName().getLastName().contains(lastNameField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH LAST NAME CONTAINING: \"" + lastNameField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR INSTRUCTORS WITH LAST NAME CONTAINING: \"" + lastNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && lastNameField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class
								&& p.getName().getFirstName().contains(firstNameField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH FIRST NAME CONTAINING: \"" + firstNameField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR INSTRUCTORS WITH FIRST NAME CONTAINING : \"" + firstNameField.getText() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!rankField.getText().isEmpty()) {
				results = personBag.search(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class && ((Instructor) p).getRank().equals(rankField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTORS WITH RANK OF: \"" + rankField.getText().toUpperCase() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR INSTRUCTORS WITH MAJOR OF: \"" + rankField.getText().toUpperCase() + "\"");
					for (Person p : results) {
						listView.getItems().add(p);
					}
				}
			} 
			
			else if (!salaryField.getText().isEmpty()) {
				double tempSalary = Double.parseDouble(salaryField.getText());
				if (tempSalary < 10000 || tempSalary > 300000) {
					outputField.appendText("THE SALARY OF $" + tempSalary + " IS OUT OF BOUNDS");
				}
				else {
					results = personBag.search(new Predicate<Person>() {
						@Override
						public boolean test(Person p) {
							return p.getClass() == Instructor.class
								&& ((Instructor) p).getSalary() == tempSalary;
						}
					});
					if (results.length == 0) {
						outputField.appendText("INSTRUCTORS WITH SALARY VALUE OF $" + tempSalary + " DOES NOT EXIST");
					} 
					else {
						outputField.appendText("RESULTS FOR INSTRUCTORS WITH SALARY OF $" + tempSalary);
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
			rankField.clear();
			salaryField.clear();
						
		});
		
//		Actions of the Update Button
		updateBtn.setOnAction(e -> {
			
			outputField.clear();
			listView.getItems().clear();
			
			if (idField.getText().isEmpty()) {
				outputField.appendText("INPUT AN ID # TO UPDATE A INSTRUCTOR");
			} 
			else {
				Person p = personBag.findById(idField.getText());			
				if (p == null || p.getClass() != Instructor.class) {
					outputField.appendText("THERE IS NO INSTRUCTOR WITH THIS ID");
				} 
				else {
					if (!firstNameField.getText().isEmpty()) {
						p.getName().setFirstName(Utilities.capitalize(firstNameField.getText()));
					}
					if (!lastNameField.getText().isEmpty()) {
						p.getName().setLastName(Utilities.capitalize(lastNameField.getText()));
					}
					if (!rankField.getText().isEmpty()) {
						String tempRank = rankField.getText().toUpperCase();
						while (!Utilities.existsRank(tempRank)) {
							TextInputDialog dialog = new TextInputDialog("Rank");
							dialog.setTitle("Rank does not exist");
							dialog.setHeaderText("A valid Rank is required");
							dialog.setContentText("Please enter a valid Rank");
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								tempRank = result.get().toUpperCase();
							}
						}
						((Instructor) p).setRank(tempRank);
					}
					if (!salaryField.getText().isEmpty()) {
						double tempSalary = Double.parseDouble(salaryField.getText());
						while (tempSalary < 10000 || tempSalary > 100000) {
							TextInputDialog dialog = new TextInputDialog("Salary");
							dialog.setTitle("Salary is out of bounds");
							dialog.setHeaderText("Salary should be between 10,000 and 100,000");
							dialog.setContentText("Please enter a valid Salary");
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								tempSalary = Double.parseDouble(result.get());
							}
						}
						((Instructor) p).setSalary(tempSalary);
					}
					outputField.appendText("INSTRUCTOR WITH ID #: " + idField.getText() + " HAS BEEN UPDATED.");
					listView.getItems().add(p);
					Backup.backupPersonBag(personBag, file);
				}
			}
			
			idField.clear();
			firstNameField.clear();
			lastNameField.clear();
			rankField.clear();
			salaryField.clear();
			
		});
		
//		Actions of the Remove Button
		removeBtn.setOnAction(e -> {
			
			outputField.clear();
			listView.getItems().clear();

			if (!idField.getText().isEmpty()) {
				Person[] results = personBag.delete(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class && p.getId().equals(idField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH ID # " + idField.getText() + " DOES NOT EXIST");
				}
				else {
					outputField.appendText("INSTRUCTOR WITH ID #: " + idField.getText() + " HAS BEEN REMOVED");
					for (Person p : results) {
						listView.getItems().add(p);
					}
					Backup.backupPersonBag(personBag, file);
				}
			} 
			
			else if (!firstNameField.getText().isEmpty() && !lastNameField.getText().isEmpty()) {
				Person[] results = personBag.delete(new Predicate<Person>() {
					@Override
					public boolean test(Person p) {
						return p.getClass() == Instructor.class
								&& p.getName().getFirstName().equals(firstNameField.getText())
								&& p.getName().getLastName().equals(lastNameField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("INSTRUCTOR WITH EXACT FULL NAME: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" DOES NOT EXIST");
				}
				else {
					outputField.appendText("INSTRUCTORS WITH EXACT FULL NAME: \"" + firstNameField.getText() + " " + lastNameField.getText() + "\" HAVE BEEN REMOVED");
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
			rankField.clear();
			salaryField.clear();

		});
		
// 		Action of the Clear Button		
		
		clearBtn.setOnAction(e -> {
			firstNameField.clear();
			lastNameField.clear();
			rankField.clear();
			salaryField.clear();
			idField.clear();
		
		});
		
// 		Action of the Exit Button

		exitBtn.setOnAction(e -> {
			Backup.backupPersonBag(personBag, file);
			Platform.exit();
		
		});

		instructorPane = new VBox(30);
		instructorPane.setAlignment(Pos.CENTER);
		instructorPane.setStyle("-fx-background-color: #b2cdda");
		instructorPane.getChildren().addAll(title, inputBox, btnBox, outputBox);
		
	}

	public VBox getInstructorPane() {
		return instructorPane;
	}
	
	public static void setPersonBag(PersonBag pBag) {
		personBag = pBag;
	}
	
	public static void setFile(File fileName) {
		file = fileName;
	}

}
