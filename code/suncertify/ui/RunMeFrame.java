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
        menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
        menuBar.add(menu);

        //a group of JMenuItems
        menuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Exit");
        menu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        return menuBar;
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

    private JPanel addSearchComponents() {
        /*
         * An easy way to put space between a top-level container
         * and its contents is to put the contents in a JPanel
         * that has an "empty" border.
         */
        JPanel pane = new JPanel();
        //pane.setBorder(BorderFactory.createEmptyBorder(50, //top
        //        50, //left
        //        10, //bottom
        //        50) //right
        //);
        //pane.setLayout(new GridLayout(0, 1));
        pane.add(new Button("Hello"));
        //pane.add(label);

        return pane;
    }

    private JScrollPane addComponents() {
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

        // Return the scroll pane.
        return scrollPane;
    }

    public void createUI() {
        // Create the top-level container and add contents to it.
        setTitle("SwingApplication");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(addSearchComponents());
        getContentPane().add(addComponents());

        pack();
        setVisible(true);
    }
}
