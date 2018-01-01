package com.softwareological.JNA;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

public class App extends Application{

	public static final String APPLICATION_ICON =
			"https://cdn1.iconfinder.com/data/icons/"
			+ "designer-s-tools-1/512/Notes-128.png";

	public static final String SPLASH_SCREEN_IMAGE =
			"http://ktpsolutions.co.za/images/jna.png";

	private static final int SPLASH_WIDTH = 676;
    private static final int SPLASH_HEIGHT = 227;
    private static final String SPLASH_STYLE =
    		"-fx-padding: 5; "
    		+ "-fx-background-color: cornsilk; "
    		+ "-fx-border-width:5; "
    		+ "-fx-border-color: "
    		+ "linear-gradient("
    		+ "to bottom, "
    		+ "black, "
    		+ "derive(black, 50%)"
    		+ ");";

	private Pane splash_layout;
	private ProgressBar progress_bar;
	private Label lbl_progress_status;
	private Stage primary_stage;

	public static void main(String[] args)
	{
		launch(args);
	}

	@Override
	public void start(Stage init_stage) throws Exception
	{
		final Task<ObservableList<String>> startup_task =
				new Task<ObservableList<String>>()
		{

			@Override
			protected ObservableList<String> call() throws Exception
			{

				ObservableList<String> completed_tasks =
						FXCollections.<String>observableArrayList();

				ObservableList<String> tasks_to_complete =
						FXCollections.<String>observableArrayList(
								"Processing UI Engine...",
								"Server monkeys creating blocks...",
								"Finding nemo...",
								"Building database connection...",
								"Initializing setup...",
								"Starting...");

				updateMessage("Executing task: ");

				int process_units = 400;
				for(int index = 0; index < tasks_to_complete.size(); index++)
				{
					Thread.sleep(process_units);

					updateProgress(index + 1, tasks_to_complete.size());
					String nextTask = tasks_to_complete.get(index);
					completed_tasks.add(nextTask);

					updateMessage("Executing task: " + nextTask);
				}

				Thread.sleep(process_units);
				updateMessage("Welcome to JNA!");

				return completed_tasks;
			}

		};

		showSplash(
				init_stage,
				startup_task,
				() -> showPrimaryStage());

		new Thread(startup_task).start();
	}

	@Override
	public void init()
	{
		ImageView image_view = new ImageView(
				new Image(SPLASH_SCREEN_IMAGE));

		int width_adjustment = 20;
		progress_bar = new ProgressBar();
		progress_bar.setPrefWidth(SPLASH_WIDTH - width_adjustment);

		lbl_progress_status = new Label(
				"Starting JNA ...");

		splash_layout = new VBox();
		splash_layout.getChildren().addAll(
				image_view,
				progress_bar,
				lbl_progress_status);

		lbl_progress_status.setAlignment(Pos.CENTER);

		splash_layout.setStyle(SPLASH_STYLE);
		splash_layout.setEffect(new DropShadow());
	}

	private void showPrimaryStage()
	{
		try
		{
			primary_stage = new Stage(StageStyle.DECORATED);
			primary_stage.setTitle("[JNA]: Java Note-Taking App");
			primary_stage.getIcons().add(new Image(
	                APPLICATION_ICON
	        ));
			
			Parent root = FXMLLoader.load(
					getClass().getClassLoader().getResource("Application_UI.fxml"));
			Scene scene = new Scene(root, 800, 500);
			primary_stage.setScene(scene);
			primary_stage.show();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
	}

	private void showSplash(
			final Stage init_stage,
			Task<?> task,
			InitCompletionHandler init_completion_handler)
	{
		lbl_progress_status.textProperty().bind(task.messageProperty());
		progress_bar.progressProperty().bind(task.progressProperty());

		task.stateProperty().addListener(
				(observableValue, oldState, newState) ->
				{
					if(newState == Worker.State.SUCCEEDED)
					{
						progress_bar.progressProperty().unbind();
						progress_bar.setProgress(1);

						init_stage.toFront();

						FadeTransition fade_transition =
								new FadeTransition(
										Duration.seconds(1.2),
										splash_layout);
						fade_transition.setFromValue(1.0);
						fade_transition.setToValue(0.0);
						fade_transition.setOnFinished(
								actionEvent -> init_stage.hide());
						fade_transition.play();

						init_completion_handler.complete();
					}
				});

		Scene splash_scene = new Scene(splash_layout, Color.TRANSPARENT);

		final Rectangle2D bounds = Screen.getPrimary().getBounds();

		init_stage.setScene(splash_scene);

		init_stage.setX(
				bounds.getMinX() +
				bounds.getWidth() / 2 - SPLASH_WIDTH / 2);

		init_stage.setY(
				bounds.getMinY() +
				bounds.getHeight() / 2 - SPLASH_HEIGHT / 2);

		init_stage.initStyle(StageStyle.TRANSPARENT);
		init_stage.setAlwaysOnTop(true);
		init_stage.show();
	}

	public interface InitCompletionHandler
	{
		void complete();
	}

}

