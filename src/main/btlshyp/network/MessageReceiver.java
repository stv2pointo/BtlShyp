package main.btlshyp.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import main.btlshyp.controller.Controller;
import main.btlshyp.message.*;
import main.btlshyp.network.exception.NetCommunicationException;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;

import static main.btlshyp.message.MessageType.*;

/**
 * Handles the receiving of new messages from the connected server and forwards them to the Controller
 */
@Slf4j
public class MessageReceiver implements Runnable {

  private final Gson gson = new Gson();

  private final Socket socket;
  private final BufferedReader input;
  private final Controller controller;

  public MessageReceiver(Socket socket, BufferedReader input, Controller controller) {
    this.socket = socket;
    this.input = input;
    this.controller = controller;
  }

  /**
   * Continually listens and attempts to read new messages from the server to forward to the Controller
   */
  @Override
  public void run() {
    while (socket.isConnected()) {
      String newMessageString = readNextMessageString();
      determineTypeThenHandoffToController(newMessageString);
    }
  }

  /**
   * Reads the next message from the input. Blocks until a new message is read.
   *
   * @return The Message which was read
   *
   * @throws NetCommunicationException if unable to read from the input
   */
  public String readNextMessageString() {
    try {
      log.info("Listening for message");
      String messageString = input.readLine();
      log.info("Read from input: {}", messageString);
      return messageString;
    } catch (IOException e) {
      log.error("Unable to read from input.", e);
      throw new NetCommunicationException("Unable to read from input.", e);
    }
  }

  /**
   * Determines the type of message which was received from the server. Once the type of message is determined a new
   * concrete version of the specific message type is created and sent back to the controller to be handled as part
   * of the game's logic cycle.
   *
   * @param newMessageString The string of JSON received from the server
   */
  public void determineTypeThenHandoffToController(String newMessageString) {
    log.info("Determining Message Type");
    
    JsonParser typeParser = new JsonParser();
    JsonObject messageStringJsonObject = typeParser.parse(newMessageString).getAsJsonObject();
    JsonElement messageType = messageStringJsonObject.get("type");
    
    // We have received an application type message from the server
    if (messageType.getAsString().equals("application")){
      log.info("Application Message Received. {}", newMessageString);
      handleApplicationMessage(newMessageString);
      
    // We have received a chat type message from the server
    } else if (messageType.getAsString().equals("chat")) {
      log.info("Global Chat Message Received. {}", newMessageString);
      handleGlobalChatMessage(newMessageString);
      
   // We have received an unknown type message from the server 
    } else {
      log.error("Uknown Message Type Received From Server. Ignoring and returning null.");
    }
  }

  private void handleApplicationMessage(String applicationMessage) {

    if (applicationMessage.contains("BtlShyp")) {
      JsonParser parser = new JsonParser();
      JsonObject jsonObject = parser.parse(applicationMessage).getAsJsonObject();

      JsonElement messageJsonElement = jsonObject.get("message");

      if (applicationMessage.contains(MessageType.CHAT.name())) {
        ChatMessage message = gson.fromJson(messageJsonElement, ChatMessage.class);
        log.info("ChatMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(JOIN_ATTEMPT.name())) {
        JoinAttemptMessage message = gson.fromJson(messageJsonElement, JoinAttemptMessage.class);
        log.info("JoinAttempt Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(JOIN_RESPONSE.name())) {
        JoinResponseMessage message = gson.fromJson(messageJsonElement, JoinResponseMessage.class);
        log.info("JoinResponseMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(GAME_START.name())) {
        GameStartMessage message = gson.fromJson(messageJsonElement, GameStartMessage.class);
        log.info("GameStartMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(SHIPS_PLACED.name())) {
        ShipsPlacedMessage message = gson.fromJson(messageJsonElement, ShipsPlacedMessage.class);
        log.info("ShipsPlacedMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(TURN.name())) {
        TurnStartMessage message = gson.fromJson(messageJsonElement, TurnStartMessage.class);
        log.info("TurnMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(ATTACK_ATTEMPT.name())) {
        AttackAttemptMessage message = gson.fromJson(messageJsonElement, AttackAttemptMessage.class);
        log.info("AttackAttemptMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(ATTACK_RESPONSE.name())) {
        AttackResponseMessage message = gson.fromJson(messageJsonElement, AttackResponseMessage.class);
        log.info("AttackResponseMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(GAME_WON_ATTEMPT.name())) {
        GameWonAttemptMessage message = gson.fromJson(messageJsonElement, GameWonAttemptMessage.class);
        log.info("GameWonAttemptMessage Received. {}", message);
        controller.handleMessage(message);
      } else if (applicationMessage.contains(GAME_WON_RESPONSE.name())) {
        GameWonResponseMessage message = gson.fromJson(messageJsonElement, GameWonResponseMessage.class);
        log.info("GameWonResponseMessage Received. {}", message);
        controller.handleMessage(message);
      } else {
        log.error("Unknown BtlShyp Message Type received! Ignoring!");
      }
    } else {
      log.info("Non-BtlShyp Message Received. Ignoring and returning null.");
    }
 }


  private void handleGlobalChatMessage(String globalChatMessage) {
    JsonParser parser = new JsonParser();
    JsonObject jsonObject = parser.parse(globalChatMessage).getAsJsonObject();
    
    String username = jsonObject.get("fromUser").getAsString();
    String text = jsonObject.get("message").getAsString();

    ChatMessage broadcastMessage = new ChatMessage(text, username);
    log.info("Global ChatMessage Received. {}", text);
    controller.handleMessage(broadcastMessage);
  }
}
