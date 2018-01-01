package com.softwareological.JNA;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class App_Control {

	@FXML Button btn_about;
	
	@FXML protected void handleBtnAbout(ActionEvent e)
	{
		System.out.println("Connected?");
	}
}
