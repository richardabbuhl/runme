package suncertify.app;

import suncertify.network.RemoteDataAdapter;
import suncertify.ui.RunMeFrame;

import javax.swing.*;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.*;
import java.rmi.Naming;
import java.rmi.RMISecurityManager;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 21, 2005
 * Time: 5:11:59 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunMe {

    private static final int SERVER_MODE = 1;
    private static final int STANDALONE_MODE = 2;
    private static final int CLIENT_MODE = 3;

    public void createServer(String hostName) {
        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        try {
            String name = "//" + hostName + "/RemoteData";
            RemoteDataAdapter data = (RemoteDataAdapter) Naming.lookup(name);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    private static void showUsage() {
        System.out.println("Usage: java -jar runme.jar [server|alone]");
        System.out.println("  server - indicates server mode and that the server must run.");
        System.out.println("  alone - indicates standalone mode and that both the client and server must run.");
        System.out.println("  default mode indicates client mode and that the client must run.");
    }

    public static void main(String[] args) {
        int mode = CLIENT_MODE;
        boolean displayUsage = false;

        /**
         * Handle the command-line arguments.
         */
        if (args.length == 1) {
            if ("server".equals(args[0])) {
                mode = SERVER_MODE;
            } else if ("alone".equals(args[0])) {
                mode = STANDALONE_MODE;
            } else {
                displayUsage = true;
            }
        } else if (args.length > 1) {
            displayUsage = true;
        }

        /**
         * Show the usage if the user did not follow the command-line conventions.
         */
        if (displayUsage) {
            showUsage();
            return;
        }

        /**
         *
         */
        if (mode == SERVER_MODE) {

            RunMe app = new RunMe();
            app.createServer("localhost");

        } else if (mode == CLIENT_MODE) {

            RunMeFrame frame = new RunMeFrame();
            frame.createUI();
        }
    }
}
