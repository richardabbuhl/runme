package suncertify;

import suncertify.network.RemoteDataAdapter;

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
public class runme {

    private static final int SERVER_MODE = 1;
    private static final int STANDALONE_MODE = 2;
    private static final int CLIENT_MODE = 3;


    private static String labelPrefix = "Number of button clicks: ";
    private int numClicks = 0;

    public Component createComponents() {
        final JLabel label = new JLabel(labelPrefix + "0    ");

        JButton button = new JButton("I'm a Swing button!");
        button.setMnemonic('i');
        button.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                numClicks++;
                label.setText(labelPrefix + numClicks);
            }
        });
        label.setLabelFor(button);

        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        JPanel pane = new JPanel();
        pane.setBorder(BorderFactory.createEmptyBorder(30, //top
                30, //left
                10, //bottom
                30) //right
        );
        pane.setLayout(new GridLayout(0, 1));
        pane.add(button);
        pane.add(label);

        return pane;
    }

    public void createServer(String hostName) {
        try {
            String name = "//" + hostName + "/RemoteData";
            RemoteDataAdapter data = (RemoteDataAdapter) Naming.lookup(name);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }

    }

    public void createUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        //Create the top-level container and add contents to it.
        JFrame frame = new JFrame("SwingApplication");
        Component contents = this.createComponents();
        frame.getContentPane().add(contents, BorderLayout.CENTER);

        //Finish setting up the frame, and show it.
        frame.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        frame.pack();
        frame.setVisible(true);
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
        runme app = new runme();
        if (mode == SERVER_MODE) {

            app.createServer("localhost");

        } else if (mode == CLIENT_MODE) {

            app.createUI();

        }
    }
}
