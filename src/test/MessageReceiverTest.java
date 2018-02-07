package test;

import static main.btlshyp.message.AttackResponseMessage.HitOrMiss.HIT;
import static main.btlshyp.message.AttackResponseMessage.ShipSunk.BATTLESHIP;
import static main.btlshyp.message.GameWonResponseMessage.GameResult.WIN;
import static main.btlshyp.message.JoinResponseMessage.ConfirmJoin.ACCEPT;
import static main.btlshyp.message.TurnStartMessage.Turn.START;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.samePropertyValuesAs;
import static org.hamcrest.core.Is.isA;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.io.BufferedReader;
import java.net.Socket;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;

import com.google.gson.Gson;

import main.btlshyp.controller.Controller;
import main.btlshyp.message.ApplicationMessage;
import main.btlshyp.message.AttackAttemptMessage;
import main.btlshyp.message.AttackResponseMessage;
import main.btlshyp.message.ChatMessage;
import main.btlshyp.message.GameStartMessage;
import main.btlshyp.message.GameWonAttemptMessage;
import main.btlshyp.message.GameWonResponseMessage;
import main.btlshyp.message.JoinAttemptMessage;
import main.btlshyp.message.JoinResponseMessage;
import main.btlshyp.message.Message;
import main.btlshyp.message.ShipsPlacedMessage;
import main.btlshyp.message.TurnStartMessage;
import main.btlshyp.model.Coordinate;
import main.btlshyp.network.MessageReceiver;

public class MessageReceiverTest {

  private MessageReceiver fixture;

  private final Gson gson = new Gson();

  @Mock
  private Socket mockSocket;

  @Mock
  private BufferedReader mockInput;

  @Mock
  private Controller mockController;

  private static final String USERNAME = "Testy McTester";
  private static final Coordinate COORDINATE = new Coordinate(2,3);

  @Before
  public void setUp() {
    initMocks(this);
    fixture = new MessageReceiver(mockSocket, mockInput, mockController);
  }

  @Test
  public void run() throws Exception {
    ChatMessage message = new ChatMessage("Test chat message", USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    when(mockInput.readLine()).thenReturn(messageJson);
    when(mockSocket.isConnected()).thenReturn(true).thenReturn(false); // Only say that it is connected the first time so we don't loop forever

    ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

    fixture.run();

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void readNextMessageString_ReturnsMessageJson() throws Exception {
    ChatMessage message = new ChatMessage("Test chat message", USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    when(mockInput.readLine()).thenReturn(messageJson);

    String result = fixture.readNextMessageString();
    assertThat(result, is(messageJson));
  }

  @Test
  public void determineTypeThenHandoffToController_ChatMessage() throws Exception {
    ChatMessage message = new ChatMessage("Test chat message", USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<ChatMessage> captor = ArgumentCaptor.forClass(ChatMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_AttackAttemptMessage() throws Exception {
    AttackAttemptMessage message = new AttackAttemptMessage(USERNAME, COORDINATE);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<AttackAttemptMessage> captor = ArgumentCaptor.forClass(AttackAttemptMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_AttackResponseMessage() throws Exception {
    AttackResponseMessage message = new AttackResponseMessage(USERNAME, HIT, BATTLESHIP, COORDINATE);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<AttackResponseMessage> captor = ArgumentCaptor.forClass(AttackResponseMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_GameStartMessage() throws Exception {
    GameStartMessage message = new GameStartMessage(USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<GameStartMessage> captor = ArgumentCaptor.forClass(GameStartMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_GameWonAttemptMessage() throws Exception {
    GameWonAttemptMessage message = new GameWonAttemptMessage(USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<GameWonAttemptMessage> captor = ArgumentCaptor.forClass(GameWonAttemptMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_GameWonResponseMessage() throws Exception {
    GameWonResponseMessage message = new GameWonResponseMessage(USERNAME, WIN);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<GameWonResponseMessage> captor = ArgumentCaptor.forClass(GameWonResponseMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_JoinAttemptMessage() throws Exception {
    JoinAttemptMessage message = new JoinAttemptMessage(USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<JoinAttemptMessage> captor = ArgumentCaptor.forClass(JoinAttemptMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_JoinResponseMessage() throws Exception {
    JoinResponseMessage message = new JoinResponseMessage(ACCEPT);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<JoinResponseMessage> captor = ArgumentCaptor.forClass(JoinResponseMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_ShipsPlacedMessage() throws Exception {
    ShipsPlacedMessage message = new ShipsPlacedMessage(USERNAME);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<ShipsPlacedMessage> captor = ArgumentCaptor.forClass(ShipsPlacedMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_TurnStartMessage() throws Exception {
    TurnStartMessage message = new TurnStartMessage(USERNAME, START);
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);
    String messageJson = gson.toJson(wrappedMessage);

    ArgumentCaptor<TurnStartMessage> captor = ArgumentCaptor.forClass(TurnStartMessage.class);

    fixture.determineTypeThenHandoffToController(messageJson);

    verify(mockController).handleMessage(captor.capture());
    assertThat(captor.getValue(), isA(Message.class));
    assertThat(captor.getValue(), samePropertyValuesAs(message));
  }

  @Test
  public void determineTypeThenHandoffToController_NonBtlShypMessage_ReturnsNull() throws Exception {
    ChatMessage message = new ChatMessage("Test chat message", USERNAME);
    message.setModule("Something_Else"); // Something not "BtlShyp"
    ApplicationMessage wrappedMessage = new ApplicationMessage(message);

    String messageJson = gson.toJson(wrappedMessage);

    fixture.determineTypeThenHandoffToController(messageJson);

    verifyZeroInteractions(mockController);
  }
  
  @Test
  public void determineTypeThenHandoffToController_NonBtlShypTypeMessage_ReturnsNull() throws Exception {
    ChatMessage message = new ChatMessage("Test chat message", USERNAME);
    message.setModule("Something_Else"); // Something not "BtlShyp"
    // Unwrapped message to mimic a server message that is not Application or GlobalChat

    String messageJson = gson.toJson(message);

    fixture.determineTypeThenHandoffToController(messageJson);

    verifyZeroInteractions(mockController);
  }

}