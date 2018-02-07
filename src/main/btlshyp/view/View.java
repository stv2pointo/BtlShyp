package main.btlshyp.view;

import lombok.extern.slf4j.Slf4j;
import main.btlshyp.view.event.AttackListener;
import main.btlshyp.view.event.ChatListener;
import main.btlshyp.view.event.SetShipListener;

import java.awt.event.ActionEvent;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

import main.btlshyp.message.*;
import main.btlshyp.model.Ship;

/**
 * An interface representing the necessary functions for the GUI of the BtlShyp game.
 */
@Slf4j
public class View extends JFrame{
  protected ChatListener chatListener;
  protected SetShipListener setShipListener;
  protected AttackListener attackListener;
  protected Ship shipToPlace;
  
  public View() {
    super();
  }

  public void registerChatListener(ChatListener listener) {
    this.chatListener = listener;
  }

  public void registerSetShipListener(SetShipListener listener) {
    this.setShipListener = listener;
  }

  public void registerAttackListener(AttackListener listener) {
    this.attackListener = listener;
  }
  
	/**
	 * Displays the results of your attack attempt on your opponent. Analogous to putting a pin in the 
	 * vertical portion of the traditional board game.
	 * @param hitOrMiss
	 * @param coordinate
	 */
	public void displayAttack(AttackResponseMessage message) {};
	
	/**
	 * Displays the results of an opponent's attack on you. Analogous to putting a pin in a boat
	 * or the sea on the horizontal part of the traditional board game.
	 * @param hitOrMiss
	 * @param coordinate
	 */
	public void displayOpponentAttack(AttackResponseMessage message) {};
	
	/**
	 * Displays community chat messages that are broadcast to all
	 * @param chat
	 */
	public void displayChat(String user, String chat) {};
	
	/**
	 * Displays notifications unique to the user, such as "Please re-place your boats", "Waiting for opponent", etc
	 */
	public void displayNotification(String text) {};
	
	/**
	 * Sends controller a coordinate to attack
	 */
	public void sendAttack(ActionEvent e) {};
	
	/** 
	 * Emits ChatEvent for controller to catch which includes a string message to send out to the world
	 */
	public void sendChat(ActionEvent e) {};
	
	/**
	 * Prompts user for name and returns to controller
	 */
	public String getUsername() {
		log.info("Collecting username from user");
		String username = JOptionPane.showInputDialog(null, "Enter username: ");

		// If the username is null then the user clicked the Cancel button or closed the dialog box, so exit the application
		if (username == null) {
			log.info("User closed username prompt. Closing application.");
			System.exit(-1);
		}

		// Don't allow a blank username
		while (username.isEmpty()) {
			JOptionPane.showMessageDialog(null, "Username cannot be blank.");
			username = JOptionPane.showInputDialog(null, "Enter username: ");

			// If the username is null then the user clicked the Cancel button or closed the dialog box, so exit the application
			if (username == null) {
				log.info("User closed username prompt. Closing application.");
				System.exit(-1);
			}
		}

		log.info("User chose username {}", username);
		return username;
	  }
	
	/** 
	 * Unlocks the inputs on the game portion of the gui
	 * Prompts user for attack
	 */
	public void yourTurn() {};
	
	/**
	 * Locks game gui, displays wait message
	 */
	public void notYourTurn() {};
	
	/**
	 * Receives ship from controller for user to place
	 * @param ship
	 */
	public void setShip(Ship ship) {
	  this.shipToPlace = ship;
	};
	
	/**
	 *Displays a properly placed ship in our board
	 * @param ship
	 */
	public void displayShip(Ship ship) {};
	
	/**
	 * Collects coordinates into a ship and emits a setShipEvent
	 */
	public void attemptSetShip(ActionEvent e) {};
	/**
	 * Resets gui to gameless state
	 */
	public void resetGame() {};
	
}
