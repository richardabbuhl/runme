package suncertify.ui;

import suncertify.db.Data;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Vector;

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

    public JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu, submenu;
        JMenuItem menuItem;
        JRadioButtonMenuItem rbMenuItem;
        JCheckBoxMenuItem cbMenuItem;

        //Create the menu bar.
        menuBar = new JMenuBar();

        //Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription(
                "The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("A text-only menu item",
                                 KeyEvent.VK_T);
        //menuItem.setMnemonic(KeyEvent.VK_T); //used constructor instead
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_1, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription(
                "This doesn't really do anything");
        menu.add(menuItem);

        //ImageIcon icon = createImageIcon("images/middle.gif");
        //menuItem = new JMenuItem("Both text and icon", icon);
        //menuItem.setMnemonic(KeyEvent.VK_B);
        //menu.add(menuItem);

        //menuItem = new JMenuItem(icon);
        //menuItem.setMnemonic(KeyEvent.VK_D);
        //menu.add(menuItem);

        //a group of radio button menu items
        menu.addSeparator();
        ButtonGroup group = new ButtonGroup();

        rbMenuItem = new JRadioButtonMenuItem("A radio button menu item");
        rbMenuItem.setSelected(true);
        rbMenuItem.setMnemonic(KeyEvent.VK_R);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        rbMenuItem = new JRadioButtonMenuItem("Another one");
        rbMenuItem.setMnemonic(KeyEvent.VK_O);
        group.add(rbMenuItem);
        menu.add(rbMenuItem);

        //a group of check box menu items
        menu.addSeparator();
        cbMenuItem = new JCheckBoxMenuItem("A check box menu item");
        cbMenuItem.setMnemonic(KeyEvent.VK_C);
        menu.add(cbMenuItem);

        cbMenuItem = new JCheckBoxMenuItem("Another one");
        cbMenuItem.setMnemonic(KeyEvent.VK_H);
        menu.add(cbMenuItem);

        //a submenu
        menu.addSeparator();
        submenu = new JMenu("A submenu");
        submenu.setMnemonic(KeyEvent.VK_S);

        menuItem = new JMenuItem("An item in the submenu");
        menuItem.setAccelerator(KeyStroke.getKeyStroke(
                KeyEvent.VK_2, ActionEvent.ALT_MASK));
        submenu.add(menuItem);

        menuItem = new JMenuItem("Another item");
        submenu.add(menuItem);
        menu.add(submenu);

        //Build second menu in the menu bar.
        menu = new JMenu("Another Menu");
        menu.setMnemonic(KeyEvent.VK_N);
        menu.getAccessibleContext().setAccessibleDescription(
                "This menu does nothing");
        menuBar.add(menu);

        return menuBar;
    }

    private Component createComponents() {
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
        pane.setBorder(BorderFactory.createEmptyBorder(50, //top
                50, //left
                10, //bottom
                50) //right
        );
        pane.setLayout(new GridLayout(0, 1));
        pane.add(button);
        pane.add(label);

        return pane;
    }

    private Vector matchTest(Data data) {
        Vector v = new Vector();
        try {
            String [] d = new String[6];
            int[] matches = data.find(d);
            if (matches != null) {
                for (int i = 0; i < matches.length; i++) {
                    String [] o = data.read(matches[i]);
                    v.add(o);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
        }

        return v;
    }

    private void addComponents() {
        String[] columnNames = {"Subcontractor Name",
                                "City",
                                "Types of work performed",
                                "Number of staff in organization",
                                "Hourly charge",
                                "Customer holding this record"};
        Data d = new Data("db-2x2.db");

        Vector o = matchTest(d);
        Object[][] c = new Object[o.size()][6];
        for (int i = 0; i < o.size(); i++) {
            String [] t = (String[])o.get(i);
            for (int j = 0; j < 6; j++) {
                c[i][j] = t[j].trim();
            }
        }

        final JTable table = new JTable(c, columnNames);
        table.setPreferredScrollableViewportSize(new Dimension(800, 600));

        //Create the scroll pane and add the table to it.
        JScrollPane scrollPane = new JScrollPane(table);

        //Add the scroll pane to this panel.
        getContentPane().add(scrollPane);
    }

    public void createUI() {
        // Create the top-level container and add contents to it.
        setTitle("SwingApplication");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        addComponents();

        pack();
        setVisible(true);
    }


}
