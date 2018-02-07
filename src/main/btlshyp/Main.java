package main.btlshyp;

import lombok.extern.slf4j.Slf4j;
import main.btlshyp.controller.Controller;
import main.btlshyp.view.DefaultView;

@Slf4j
public class Main {

  private static final String LOG_FORMAT = "%1$tY-%1$tm-%1$td %1$tH:%1$tM:%1$tS %4$-6s [%2$s] %5$s%6$s%n";

  public static String SERVER_IP_ADDRESS = "ec2-34-224-216-23.compute-1.amazonaws.com";
  public static int SERVER_PORT = 8989;

  /**
   * The main entry point for BtlShyp!
   *
   * @param args Accepts an (optional) server ip address followed by an (optional) server port
   */
  public static void main(String[] args) {
    System.setProperty("java.util.logging.SimpleFormatter.format", LOG_FORMAT);
    log.info("Application Starting");

    if (args.length > 0) {
      handleCommandlineArgs(args);
    }

    DefaultView defaultView = new DefaultView();
    Controller controller = new Controller(defaultView);
    controller.init();
    controller.playGame();
  }

  /**
   * Handles the commandline arguments.
   */
  private static void handleCommandlineArgs(String[] args) {
    log.info("Commandline arguments provided");

    SERVER_IP_ADDRESS = args[0];
    log.info("Server ip address: {}", SERVER_IP_ADDRESS);

    if (args.length > 1) {
      SERVER_PORT = Integer.parseInt(args[1]);
      log.info("Server port: {}", SERVER_PORT);
    }
  }

}