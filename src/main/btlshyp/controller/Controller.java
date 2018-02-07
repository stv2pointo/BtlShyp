package main.btlshyp.controller;

import lombok.extern.slf4j.Slf4j;
import main.btlshyp.message.*;
import main.btlshyp.model.Coordinate;
import main.btlshyp.model.Model;
import main.btlshyp.model.Model.AttackResult;
import main.btlshyp.model.Ship;
import main.btlshyp.model.ShipType;
import main.btlshyp.network.NetworkClient;
import main.btlshyp.view.View;
import main.btlshyp.view.event.*;

import static main.btlshyp.Main.SERVER_IP_ADDRESS;
import static main.btlshyp.Main.SERVER_PORT;
import static main.btlshyp.message.AttackResponseMessage.HitOrMiss.HIT;
import static main.btlshyp.message.AttackResponseMessage.HitOrMiss.MISS;
import static main.btlshyp.message.AttackResponseMessage.ShipSunk.*;
import static main.btlshyp.message.GameWonResponseMessage.GameResult.WIN;

/**
 * The "Controller" (or "Presenter" or "Mediator") in the MVC architecture which orchestrates the game play logic and
 * flow, communication with the server and other client, and updating of the view and model.
 */
@Slf4j
public class Controller {

  private static final int GAME_THROTTLE = 250;

  private View view;
  private Model model;
  private NetworkClient networkClient;

  private ChatListener chatListener;
  private SetShipListener setShipListener;
  private AttackListener attackListener;

  private GameState gameState;

  // Storage for events coming back from the view
  private Ship placedShip;
  private Coordinate attackCoordinate;

  /**
   * A Controller must be initialized with an implementation of the {@link View} interface. This is necessary because
   * of requirements of the assignment
   *
   * @param view An implementation of the {@link View} interface which the controller will update throughout the game.
   */
  public Controller(View view) {
    this.view = view;
    gameState = GameState.NEW;
  }

  /**
   * Sets the game state. Should ALWAYS be used to modify the current game state as this method performs additional
   * logic before setting the state to ensure things such as not attempting to start the game twice by accident.
   *
   * @param newGameState The {@link GameState} which will potentially be set as the current game state
   */
  private void setGameState(GameState newGameState) {
    // If the game is already started and we happen to get the Joined message after that we don't want to go back to Joined
    if (this.gameState == GameState.STARTED && newGameState == GameState.JOINED) {
      return;
    }

    log.info("Game state set to {}", newGameState);
    this.gameState = newGameState;
  }

  /**
   * Initialized the Controller and all of its various components (such as the {@link Model} and {@link NetworkClient})
   * to prepare for the game to start
   */
  public void init() {
    // Initialize Model
    model = new Model();

    // Initialize View
    view.resetGame();
    initListeners();

    // Initialize NetworkClient
    networkClient = new NetworkClient(SERVER_IP_ADDRESS, SERVER_PORT, this);
  }

  /**
   * Initializes and registers the event listeners with the View
   */
  public void initListeners() {

    this.chatListener = new ChatListener() {
      @Override
      public void chatEventOccurred(ChatEvent ce) {
        String chatText = ce.getChat();
        log.info("ChatEvent received into Controller. Message: {}", chatText);
        networkClient.sendMessageToServer(new ChatMessage(chatText, model.getUserName()));
      }
    };
    view.registerChatListener(chatListener);

    this.setShipListener = new SetShipListener() {
      @Override
      public void setShipEventOccurred(SetShipEvent sse) {
        log.info("SetShipEvent received into Controller. Populated Ship: {}", sse.getShip());
        placedShip = sse.getShip();
      }
    };
    view.registerSetShipListener(setShipListener);

    this.attackListener = new AttackListener() {
      @Override
      public void attackEventOccurred(AttackEvent ae) {
        Coordinate coordinate = ae.getCoordinate();
        log.info("AttackEvent received into Controller. Coordinate: {}", coordinate);
        attackCoordinate = coordinate;
      }
    };
    view.registerAttackListener(attackListener);
  }

  /**
   * Begins a game (from the client's perspective). This begins the various looping which takes place to handle the game
   * logic.
   */
  public void playGame() {
    while (gameState != GameState.DONE) {
      switch (gameState) {
        case NEW:
          connectToServer();
          break;
        case CONNECTED:
          loginToServer();
          break;
        case LOGGED_IN:
          joinGame();
          break;
        case JOINED:
          view.displayNotification("Game Joined. Waiting for game to start.");
          setGameState(GameState.WAITING);
          break;
        case STARTED:
          view.displayNotification("Game has started.");
          placeShips();
          break;
        case TURN_ME:
          performAttack();
          break;
        case TURN_THEM:
          waitForOpponentTurn();
          break;
        case WIN:
          view.displayNotification("\nYou won! Victory tastes so sweet!\n");
          resetGame();
          setGameState(GameState.LOGGED_IN);
          break;
        case LOSE:
          view.displayNotification("You lost... Better luck next time.");
          resetGame();
          setGameState(GameState.LOGGED_IN);
          break;
        case WAITING:
          // Do nothing... Just wait...
          break;
      }

      // Throttle so we don't start churning through CPU cycles
      sleep(GAME_THROTTLE);
    }

    if (gameState == GameState.DONE) {
      resetGame();
    }
  }

  /**
   * Attempts to connect to the server.
   */
  private void connectToServer() {
    networkClient.connectToServer();
    log.info("Connected to Server");
    setGameState(GameState.CONNECTED);
  }

  /**
   * Attempts to log in to the server. If there is an error in connecting, it will re-prompt the user for a different
   * username and attempt to log in again until it is successful.
   */
  private void loginToServer() {
    model.setUserName(view.getUsername());

    boolean loginSuccess = networkClient.loginToServer(model.getUserName());
    while (!loginSuccess) {
      view.displayNotification("Unable to log in to server. Username already in use.");
      model.setUserName(view.getUsername());
      loginSuccess = networkClient.loginToServer(model.getUserName());
    }
    log.info("Logged into server.");
    setGameState(GameState.LOGGED_IN);
    networkClient.beginListeningForMessages();
  }

  /**
   * Attempts to join a new game on the server.
   */
  public void joinGame() {
    log.info("Attempting to Join a new game");
    JoinAttemptMessage joinAttemptMessage = new JoinAttemptMessage(model.getUserName());
    networkClient.sendMessageToServer(joinAttemptMessage);
    setGameState(GameState.WAITING);
  }

  /**
   * Resets the view and the model so that another game can be played
   */
  private void resetGame() {
    log.info("Resetting the game");
    view.resetGame();
    model = new Model();
    setGameState(GameState.CONNECTED);
  }

  /**
   * Coordinates the placing of ships by the {@link View} and storing them inside the {@link Model}.
   */
  private void placeShips() {
    log.info("Placing ships");

    placeShip(new Ship(ShipType.BATTLESHIP));
    placeShip(new Ship(ShipType.DESTROYER));
    placeShip(new Ship(ShipType.PATROLBOAT));
    placeShip(new Ship(ShipType.SUBMARINE));

    log.info("All Ships placed successfully");
    view.displayNotification("All Ships have been placed. Waiting for other player to place their Ships.");
    ShipsPlacedMessage shipsPlacedMessage = new ShipsPlacedMessage(model.getUserName());
    networkClient.sendMessageToServer(shipsPlacedMessage);

    setGameState(GameState.WAITING);
  }

  /**
   * Coordinates the placing of a {@link Ship} on the game board. This method triggers the {@link View} to populate a
   * Ship with coordinates then the Ship is validated then sent to the Model to be placed on the game board. If the Ship
   * or its placement are invalid then the user is prompted to try again. Once the Ship is known to be valid it is
   * given to the View to be displayed.
   *
   * @param ship The ship which is to be populated by the View then placed in the Board.
   */
  private void placeShip(Ship ship) {
    log.info("Asking view to populate ship. Ship to populate: {}", ship);
    placedShip = null;

    view.setShip(ship);

    log.info("Waiting for player to place ship...");

    // Wait until they populate it and we get the message back
    while (placedShip == null) {
      sleep(GAME_THROTTLE);
    }

    log.info("Player has placed ship. Placed ship: {}", placedShip);

    // Verify the Ship is valid and that it's placement on the board is valid. If it isn't, hve the user try again.
    boolean validShip = placedShip.isValid();
    boolean validPlacement = model.isShipPlacementValid(placedShip);

    while (!validShip || !validPlacement) {
      log.warn("Ship placement invalid. validShip={}, validPlacement={}", validShip, validPlacement);
      placedShip = null;

      view.displayNotification("Invalid ship placement.\n" +
        "Ships must be vertical or horizontal.\n" +
        "Ships must occupy their own space.");

      view.setShip(ship);

      log.info("Waiting for player to place ship (again)...");

      // Wait until they populate it and we get the message back
      while (placedShip == null) {
        sleep(GAME_THROTTLE);
      }

      log.info("Player has placed ship (again). Placed ship: {}", placedShip);

      validShip = placedShip.isValid();
      validPlacement = model.isShipPlacementValid(placedShip);
    }

    model.placeShip(placedShip);
    log.info("Ship placed successfully. Ship: {}", placedShip);
    view.displayShip(placedShip);
  }

  /**
   * Coordinates and attack against the opponent using the {@link View} and storing the attempt in the {@link Model}.
   */
  private void performAttack() {
    log.info("Performing Attack");
    attackCoordinate = null;

    view.yourTurn();

    // Wait until they perform an attack
    while (attackCoordinate == null) {
      sleep(GAME_THROTTLE);
    }

    log.info("Player is attempting to attack {}", attackCoordinate);

    networkClient.sendMessageToServer(new AttackAttemptMessage(model.getUserName(), attackCoordinate));

    log.info("Attack complete");
    setGameState(GameState.WAITING);
  }

  /**
   * Ensures the {@link View} knows it is not our turn
   */
  private void waitForOpponentTurn() {
    view.notYourTurn();
    setGameState(GameState.WAITING);
  }

  public void handleMessage(AttackAttemptMessage message) {
    log.info("Controller received AttackAttempt message: {}", message);

    Coordinate attackCoordinate = message.getCoordinate();
    log.info("Attack attempted at {}", attackCoordinate);
    AttackResult attackResults = model.getAttackResults(attackCoordinate);
    log.info("Attack Results: {}", attackResults);

    // Translate between enums
    Ship hitShip = attackResults.ship;
    AttackResponseMessage.ShipSunk shipSunk = NONE;
    if (hitShip != null) {
      switch (hitShip.getShipType()) {
        case DESTROYER:
          shipSunk = DESTROYER;
          break;
        case BATTLESHIP:
          shipSunk = BATTLESHIP;
          break;
        case PATROLBOAT:
          shipSunk = PATROL_BOAT;
          break;
        case SUBMARINE:
          shipSunk = SUBMARINE;
          break;
        default:
          shipSunk = NONE;
          break;
      }
    }

    // Create the appropriate attack response
    AttackResponseMessage responseMessage = new AttackResponseMessage(
      model.getUserName(),
      attackResults.isHit ? HIT : MISS,
      attackResults.isSunk ? shipSunk : NONE,
      message.getCoordinate());

    // Display the attack
    view.displayOpponentAttack(responseMessage);

    // Send the attack response to the server
    networkClient.sendMessageToServer(responseMessage);

    setGameState(GameState.WAITING);
  }

  public void handleMessage(AttackResponseMessage message) {
    log.info("Controller received AttackResponse message: {}", message);

    model.storeResultOfAttackOnOpponent(message.getHitOrMiss() == HIT, message.getCoordinate());

    if (message.getShipSunk() != NONE) {
      log.info("Ship was sunk: {}", message.getShipSunk());
//      model.storeSunkenOpponentShip(message.getShipSunk()); // TODO: This needs to accept a ShipSunk instead of a Ship
    }

    view.displayAttack(message);

    if (model.isGameWon()) {
      log.info("Game was won! Sending an attempt message to the other client to confirm.");
      networkClient.sendMessageToServer(new GameWonAttemptMessage(model.getUserName()));
      view.displayNotification("Please wait while we validate your win...");
      setGameState(GameState.WAITING);
    } else {
      log.info("Attack response stored. End of turn.");
      setGameState(GameState.TURN_THEM);
      TurnStartMessage endTurn = new TurnStartMessage(model.getUserName(), TurnStartMessage.Turn.END);
      networkClient.sendMessageToServer(endTurn);
    }
  }

  public void handleMessage(ChatMessage message) {
    log.info("Controller received Chat message: {}", message);
    view.displayChat(message.getUsername(), message.getText());
  }

  public void handleMessage(GameStartMessage message) {
    log.info("Controller received GameStart message: {}", message);
    model.setOppUserName(message.getOpponentUsername());
    setGameState(GameState.STARTED);
  }

  public void handleMessage(GameWonAttemptMessage message) {
    log.info("Controller received GameWonAttempt message: {}", message);

    boolean gameLost = model.isGameLost();

    if (gameLost) {
      log.info("Other player won. We've lost.");
      networkClient.sendMessageToServer(new GameWonResponseMessage(model.getUserName(), WIN));
      view.displayNotification("Other player won. We've lost...");
      view.resetGame();
    } else {
      log.info("Game not lost. There is still hope.");
      // TODO: There needs to be a response which can represent a "False Alarm" state. WIN or LOSE don't fit this usecase well.
    }
  }

  public void handleMessage(GameWonResponseMessage message) {
    log.info("Controller received GameWonResponse message: {}", message);
    switch (message.getGameResult()) {
      case WIN:
        log.info("Game won!");
        setGameState(GameState.WIN);
        break;
      case LOSE:
        log.info("Game lost...");
        setGameState(GameState.LOSE);
        break;
    }
  }

  public void handleMessage(JoinAttemptMessage message) {
    log.info("Controller received JoinAttempt message: {}", message);
    // Do nothing. The server handles these. There isn't currently anything the clients do if they get one.
  }

  public void handleMessage(JoinResponseMessage message) {
    log.info("Controller received JoinResponse message: {}", message);
    if (message.getConfirmJoin() == JoinResponseMessage.ConfirmJoin.ACCEPT) {
      setGameState(GameState.JOINED);
    } else {
      log.error("Unable to join game. Resetting Game.");
      setGameState(GameState.DONE);
    }
  }

  public void handleMessage(LoginMessage message) {
    log.info("Controller received Login message: {}", message);
    // Do nothing. The server handles these. There isn't currently anything the clients do if they get one.
  }

  public void handleMessage(ShipsPlacedMessage message) {
    log.info("Controller received ShipsPlaced message: {}", message);
    // Do nothing. The server handles these. There isn't currently anything the clients do if they get one.
  }

  public void handleMessage(TurnStartMessage message) {
    log.info("Controller received TurnStart message: {}", message);
    setGameState(GameState.TURN_ME);
  }

  /**
   * Sleep for the specified number of milliseconds
   */
  private void sleep(int sleepMillis) {
    try {
      Thread.sleep(sleepMillis);
    } catch (InterruptedException e) {
      log.error("Thread interrupted!", e);
    }
  }

}
