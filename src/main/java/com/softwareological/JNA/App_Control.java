package com.softwareological.JNA;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Optional;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import javafx.stage.Stage;
import javafx.util.Pair;

public class App_Control {
	
	private static String LOGIN_ICON = 
			"https://cdn1.iconfinder.com/data/icons/designer-s-tools-1/512/Notes-128.png";
	private static String LOGIN_IMAGE = 
			"https://findicons.com/files/icons/121/security/48/login.png";
	@FXML Button btn_login;
	@FXML Button btn_signup;
	
	@FXML protected void handleBtnLogin(ActionEvent e)
	{
		Dialog<Pair<String, String>> dialog = new Dialog<>();
		dialog.setTitle("Login: Welcome back!");
		dialog.setHeaderText("Let's get you back to your notes.");
		dialog.setGraphic(new ImageView(new Image(LOGIN_IMAGE)));
		
		ButtonType login_button = new ButtonType("Log In", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(login_button,ButtonType.CANCEL);
		
		GridPane grid_pane = new GridPane();
		grid_pane.setHgap(10);
		grid_pane.setVgap(10);
		grid_pane.setPadding(new Insets(20,150,10,10));
		
		TextField username = new TextField();
		username.setPromptText("username");
		PasswordField password = new PasswordField();
		password.setPromptText("password");
		
		grid_pane.add(new Label("Username: "), 0, 0);
		grid_pane.add(username, 1, 0);
		grid_pane.add(new Label("Password: "), 0, 1);
		grid_pane.add(password, 1, 1);
		
		Node login_button_node = dialog.getDialogPane().lookupButton(login_button);
		login_button_node.setDisable(true);
		
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			login_button_node.setDisable(newValue.trim().isEmpty());
		});
		
		dialog.getDialogPane().setContent(grid_pane);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(LOGIN_ICON));
		
		Platform.runLater(() -> username.requestFocus());
		
		dialog.setResultConverter(dialogButton -> {
  	      if (dialogButton == login_button) {
  	          return new Pair<>(username.getText(), password.getText());
  	      }
  	      return null;
  	  	});
		
		Optional<Pair<String, String>> result = dialog.showAndWait();

  	  	result.ifPresent(usernamePassword -> {
  	  		try {
  	  			
  	  			Object[] result_array = App.RESOURCE_MANAGER.executeQuery(
						"SELECT user_password, user_name, user_surname, user_email, user_id FROM users WHERE user_email='" 
						+ usernamePassword.getKey() + "';");
				
  	  			ResultSet results = (ResultSet)result_array[0];
				
				try 
				{
					results.next();
					
					if(results.getString(1).equals(usernamePassword.getValue()))
					{
						Alert alert = new Alert(AlertType.INFORMATION);
						Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
						alert_stage.getIcons().add(new Image(LOGIN_ICON));
						alert.setTitle("Successful Log In");
						alert.setHeaderText("Welcome back " + results.getString(2));
						alert.setContentText("We're glad you're back!");

						alert.showAndWait();
						
						//Create User Object
						
						App.USER = new User(results.getString(2),
								results.getString(3),
								results.getString(4),
								results.getInt(5));
						
						//Change Scenes
						
						Parent root = App.getRoot();
						root = FXMLLoader.load(
								getClass().getClassLoader().getResource("Application_UI.fxml"));
						
						Scene scene = new Scene(root, 800, 500);
						
						Stage primary_stage = App.getPrimaryStage();
						primary_stage.setScene(scene);
						primary_stage.setResizable(true);
					}
					else {
						Alert alert = new Alert(AlertType.ERROR);
						Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
						alert_stage.getIcons().add(new Image(LOGIN_ICON));
						alert.setTitle("Unable to log in");
						alert.setHeaderText("Oh no!");
						alert.setContentText("We're sorry " + usernamePassword.getKey() +
								" you've entered the incorrect password.");

						alert.showAndWait();
					}
				}
				catch (Exception w)
				{
					Alert alert = new Alert(AlertType.ERROR);
					Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert_stage.getIcons().add(new Image(LOGIN_ICON));
					alert.setTitle("Invalid username");
					alert.setHeaderText("Oh no!");
					alert.setContentText("We're sorry, we could not find the username: "
							+ usernamePassword.getKey() + ". Try again or if you are new, choose the Sign Up option.");

					alert.showAndWait();
				}
				
				Statement statement = (Statement) result_array [1];
				statement.close();
				
			} catch (Exception e1) {
				App_Control.displayException(e1);
			}
  	  	});
	}
	
	@FXML protected void handleBtnSignup(ActionEvent e)
	{
		Dialog<ArrayList<String>> dialog = new Dialog<>();
		dialog.setTitle("Sign Up Today!");
		dialog.setHeaderText("Let's get you set up with an account");
		dialog.setGraphic(new ImageView(new Image(LOGIN_IMAGE)));
		
		ButtonType signup_button = new ButtonType("Sign Up", ButtonData.OK_DONE);
		dialog.getDialogPane().getButtonTypes().addAll(signup_button,ButtonType.CANCEL);
		
		GridPane grid_pane = new GridPane();
		grid_pane.setHgap(10);
		grid_pane.setVgap(10);
		grid_pane.setPadding(new Insets(20,150,10,10));
		
		TextField username = new TextField();
		username.setPromptText("name");
		TextField surname = new TextField();
		surname.setPromptText("surname");
		TextField email = new TextField();
		email.setPromptText("email");
		PasswordField password = new PasswordField();
		password.setPromptText("password");
		
		grid_pane.add(new Label("Name: "), 0, 0);
		grid_pane.add(username, 1, 0);
		grid_pane.add(new Label("Surname: "), 0, 1);
		grid_pane.add(surname, 1, 1);
		grid_pane.add(new Label("Email: "), 0, 2);
		grid_pane.add(email, 1, 2);
		grid_pane.add(new Label("Password: "), 0, 3);
		grid_pane.add(password, 1, 3);
		
		Node signup_button_node = dialog.getDialogPane().lookupButton(signup_button);
		signup_button_node.setDisable(true);

		password.textProperty().addListener((observable, oldValue, newValue) -> {
			signup_button_node.setDisable(newValue.trim().isEmpty() || email.getText().equals("")
					|| username.getText().equals("")
					|| surname.getText().equals(""));
		});
		
		username.textProperty().addListener((observable, oldValue, newValue) -> {
			signup_button_node.setDisable(newValue.trim().isEmpty() || email.getText().equals("")
					|| password.getText().equals("")
					|| surname.getText().equals(""));
		});
		
		email.textProperty().addListener((observable, oldValue, newValue) -> {
			signup_button_node.setDisable(newValue.trim().isEmpty() || password.getText().equals("")
					|| username.getText().equals("")
					|| surname.getText().equals(""));
		});
		
		surname.textProperty().addListener((observable, oldValue, newValue) -> {
			signup_button_node.setDisable(newValue.trim().isEmpty() || email.getText().equals("")
					|| username.getText().equals("")
					|| password.getText().equals(""));
		});
		
		dialog.getDialogPane().setContent(grid_pane);
		Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
		stage.getIcons().add(new Image(LOGIN_ICON));
		
		Platform.runLater(() -> username.requestFocus());
		
		dialog.setResultConverter(dialogButton -> {
	  	      if (dialogButton == signup_button) {
	  	    	  ArrayList<String> returnList = new ArrayList<String>();
	  	    	  returnList.add(username.getText());
	  	    	  returnList.add(surname.getText());
	  	    	  returnList.add(email.getText());
	  	    	  returnList.add(password.getText());
	  	          return returnList;
	  	      }
	  	      return null;
	  	  	});
		
		Optional<ArrayList<String>> result = dialog.showAndWait();
		
		result.ifPresent(usernamePassword -> {
  	  		try 
  	  		{
  	  			Boolean isRegistered = true;
  	  			try 
  	  			{
  	  				App.RESOURCE_MANAGER.execute(
						"INSERT INTO users(user_name, user_surname, user_email, user_password) "
						+ "VALUES('"+usernamePassword.get(0)+"','"+usernamePassword.get(1)+"'"
								+ ",'"+usernamePassword.get(2)+"',"
										+ "'"+usernamePassword.get(3)+"');");
  	  				
  	  				isRegistered = false;
  	  			}
  	  			catch (SQLException j)
  	  			{
	  	  			Alert alert = new Alert(AlertType.ERROR);
					Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert_stage.getIcons().add(new Image(LOGIN_ICON));
					alert.setTitle("User already exists");
					alert.setHeaderText("Oh no!");
					alert.setContentText("We already have a user registed with us with this email.");
		
					alert.showAndWait();
  	  			}
	  	  		
  	  			if(!isRegistered) 
  	  			{
	  	  			Object[] results_array = App.RESOURCE_MANAGER.executeQuery(
		  	  				"SELECT user_id FROM users WHERE user_email='" + usernamePassword.get(2) + "';");
		  	  		
		  	  		ResultSet results = (ResultSet) results_array[0];
		  	  		
		  	  		results.next();
		  	  		
		  	  		App.USER = new User(
		  	  			usernamePassword.get(0),
		  	  			usernamePassword.get(1),
		  	  			usernamePassword.get(2),
		  	  			results.getInt(1));
		  	  		
		  	  		Statement statement = (Statement) results_array[1];
		  	  		statement.close();
			  	  	
		  	  		Alert alert = new Alert(AlertType.INFORMATION);
					Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
					alert_stage.getIcons().add(new Image(LOGIN_ICON));
					alert.setTitle("Welcome aboard");
					alert.setHeaderText("Welcome to [JNA] Note-Taking " + usernamePassword.get(0));
					alert.setContentText("We're glad you decided to join us, enjoy your note-taking!");
					alert.showAndWait();
					
					//Change scenes
					
					Parent root = App.getRoot();
					try {
						root = FXMLLoader.load(
								getClass().getClassLoader().getResource("Application_UI.fxml"));
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					
					Scene scene = new Scene(root, 800, 500);
					
					Stage primary_stage = App.getPrimaryStage();
					primary_stage.setScene(scene);
					primary_stage.setResizable(true);
  	  			}
	  	  		
  	  		}
  	  		catch (SQLException f)
  	  		{
	  	  		Alert alert = new Alert(AlertType.ERROR);
				Stage alert_stage = (Stage) alert.getDialogPane().getScene().getWindow();
				alert_stage.getIcons().add(new Image(LOGIN_ICON));
				alert.setTitle("User already exists");
				alert.setHeaderText("Oh no!");
				alert.setContentText("We already have a user registed with us with this email.");
	
				alert.showAndWait();
  	  		}
			
  	  	});
	}
	
	public static void displayException(Exception e)
	{
		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Error");
		alert.setHeaderText("Oh no!");
		alert.setContentText("Something went wrong");

		// Create expandable Exception.
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		String exceptionText = sw.toString();

		Label label = new Label("The exception stacktrace was:");

		TextArea textArea = new TextArea(exceptionText);
		textArea.setEditable(false);
		textArea.setWrapText(true);

		textArea.setMaxWidth(Double.MAX_VALUE);
		textArea.setMaxHeight(Double.MAX_VALUE);
		GridPane.setVgrow(textArea, Priority.ALWAYS);
		GridPane.setHgrow(textArea, Priority.ALWAYS);

		GridPane expContent = new GridPane();
		expContent.setMaxWidth(Double.MAX_VALUE);
		expContent.add(label, 0, 0);
		expContent.add(textArea, 0, 1);

		// Set expandable Exception into the dialog pane.
		alert.getDialogPane().setExpandableContent(expContent);

		alert.showAndWait();
	}
}
