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
        pane.setBorder(BorderFactory.createEmptyBorder(
                                        30, //top
                                        30, //left
                                        10, //bottom
                                        30) //right
                                        );
        pane.setLayout(new GridLayout(0, 1));
        pane.add(button);
        pane.add(label);

        return pane;
    }

    public static void main(String[] args) {
        String mode = null;
        if (args.length > 1) {
            if (args.length == 1) {
                mode = args[0];
            }
        }

/*
        try {
            String name = "//" + args[0] + "/RemoteData";
            RemoteDataAdapter data = (RemoteDataAdapter) Naming.lookup(name);
        } catch (Exception e) {
            System.err.println("Exception: " + e.getMessage());
            e.printStackTrace();
        }
*/

        try {
            UIManager.setLookAndFeel(
                UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) { }

        if (System.getSecurityManager() == null) {
            System.setSecurityManager(new RMISecurityManager());
        }

        //Create the top-level container and add contents to it.
        JFrame frame = new JFrame("SwingApplication");
        runme app = new runme();
        Component contents = app.createComponents();
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
}
