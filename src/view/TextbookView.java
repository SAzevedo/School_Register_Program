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
import model.Textbook;
import model.TextbookBag;
import util.Backup;
import util.Utilities;

public class TextbookView {
	private static TextbookBag textbookBag;
	private static File file;
	ObservableList<Textbook> watcher;
	ChangeListener<Textbook> listener;
	private VBox textbookPane;

	public TextbookView(TextbookBag tBag) {
		textbookBag = tBag;
		file = new File("backupFolder/TextbookBag.dat");

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
		
		Text header = new Text("TextBook Database");
		header.setFont(Font.font("Verdana", FontWeight.BOLD, 30));
		header.setFill(Color.HONEYDEW);

		DropShadow ds = new DropShadow();
		header.setEffect(ds);
		insertBtn.setEffect(ds);
		searchBtn.setEffect(ds);
		updateBtn.setEffect(ds);
		removeBtn.setEffect(ds);
		exitBtn.setEffect(ds);
		clearBtn.setEffect(ds);
		
		HBox btnBox = new HBox(30);
		btnBox.setAlignment(Pos.CENTER);
		btnBox.getChildren().addAll(insertBtn, searchBtn, updateBtn, removeBtn, clearBtn);

		TextField titleField = new TextField();
		titleField.setPromptText("TITLE");
		titleField.setPrefSize(200, 30);
		titleField.setEffect(ds);

		TextField authorFirstField = new TextField();
		authorFirstField.setPromptText("AUTHOR FIRST");
		authorFirstField.setPrefSize(120, 30);
		authorFirstField.setEffect(ds);

		TextField authorLastField = new TextField();
		authorLastField.setPromptText("AUTHOR LAST");
		authorLastField.setPrefSize(120, 30);
		authorLastField.setEffect(ds);

		TextField priceField = new TextField();
		priceField.setPromptText("PRICE");
		priceField.setPrefSize(60, 30);
		priceField.setEffect(ds);

		TextField isbnField = new TextField();
		isbnField.setPromptText("ISBN");
		isbnField.setPrefSize(150, 30);
		isbnField.setEffect(ds);

		HBox inputBox = new HBox(20);
		inputBox.setAlignment(Pos.CENTER);
		inputBox.getChildren().addAll(titleField, authorFirstField, authorLastField, priceField, isbnField);

		TextField outputField = new TextField();
		outputField.setMaxSize(700, 30);
		outputField.setEffect(ds);
		outputField.setFont(Font.font("Verdana", FontWeight.BOLD, 15));

		ListView<Textbook> listView = new ListView<Textbook>();
		listView.setMaxSize(800, 200);
		listView.setEffect(ds);

		VBox outputBox = new VBox(10);
		outputBox.setAlignment(Pos.CENTER);
		outputBox.getChildren().addAll(outputField, listView, exitBtn);
		
//		Makes ListView Observable and creates a Listener that inputs selection to textFields
		
		watcher = FXCollections.observableArrayList(listView.getItems());
		listener = new ChangeListener<Textbook>() {
			@Override
			public void changed(ObservableValue<? extends Textbook> arg0, Textbook t1, Textbook t2) {
				if (t2 != null) {
					titleField.setText(t2.getTitle());
					authorFirstField.setText(t2.getAuthor().getFirstName());
					authorLastField.setText(t2.getAuthor().getLastName());
					isbnField.setText(((Textbook) t2).getIsbn());
					priceField.setText(((Textbook) t2).getPrice() + "");
				}
				
			}
		};
		listView.getSelectionModel().selectedItemProperty().addListener(listener);

// 		Actions of the Insert Button

		insertBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();

			if (!authorFirstField.getText().isEmpty() && !authorLastField.getText().isEmpty() &&
					!priceField.getText().isEmpty() && !isbnField.getText().isEmpty()) {
				String title = Utilities.capitalize(titleField.getText());
				String authorFirst = Utilities.capitalize(authorFirstField.getText());
				String authorLast = Utilities.capitalize(authorLastField.getText());
				Name author = new Name(authorFirst, authorLast);
				double price = Double.parseDouble(priceField.getText());
				while (price < 0.0 || price > 200.00) {
					TextInputDialog dialog = new TextInputDialog("Price");
					dialog.setTitle("Price is out of bounds");
					dialog.setHeaderText("Price should be between $0.00 and $200.00");
					dialog.setContentText("Please enter a valid Price");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						price = Double.parseDouble(result.get());
					}
				}
				String isbn = isbnField.getText();
				while (!Utilities.possibleIsbn(isbn)) {
					TextInputDialog dialog = new TextInputDialog("ISBN");
					dialog.setTitle("ISBN # is out of bounds");
					dialog.setHeaderText("A valid ISBN is either 10 or 13 numbers and doesn't contain letters.");
					dialog.setContentText("Please enter a valid ISBN");
					Optional<String> result = dialog.showAndWait();
					if (result.isPresent()) {
						isbn = result.get();
					}
				}
				
				Textbook book = new Textbook(title, isbn, author, price);
				textbookBag.insert(book);
				textbookBag.display();
				outputField.setText("YOU HAVE ADDED THIS TEXTBOOK.");
				listView.getItems().add(book);
				Backup.backupTextbookBag(textbookBag, file);
			}
			
			else {
				outputField.appendText("INPUT A TITLE, AUTHOR'S FULL NAME, PRICE, AND ISBN# TO INSERT A TEXTBOOK");
			}

			titleField.clear();
			authorFirstField.clear();
			authorLastField.clear();
			isbnField.clear();
			priceField.clear();
			
		});

// 		Actions of the Search Button

		searchBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();
			Textbook[] results = null;

			if (!isbnField.getText().isEmpty()) {
				results = textbookBag.search(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getIsbn().equals(isbnField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH ISBN # " + isbnField.getText() + " DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULT FOR TEXTBOOK WITH ISBN #: " + isbnField.getText());
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
				}
			} 
			
			else if (!titleField.getText().isEmpty()) {
				results = textbookBag.search(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getTitle().contains(titleField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH TITLE CONTAINING \"" + titleField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR TEXTBOOK TITLES CONTAINING \"" + titleField.getText() + "\"");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
				}
			} 
			
			else if (!authorFirstField.getText().isEmpty() && !authorLastField.getText().isEmpty()) {
				results = textbookBag.search(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getAuthor().getFirstName().contains(authorFirstField.getText())
								&& t.getAuthor().getLastName().contains(authorLastField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH AUTHOR'S FULL NAME CONTAINING \"" + authorFirstField.getText() + " " + authorLastField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR TEXTBOOKS WITH AUTHOR'S FULL NAME CONTAINING \"" + authorFirstField.getText() + " " + authorLastField.getText() + "\"");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
				}
			} 
			
			else if (!authorLastField.getText().isEmpty() && authorFirstField.getText().isEmpty()) {
				results = textbookBag.search(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getAuthor().getLastName().contains(authorLastField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH AUTHOR'S LAST NAME CONTAINING \"" + authorLastField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR TEXTBOOKS WITH AUTHOR'S LAST NAME CONTAINING \"" + authorLastField.getText() + "\"");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
				}
			} 
			
			else if (!authorFirstField.getText().isEmpty() && authorLastField.getText().isEmpty()) {
				results = textbookBag.search(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getAuthor().getFirstName().contains(authorFirstField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH AUTHOR'S FIRST NAME CONTAINING \"" + authorFirstField.getText() + "\" DOES NOT EXIST");
				} 
				else {
					outputField.appendText("RESULTS FOR TEXTBOOKS WITH AUTHOR'S FIRST NAME CONTAINING \"" + authorFirstField.getText() + "\"");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
				}
			} 
			
			else if (!priceField.getText().isEmpty()) {
				double tempPrice = Double.parseDouble(priceField.getText());
				if (tempPrice < 0.00 || tempPrice > 200.00) {
					outputField.appendText("THE PRICE OF $" + tempPrice + " IS OUT OF BOUNDS");
				}
				else {
					results = textbookBag.search(new Predicate<Textbook>() {
						@Override
						public boolean test(Textbook t) {
							return t.getPrice() == Double.parseDouble(priceField.getText());
						}
					});
					if (results.length == 0) {
						outputField.appendText("TEXTBOOK WITH PRICE VALUE OF $" + tempPrice + " DOES NOT EXIST");
					} 
					else {
						outputField.appendText("RESULTS FOR TEXTBOOKS WITH PRICE VALUE OF $" + tempPrice);
						for (Textbook t : results) {
							listView.getItems().add(t);
						}
					}
				}
			} 
			
			else {
				outputField.appendText("ENTER AN INPUT INTO A TEXT FIELD IN ORDER TO SEARCH");
			}

			titleField.clear();
			authorFirstField.clear();
			authorLastField.clear();
			priceField.clear();
			isbnField.clear();

		});

//		Actions of the Update Button
		updateBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();

			if (isbnField.getText().isEmpty()) {
				outputField.appendText("INPUT A ISBN # TO UPDATE A TEXTBOOK");
			} 
			
			else {
				Textbook t = textbookBag.findByIsbn(isbnField.getText());
				if (t == null) {
					outputField.appendText("THERE IS NO TEXTBOOK WITH THIS ISBN #.");
				} 
				else {
					if (!authorFirstField.getText().isEmpty()) {
						t.getAuthor().setFirstName(Utilities.capitalize(authorFirstField.getText()));
					}
					if (!authorLastField.getText().isEmpty()) {
						t.getAuthor().setLastName(Utilities.capitalize(authorLastField.getText()));
					}
					if (!titleField.getText().isEmpty()) {
						t.setTitle(Utilities.capitalize(titleField.getText()));
					}
					if (!priceField.getText().isEmpty()) {
						double tempPrice = Double.parseDouble(priceField.getText());
						while (tempPrice < 0.0 || tempPrice > 200.00) {
							TextInputDialog dialog = new TextInputDialog("Price");
							dialog.setTitle("Price is out of bounds");
							dialog.setHeaderText("The Price should be in between $0 and $200.00");
							dialog.setContentText("Please enter a valid Price value");
							Optional<String> result = dialog.showAndWait();
							if (result.isPresent()) {
								tempPrice = Double.parseDouble(result.get());
							}	
						}
						t.setPrice(Double.parseDouble(priceField.getText()));
					}
					outputField.appendText("TEXTBOOK WITH ISBN #: " + isbnField.getText() + " HAS BEEN UPDATED.");
					listView.getItems().add(t);
					Backup.backupTextbookBag(textbookBag, file);
				}
			}

			titleField.clear();
			authorFirstField.clear();
			authorLastField.clear();
			priceField.clear();
			isbnField.clear();

		});

//		Actions of the Remove Button
		removeBtn.setOnAction(e -> {

			outputField.clear();
			listView.getItems().clear();
			Textbook[] results;

			if (!isbnField.getText().isEmpty()) {
				results = textbookBag.delete(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getIsbn().equals(isbnField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH ISBN # " + isbnField.getText() + " DOES NOT EXIST");
				}
				else {
					outputField.appendText("TEXTBOOK WITH ISBN #: " + isbnField.getText() + " HAS BEEN REMOVED");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
					Backup.backupTextbookBag(textbookBag, file);
				}
			} 
			
			else if (!titleField.getText().isEmpty()) {
				results = textbookBag.delete(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getTitle().equals(titleField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH EXACT TITLE OF \"" + titleField.getText() + "\" DOES NOT EXIST");
				}
				else {
					outputField.appendText("TEXTBOOKS WITH EXACT TITLE OF \"" + titleField.getText() + "\" HAVE BEEN REMOVED");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
					Backup.backupTextbookBag(textbookBag, file);
				}
			} 
			
			else if (!authorFirstField.getText().isEmpty() && !authorLastField.getText().isEmpty()) {
				results = textbookBag.delete(new Predicate<Textbook>() {
					@Override
					public boolean test(Textbook t) {
						return t.getAuthor().getFirstName().contains(authorFirstField.getText())
								&& t.getAuthor().getLastName().contains(authorLastField.getText());
					}
				});
				if (results.length == 0) {
					outputField.appendText("TEXTBOOK WITH AUTHOR'S FULL NAME OF \"" + authorFirstField.getText() + " " + authorLastField.getText() + "\" DOES NOT EXIST");
				}
				else {
					outputField.appendText("TEXTBOOKS WITH AUTHOR'S FULL NAME OF \"" + authorFirstField.getText() + " " + authorLastField.getText() + "\" HAVE BEEN REMOVED");
					for (Textbook t : results) {
						listView.getItems().add(t);
					}
					Backup.backupTextbookBag(textbookBag, file);
				}
			} 
			
			else {
				outputField.appendText("INPUT A ISBN #, EXACT TITLE, OR AUTHOR'S FULL NAME TO REMOVE");
			}

			titleField.clear();
			authorFirstField.clear();
			authorLastField.clear();
			priceField.clear();
			isbnField.clear();

		});

// 		Action of the Clear Button		
		
		clearBtn.setOnAction(e -> {
			authorFirstField.clear();
			authorLastField.clear();
			titleField.clear();
			priceField.clear();
			isbnField.clear();
		
		});
		
// 		Action of the Exit Button

		exitBtn.setOnAction(e -> {
			Backup.backupTextbookBag(textbookBag, file);
			Platform.exit();
		
		});

		textbookPane = new VBox(30);
		textbookPane.setAlignment(Pos.CENTER);
		textbookPane.setStyle("-fx-background-color: #b2cdda");
		textbookPane.getChildren().addAll(header, inputBox, btnBox, outputBox);
		

	}

	public VBox getTextbookPane() {
		return textbookPane;
	}
	
	public static void setTextbookBag(TextbookBag tBag) {
		textbookBag = tBag;
	}
	
	public static void setFile(File fileName) {
		file = fileName;
	}

}
