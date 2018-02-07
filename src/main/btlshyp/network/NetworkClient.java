package main.btlshyp.network;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import main.btlshyp.controller.Controller;
import main.btlshyp.message.ApplicationMessage;
import main.btlshyp.message.LoginMessage;
import main.btlshyp.message.Message;
import main.btlshyp.network.exception.ClientServerConnectionException;

import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

import static main.btlshyp.message.ApplicationMessage.ApplicationMessageType.ACKNOWLEDGE;
import static main.btlshyp.message.ApplicationMessage.ApplicationMessageType.LOGIN;

/**
 * Handles the setting up of connections and sockets to the game server and sends and receives messages to and from the
 * server.
 */
@Slf4j
public class NetworkClient {

  private static final int SERVER_CONNECT_TIMEOUT = 2_000;

  private final String serverIPAddress;
  private final int serverPort;

  private Gson gson = new Gson();

  private Socket socket;
  private BufferedReader input;
  private PrintWriter output;

  private Controller controller;

  private MessageReceiver messageReceiver;

  /**
   * Constructs a new NetworkClient.
   *
   * @param serverIPAddress The IP Address used to connect to the server
   * @param serverPort The port the server is running on
   * @param controller The {@link Controller} which will receive callbacks on when a message is received from the server
   */
  public NetworkClient(String serverIPAddress, int serverPort, Controller controller) {
    this.serverIPAddress = serverIPAddress;
    this.serverPort = serverPort;
    this.controller = controller;
  }

  /**
   * Attempts to connect to the configured server.
   */
  public void connectToServer() {
    attemptConnection();

    if (socket != null && socket.isConnected()) {
      initializeInputAndOutput();
    }

    log.info("Connection to server established.");
  }

  public void beginListeningForMessages() {
    log.info("Starting message receiver thread.");
    this.messageReceiver = new MessageReceiver(socket, input, controller);
    new Thread(messageReceiver).start();
  }

  /**
   * Attempt to connect to the server using the previously configured values
   */
  public void attemptConnection() {
    try {
      socket = new Socket();
      socket.connect(new InetSocketAddress(serverIPAddress, serverPort), SERVER_CONNECT_TIMEOUT);
      log.info("Connected to server. serverIPAddress={} serverPort={}", serverIPAddress, serverPort);
    } catch (IOException e) {
      log.error("Unable to connect to configured server. serverIPAddress={} serverPort={}",
          serverIPAddress, serverPort, e);
      throw new ClientServerConnectionException("Unable to connect to configured server.", e);
    }
  }

  /**
   * Sets up the Reader and Writer for the Input and Output streams
   */
  public void initializeInputAndOutput() {
    try {
      input = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    } catch (IOException e) {
      log.error("Unable to open Input Stream.", e);
      throw new ClientServerConnectionException("Unable to initialize input reader.", e);
    }

    try {
      output = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()), true);
    } catch (IOException e) {
      log.error("Unable to open Output Stream.", e);
      throw new ClientServerConnectionException("Unable to initialize output writer.", e);
    }
  }

  /**
   * Sends a json encoded message to the server
   */
  public void sendMessageToServer(Message messageToSend) {
    try {
      log.info("Sending message: {}", messageToSend);

      ApplicationMessage wrappedMessage = new ApplicationMessage(messageToSend);
      String jsonEncodedMessage = gson.toJson(wrappedMessage);

      output.println(jsonEncodedMessage);
      output.flush();

      log.info("Message sent to Server. Message json: {}", jsonEncodedMessage);
    } catch (Exception e) {
      log.error("Error sending message: {} to server", messageToSend, e);
      throw new ClientServerConnectionException("Unable to send message to server.", e);
    }
  }

  /**
   * Creates and sends a {@link LoginMessage} to the Server
   *
   * @return {@code true} if the login was successful, {@code false} otherwise
   */
  public boolean loginToServer(String username) {
    LoginMessage loginMessage = new LoginMessage(username);
    return attemptLoginWithServer(loginMessage);
  }

  /**
   * Sends a json encoded login message to the server
   */
  public boolean attemptLoginWithServer(LoginMessage loginMessage) {
    try {
      log.info("Sending login message: {}", loginMessage);

      ApplicationMessage wrappedMessage = new ApplicationMessage(LOGIN.toString(), loginMessage);
      String jsonEncodedMessage = gson.toJson(wrappedMessage);

      output.println(jsonEncodedMessage);
      output.flush();

      log.info("Login message sent to Server");

      log.info("Listening for login responseMessage json: {}", jsonEncodedMessage);
      String loginResponseString = input.readLine();
      log.info("Read from input: {}", loginResponseString);

      JsonParser parser = new JsonParser();
      JsonObject loginResponseJson = parser.parse(loginResponseString).getAsJsonObject();

      JsonElement response = loginResponseJson.get("type");
      JsonElement responseMessage = loginResponseJson.get("message");

      if (response.getAsString().equals(ACKNOWLEDGE.toString())) {
        log.info("Login Successful. Response: {}", responseMessage);
        return true;
      } else {
        log.error("Login Failed! Response: {}", responseMessage);
        return false;
      }

    } catch (Exception e) {
      log.error("Error sending login message: {} to server", loginMessage, e);
      throw new ClientServerConnectionException("Unable to send login message to server.", e);
    }
  }
}

