package main.btlshyp.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import main.btlshyp.view.event.AttackEvent;
import main.btlshyp.view.event.AttackListener;
import main.btlshyp.view.event.ChatEvent;
import main.btlshyp.view.event.ChatListener;
import main.btlshyp.view.event.SetShipEvent;
import main.btlshyp.view.event.SetShipListener;
import main.btlshyp.message.AttackResponseMessage;
import main.btlshyp.model.Coordinate;
import main.btlshyp.model.Ship;
import main.btlshyp.model.ShipType;

/**
 * A default concrete implementation of the {@link View} interface. This is
 * meant to stand as a simple placeholder and model for each group member's own
 * implementation of the {@link View} interface.
 */
public class DefaultView extends View {

  private final Logger logger = LoggerFactory.getLogger(getClass());
  private JTextArea txtArea;
  private JTextArea inputArea;
  private JButton btnChat;
  private JButton btnSetShip;
  private JButton btnAttack;

  public DefaultView() {
    super();
    // for testing
    this.shipToPlace = new Ship(ShipType.BATTLESHIP);

    setLayout(new BorderLayout());

    this.inputArea = new JTextArea(5, 30);
    inputArea.setFont(new Font("Arial", Font.PLAIN, 20));
    inputArea.setText("Enter a test chat message: ");
    inputArea.setCaretPosition(inputArea.getDocument().getLength());
    this.txtArea = new JTextArea();
    this.btnChat = new JButton("Send Chat");
    this.btnSetShip = new JButton("Set Ship");
    this.btnAttack = new JButton("Attack");

    btnChat.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendChat(e);
      }
    });

    btnSetShip.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        attemptSetShip(e);
      }
    });

    btnAttack.addActionListener(new ActionListener() {
      @Override
      public void actionPerformed(ActionEvent e) {
        sendAttack(e);
      }
    });

    add(btnChat, BorderLayout.NORTH);
    add(btnAttack, BorderLayout.EAST);
    add(btnSetShip, BorderLayout.WEST);
    add(inputArea, BorderLayout.SOUTH);
    add(txtArea, BorderLayout.CENTER);

    setMinimumSize(new Dimension(500, 400));
    setSize(1200, 1000);

    setVisible(true);
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

  } // end ctor

  // for testing
  public void show(String stuff) {
    txtArea.append(stuff + "\n");
  }

  @Override
  public void displayAttack(AttackResponseMessage message) {
    show("Your attack was a " + message.getHitOrMiss().toString() + " at (" + String.valueOf(message.getCoordinate().x)
        + ", " + String.valueOf(message.getCoordinate().y) + ")");
  }

  @Override
  public void displayOpponentAttack(AttackResponseMessage message) {
    show("Opponent fired a " + message.getHitOrMiss().toString() + " at (" + String.valueOf(message.getCoordinate().x)
        + ", " + String.valueOf(message.getCoordinate().y) + ")");
  }

  @Override
  public void displayChat(String user, String chat) {
    show(user + ": " + chat);
  }

  @Override
  public void displayNotification(String text) {
    show(text);
  }

  /**
   * Collect the user's attack choice coordinate Make an attackEvent Plop in the
   * coordinate and let it fly
   */
  @Override
  public void sendAttack(ActionEvent e) {
    Coordinate coordinate = new Coordinate(2, 2);
    AttackEvent ae = new AttackEvent(e, coordinate);
    if (attackListener != null) {
      attackListener.attackEventOccurred(ae);
    }
  }

  /**
   * Collects chat text from an input field Packages the chat msg in a chatEvent
   * and lets it fly
   */
  @Override
  public void sendChat(ActionEvent e) {
    String chat = inputArea.getText();
    ChatEvent chatEvent = new ChatEvent(this, chat);
    if (chatListener != null) {
      chatListener.chatEventOccurred(chatEvent);
    }
  }

  /**
   * Enable input Prompt user for attack
   */
  @Override
  public void yourTurn() {
    show("It is your turn...");
    btnAttack.setEnabled(true);
  }

  /**
   * Disable input Maybe display waiting message
   */
  @Override
  public void notYourTurn() {
    // for testing
    show("Waiting on opponent...");
    try {
      Thread.sleep(1500);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    btnAttack.setEnabled(false);
  }

  /**
   * Receive ship from controller and set the class' shipToSet then prompt the
   * user to place it on the board
   */
  @Override
  public void setShip(Ship ship) {
    // for testing
    show("User, please place this " + ship.toString());
  }

  /**
   * Receives a properly placed ship and displays it on the user's board
   */
  @Override
  public void displayShip(Ship ship) {
    // for testing
    String boatLoc = "Imagine a newly placed ship  at ";
    for (Coordinate c : ship.getShipCoordinates()) {
      boatLoc += "(" + String.valueOf(c.x) + ", " + String.valueOf(c.y) + ") ";
    }
    show(boatLoc);
  }

  /**
   * Wipe view clean
   */
  @Override
  public void resetGame() {
    // for testing
    show("\n\n***  Restarting game ......");
    try {
      Thread.sleep(3000);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    txtArea.setText("");
  }

  /**
   * Collects the user's selected coordinates of where the ship should go sets
   * those coordinates in the ship that the controller gave you pack that ship up
   * in an event and let it fly
   */
  @Override
  public void attemptSetShip(ActionEvent e) {
    Ship ship = shipToPlace;
    ArrayList<Coordinate> coords = new ArrayList<Coordinate>();
    coords.add(new Coordinate(0, 0));
    coords.add(new Coordinate(0, 1));
    coords.add(new Coordinate(0, 2));
    coords.add(new Coordinate(0, 3));
    ship.setShipCoordinates(coords);
    SetShipEvent sse = new SetShipEvent(e, ship);
    if (setShipListener != null) {
      setShipListener.setShipEventOccurred(sse);
    }
  }

}
