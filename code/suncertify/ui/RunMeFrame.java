/*
 * RunMeFrame.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.ui;

import suncertify.db.Data;
import suncertify.db.DB;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 23, 2005
 * Time: 4:44:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunMeFrame extends JFrame {

    private JTextField subcontractorName = new JTextField();
    private JTextField subcontractorCity = new JTextField();
    private Button searchButton = new Button("Search");
    private String[] columnNames = {"Subcontractor Name",
                                    "City",
                                    "Types of work performed",
                                    "Number of staff in organization",
                                    "Hourly charge",
                                    "Customer holding this record"};
    private JTable resultsTable = new JTable();
    private MyTableModel resultsModel = new MyTableModel(new Vector());
    private boolean dbRemote = true;

    private class MyTableModel extends AbstractTableModel {
        private Vector v = new Vector();

        MyTableModel(Vector v){
            this.v = v;
        }

        public String getColumnName(int colIndex) {
            return columnNames[colIndex];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return (v.size() - 1);
        }

        public Object getValueAt(int rowIndex, int colIndex) {
            return ((String[])v.get(rowIndex + 1))[colIndex];
        }
    }

    private DB getDB() {
        DB data = null;
        try {
            if (dbRemote) {
                Registry remoteRegistry = LocateRegistry.getRegistry("192.168.1.53");
                data = (DB)remoteRegistry.lookup(DB.SERVICENAME);
            } else {
                data = new Data("db-2x2.db");
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.toString());                        
        }
        return data ;
    }

    public boolean isDbRemote() {
        return dbRemote;
    }

    public void setDbRemote(boolean dbRemote) {
        this.dbRemote = dbRemote;
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar;
        JMenu menu;
        JMenuItem menuItem;

        // Create the menu bar.
        menuBar = new JMenuBar();

        // Build the first menu.
        menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("The only menu in this program that has menu items");
        menuBar.add(menu);

        // a group of JMenuItems
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

    private Vector matchTest() {
        Vector v = new Vector();
        try {
            String [] d = { "", "", "", "", "", "" };

            String name = subcontractorName.getText().trim();
            String city = subcontractorCity.getText().trim();
            if (name != null && name.length() > 0) {
                d[0] = name;
                if (city != null && city.length() > 0) {
                    d[1] = city;
                }
            } else if (city != null && city.length() > 0) {
                d[1] = city;
            } else {
                d[0] = null;
            }

            DB data = getDB();
            int[] matches = data.find(d);
            if (matches != null) {
                for (int i = 0; i < matches.length; i++) {
                    String [] result = data.read(matches[i]);
                    for (int j = 0; j < result.length; j++) {
                        result[j] = result[j].trim();
                    }
                    v.add(result);
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
        pane.setBorder(BorderFactory.createEmptyBorder(50, //top
                50, //left
                10, //bottom
                50) //right
        );
        pane.setLayout(new GridLayout());
        pane.add(searchButton);
        pane.add(subcontractorName);
        pane.add(subcontractorCity);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector o = matchTest();
                resultsTable.setModel(new MyTableModel(o));
            }
        });

        return pane;
    }

    private JScrollPane addComponents() {
        // Create the scroll pane and add the table to it.
        resultsTable.setModel(resultsModel);
        resultsTable.setPreferredScrollableViewportSize(new Dimension(800, 600));
        JScrollPane scrollPane = new JScrollPane(resultsTable);

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
