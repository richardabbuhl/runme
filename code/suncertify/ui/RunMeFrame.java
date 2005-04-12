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
import java.util.*;
import java.util.List;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 * RunMeFrame implements the user interface using Swing components.
 *
 * @author Richard Abbuhl
 * @version 1.00, April 12, 2005
 */
public class RunMeFrame extends JFrame {

    /**
     * Define a constant for the properties file for the user interface.
     */
    private static final String PROPERTIES_FILE = "suncertify.properties";

    /**
     * Define a constant for the rec number and customer holding columns in the user interface.
     */
    private static final int COL_REC_NUM = 0;
    private static final int COL_CUST_HOLD = 6;

    /**
     * Define values for the the column names.
     */
    private static final String[] columnNames = {"Record Num",
                                                 "Subcontractor Name",
                                                 "City",
                                                 "Types of work performed",
                                                 "Number of staff in organization",
                                                 "Hourly charge",
                                                 "Customer holding"};

    /**
     * Define a boolean which indicates if the database connection is remote.
     */
    private boolean dbRemote = false;

    /**
     * Define the components for the user interface.
     */
    private JMenuItem optionsMenuItem;
    private JMenuItem exitMenuItem;
    private JTextField subcontractorName = new JTextField();
    private JTextField subcontractorCity = new JTextField();
    private Button searchButton = new Button("Search");
    private JTextField bookCity = new JTextField();
    private Button bookButton = new Button("Save");
    private JTable resultsTable = new JTable();

    /**
     * Define a class that allows data read from the customer's database to be displayed using a JTable
     * in the user interface.
     */
    private class MyTableModel extends AbstractTableModel {

        /**
         * Define a list to hold the data for all of the values in the JTable.
         */
        private java.util.List modelList = new Vector();

        /**
         * Define a constructor which allows a list to be specified that contains the model data.
         * @param modelList list that contains the model data.
         */
        MyTableModel(List modelList) {
            super();
            this.modelList = modelList;
        }

        /**
         * Gets the name of the column.
         * @param colIndex index of the column name to be returned.
         * @return the name of the column.
         */
        public String getColumnName(int colIndex) {
            return columnNames[colIndex];
        }

        /**
         * Gets a count of all of the columns.
         * @return the count of all of the columns.
         */
        public int getColumnCount() {
            return columnNames.length;
        }

        /**
         * Gets a count of all of the rows.
         * @return the count of all of the rows.
         */
        public int getRowCount() {
            return modelList.size();
        }

        /**
         * Gets the model data defined for a row and column.
         * @param rowIndex row for the model data to be returned.
         * @param colIndex column for the model data to be returned.
         * @return the model data defined for a row and column.
         */
        public Object getValueAt(int rowIndex, int colIndex) {
            return ((String[]) modelList.get(rowIndex))[colIndex];
        }

        /**
         * Sets the model data for a row and column.
         * @param aValue value to be updated.
         * @param rowIndex row for the model data to be updated.
         * @param colIndex column for the model data to be updated.
         */
        public void setValueAt(Object aValue, int rowIndex, int colIndex) {
            ((String[]) modelList.get(rowIndex))[colIndex] = (String) aValue;
        }

        /**
         * Returns true if the model data for a row and column is editable by the user.
         * @param row row of the model data to be edited.
         * @param col column of the model data to be edited.
         * @return true if the model data for a row and column is editable by the user.
         */
        public boolean isCellEditable(int row, int col) {
            return false;
        }
    }

    /**
     * Gets the property value from the properties file identified by key.
     * @param key key of the property value to be returned.
     * @param defaultValue default value to be returned if the key does not exist in the properties file.
     * @return the property value from the properties file identified by key.
     */
    private String getProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(PROPERTIES_FILE));
            value = properties.getProperty(key, defaultValue);
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        }
        return value;
    }

    /**
     * Sets the properties value in the properties files identified by key.
     * @param key key of the property value to be updated.
     * @param value value of the property to be updated.
     */
    private void setProperty(String key, String value) {
        Properties properties = new Properties();
        try {
            properties.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        }
        properties.setProperty(key, value);
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error writing " + PROPERTIES_FILE + " " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Returns a instance of the database based upon the value of dbRemote.  If dbRemote is true then a remote
     * database connection will be established.  Otherwise, a local database connection will be established.
     * @return a database connection or null if the connection was not allowed.
     */
    private DB getDB() {
        DB data = null;
        try {
            if (dbRemote) {
                String remoteHost = getProperty("remote-host", "localhost");
                Registry remoteRegistry = LocateRegistry.getRegistry(remoteHost);
                data = (DB) remoteRegistry.lookup(DB.SERVICENAME);
            } else {
                String localDBPath = getProperty("localdb-path", "db-2x2.db");
                data = new Data(localDBPath);
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error reading " + PROPERTIES_FILE + " " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
        }
        return data;
    }

    /**
     * Returns true if the database connection is remote.
     * @return true if the database connection is remote.
     */
    public boolean isDbRemote() {
        return dbRemote;
    }

    /**
     * Set a boolean value which indicates if the database connection is remote.
     * @param dbRemote true if the database connection is remote.
     */
    public void setDbRemote(boolean dbRemote) {
        this.dbRemote = dbRemote;
    }

    /**
     * Sets up the menu bar so it can be added to the user interface..
     * @return a menu bar.
     */
    private JMenuBar addMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Build the File menu.
        JMenu menu = new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);

        // Build the options menu item.
        optionsMenuItem = new JMenuItem("Options", KeyEvent.VK_O);
        optionsMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        optionsMenuItem.getAccessibleContext().setAccessibleDescription("Options menu item");
        menu.add(optionsMenuItem);

        // a group of JMenuItems
        exitMenuItem = new JMenuItem("Exit", KeyEvent.VK_E);
        exitMenuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_E, ActionEvent.ALT_MASK));
        exitMenuItem.getAccessibleContext().setAccessibleDescription("Exit");
        menu.add(exitMenuItem);

        return menuBar;
    }

    /**
     * Creates the listeners for menu items.
     */
    private void createMenuBarListeners() {
        optionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dbRemote) {
                    String remoteHost = getProperty("remote-host", "localhost");
                    String result = (String) JOptionPane.showInputDialog(null,
                            "Remote host:",
                            "Options",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            remoteHost);

                    if (result != null && !"".equals(result)) {
                        setProperty("remote-host", result);
                        JOptionPane.showMessageDialog(null, "Remote host updated to " + result,
                                "alert", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {

                    String localDBPath = getProperty("localdb-path", "db-2x2.db");
                    String result = (String) JOptionPane.showInputDialog(null,
                            "Local DB Path:",
                            "Options",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            localDBPath);

                    if (result != null && !"".equals(result)) {
                        setProperty("localdb-path", result);
                        JOptionPane.showMessageDialog(null, "Local DB Path updated to " + result,
                                "alert", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });
    }

    /**
     * Sets up the search and booking components so they can be added to the user interface..
     * @return the search and booking components.
     */
    private JPanel addSearchBookComponents() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(0, 1));
        pane.add(new JLabel("Subcontractor Name:"));
        pane.add(subcontractorName);
        pane.add(new JLabel("Subcontractor City:"));
        pane.add(subcontractorCity);
        pane.add(searchButton);
        pane.add(new JSeparator());
        pane.add(new JLabel("Customer Holding:"));
        pane.add(bookCity);
        pane.add(bookButton);

        return pane;
    }

    /**
     * Calls the find on the DB interface to return a list records that matched the current value of the subcontractor
     * name and subcontractor city.
     * @return a list records that matched the current value of the subcontractor name and subcontractor city.
     */
    private List matchTest() {
        List matchList = new Vector();
        try {
            String[] d = {"", "", "", "", "", ""};

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
                    String[] result = data.read(matches[i]);
                    String[] fullResults = new String[result.length + 1];
                    fullResults[0] = String.valueOf(matches[i]);
                    for (int j = 0; j < result.length; j++) {
                        fullResults[j + 1] = result[j].trim();
                    }
                    matchList.add(fullResults);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error reading DB " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
        }

        return matchList;
    }

    /**
     * Returns true if the string s contains only digits.
     * @param s string to check
     * @return true if the string s contains only digits.
     */
    private boolean hasOnlyDigits(String s) {
        for (int i = 0; i < s.length(); i++) {
            if (!Character.isDigit(s.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Creates the listeners for search and booking components..
     */
    private void createSearchBookListeners() {
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                List matchList = matchTest();
                resultsTable.setModel(new MyTableModel(matchList));
                if (matchList.size() > 0) {
                    ListSelectionModel selectionModel = resultsTable.getSelectionModel();
                    selectionModel.setSelectionInterval(0, 0);
                }
            }
        });

        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                int rowIndex = resultsTable.getSelectedRow();
                if (rowIndex != -1) {
                    String newCustomerHold = bookCity.getText().trim();
                    boolean valid = false;
                    if ("".equals(newCustomerHold)) {
                        valid = true;
                    } else if (newCustomerHold.length() == 8) {
                        valid = hasOnlyDigits(newCustomerHold);
                    }

                    if (!valid) {
                        JOptionPane.showMessageDialog(null,
                                "Customer holding must be either an 8-digit customer number to book or empty to unbook.",
                                "alert", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    try {
                        DB data = getDB();
                        int recNo = Integer.parseInt((String) resultsTable.getModel().getValueAt(rowIndex, COL_REC_NUM));
                        String currentCustomerHold = (String) resultsTable.getModel().getValueAt(rowIndex, COL_CUST_HOLD);
                        System.out.println("Update started recNo " + recNo + " customer to " + newCustomerHold);
                        boolean doUpdate = true;
                        String[] currentValues = data.read(recNo);
                        if (!currentValues[5].trim().equals(currentCustomerHold)) {

                            int result = JOptionPane.showConfirmDialog(null,
                                    "Customer holding was recently booked by another CSR to " + currentValues[5].trim() +
                                    ". Click YES to book customer holding to " + newCustomerHold,
                                    "alert", JOptionPane.YES_NO_OPTION);

                            if (result == JOptionPane.NO_OPTION) {
                                System.out.println("Rollback recNo " + recNo + " customer to " + currentValues[5].trim());

                                MyTableModel resultsModel = (MyTableModel) resultsTable.getModel();
                                resultsModel.setValueAt(currentValues[5].trim(), rowIndex, COL_CUST_HOLD);
                                resultsModel.fireTableCellUpdated(rowIndex, COL_CUST_HOLD);

                                doUpdate = false;
                            }
                        }

                        if (doUpdate) {
                            String[] d = {null, null, null, null, null, newCustomerHold};
                            long cookie = data.lock(recNo);
                            data.update(recNo, d, cookie);
                            data.unlock(recNo, cookie);
                            System.out.println("Update commited recNo " + recNo + " customer to " + newCustomerHold);

                            MyTableModel resultsModel = (MyTableModel) resultsTable.getModel();
                            resultsModel.setValueAt(newCustomerHold, rowIndex, COL_CUST_HOLD);
                            resultsModel.fireTableCellUpdated(rowIndex, COL_CUST_HOLD);

                            if ("".equals(newCustomerHold)) {
                                JOptionPane.showMessageDialog(null, "Unbooked Record Num " + recNo,
                                        "alert", JOptionPane.INFORMATION_MESSAGE);
                            } else {
                                JOptionPane.showMessageDialog(null, "Booked Record Num " + recNo + " to customer " +
                                        newCustomerHold, "alert", JOptionPane.INFORMATION_MESSAGE);
                            }
                        }

                    } catch (Exception ex) {
                        System.out.println("Exception " + e.toString());
                        JOptionPane.showMessageDialog(null, "Error writing DB " + ex.toString(),
                                "alert", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "No customer data is being edited. Click on a customer holding,\n" +
                            "change the value and then press then book button.",
                            "alert", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
    }

    /**
     * Sets up the JTable components so they can be added to the user interface..
     * @return the JTable components.
     */
    private JScrollPane addTableComponents() {
        // Create the scroll pane and add the table to it.
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setModel(new MyTableModel(new Vector()));
        resultsTable.setPreferredScrollableViewportSize(new Dimension(800, 500));
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // Return the scroll pane.
        return scrollPane;
    }

    /**
     * Creates the listeners for the JTable.
     */
    private void createTableListeners() {
        resultsTable.addMouseListener(new MouseListener() {
            public void mouseClicked(MouseEvent e) {
                int rowIndex = resultsTable.getSelectedRow();
                if (rowIndex != -1) {
                    bookCity.setText((String) resultsTable.getModel().getValueAt(rowIndex, COL_CUST_HOLD));
                }
            }

            public void mousePressed(MouseEvent e) {
            }

            public void mouseReleased(MouseEvent e) {
            }

            public void mouseEntered(MouseEvent e) {
            }

            public void mouseExited(MouseEvent e) {
            }
        });
    }

    /**
     * Creates the user interface for the client.
     */
    public void createUI() {
        // Create the top-level container and add contents to it.
        setTitle("Sun Certified Developer for the Java 2 Platform: Application Submission");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(addMenuBar());
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(addSearchBookComponents());
        getContentPane().add(addTableComponents());

        createMenuBarListeners();
        createSearchBookListeners();
        createTableListeners();

        pack();
        setVisible(true);
    }
}
