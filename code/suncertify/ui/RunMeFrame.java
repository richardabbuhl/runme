package suncertify.ui;

import javax.swing.*;
import java.rmi.RMISecurityManager;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 23, 2005
 * Time: 4:44:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunMeFrame extends JFrame {

    private static String labelPrefix = "Number of button clicks: ";
    private int numClicks = 0;

    public JMenuBar createMenu() {
        JMenuBar mb = new JMenuBar();
        JMenu fileMenu = new JMenu();
        JMenuItem exitItem = new JMenuItem();

        fileMenu.add("New...");
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        mb.add(fileMenu);

        return mb;
    }

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

    public void createUI() {
        try {
            UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
        } catch (Exception e) {
        }

        // Create the top-level container and add contents to it.
        setTitle("SwingApplication");
        Component contents = createComponents();
        getContentPane().add(contents, BorderLayout.CENTER);

        //Finish setting up the frame, and show it.
        addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });
        pack();
        setVisible(true);
    }


}
