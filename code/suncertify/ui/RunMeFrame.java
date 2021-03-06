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
import suncertify.app.RunMe;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

/**
 * RunMeFrame implements the user interface using Swing components.
 *
 * @author Richard Abbuhl
 * @version 1.0.0, April 19, 2005
 */
public class RunMeFrame extends JFrame {

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
     * Returns a instance of the database based upon the value of dbRemote.  If dbRemote is true then a remote
     * database connection will be established.  Otherwise, a local database connection will be established.
     * @return a database connection or null if the connection was not allowed.
     */
    private DB getDB() {
        DB data = null;
        try {
            if (dbRemote) {
                // If a remote connection to the database is required then use RMI.
                String remoteHost = RunMe.getProperty("remote-host", "localhost");
                Registry remoteRegistry = LocateRegistry.getRegistry(remoteHost);
                data = (DB) remoteRegistry.lookup(DB.SERVICENAME);

            } else {

                // If a local connection to the database is required then use the Data class.
                String localDBPath = RunMe.getProperty("localdb-path", "db-2x2.db");
                data = new Data(localDBPath);
            }

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error reading " + RunMe.PROPERTIES_FILE + " " + e.toString(),
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

        // If the user select options then allow then to change the database connection property.  For remote
        // connections this is either localhost or the ip address of the server.  For local connections this
        // is the path of the database file.
        optionsMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                if (dbRemote) {
                    String remoteHost = RunMe.getProperty("remote-host", "localhost");
                    String result = (String) JOptionPane.showInputDialog(null,
                            "Remote host:",
                            "Options",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            remoteHost);

                    if (result != null && !"".equals(result)) {
                        RunMe.setProperty("remote-host", result);
                        JOptionPane.showMessageDialog(null, "Remote host updated to " + result,
                                "alert", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else {

                    String localDBPath = RunMe.getProperty("localdb-path", "db-2x2.db");
                    String result = (String) JOptionPane.showInputDialog(null,
                            "Local DB Path:",
                            "Options",
                            JOptionPane.PLAIN_MESSAGE,
                            null,
                            null,
                            localDBPath);

                    if (result != null && !"".equals(result)) {
                        RunMe.setProperty("localdb-path", result);
                        JOptionPane.showMessageDialog(null, "Local DB Path updated to " + result,
                                "alert", JOptionPane.INFORMATION_MESSAGE);
                    }
                }
            }
        });

        // If the user selects exit then shutdown the user interface.
        exitMenuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
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

            // If both the subcontractor name and city are specified then search for both.  If neither are specfied
            // then do search for all records by specifying null for the subcontractor name.
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

            // Perform the search and return a list of records.  Add an extra column which contains the record
            // number so that it can be used for updated to easily find the record.
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
     * Try and update the update the customer holding for the currently selected record.  Before allowing the
     * update to continue the record is checked for a dirty update (another user changed the value).
     * @param rowIndex current selected row.
     * @param newCustomerHold new value for the customer holding.
     */
    private void updateCustomerHolding(int rowIndex, String newCustomerHold) {
        try {
            DB data = getDB();
            int recNo = Integer.parseInt((String) resultsTable.getModel().getValueAt(rowIndex, COL_REC_NUM));
            String currentCustomerHold = (String) resultsTable.getModel().getValueAt(rowIndex, COL_CUST_HOLD);
            System.out.println("Update started recNo " + recNo + " customer to " + newCustomerHold);

            // Check for a dirty update.
            boolean doUpdate = true;
            String[] currentValues = data.read(recNo);
            if (!currentValues[5].trim().equals(currentCustomerHold)) {

                int result = JOptionPane.showConfirmDialog(null,
                        "Customer holding was recently booked by another CSR to " + currentValues[5].trim() +
                        ". Click YES to book customer holding to " + newCustomerHold,
                        "alert", JOptionPane.YES_NO_OPTION);

                if (result == JOptionPane.NO_OPTION) {
                    System.out.println("Rollback recNo " + recNo + " customer to " + currentValues[5].trim());

                    // Update the row with the current value from the database.
                    MyTableModel resultsModel = (MyTableModel) resultsTable.getModel();
                    resultsModel.setValueAt(currentValues[5].trim(), rowIndex, COL_CUST_HOLD);
                    resultsModel.fireTableCellUpdated(rowIndex, COL_CUST_HOLD);

                    doUpdate = false;
                }
            }

            // Update the customer holding for the currently selected record.
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

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error writing DB " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * Creates the listeners for search and booking components..
     */
    private void createSearchBookListeners() {

        // If the user presses the search button then update the table model and redisplay the table.
        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                List matchList = matchTest();
                resultsTable.setModel(new MyTableModel(matchList));
                if (matchList.size() > 0) {
                    ListSelectionModel selectionModel = resultsTable.getSelectionModel();
                    selectionModel.setSelectionInterval(0, 0);
                }
            }
        });

        // If the user presses the book button then try to update the customer holding.
        bookButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {

                // Updates are allowed only when a row is selected.
                int rowIndex = resultsTable.getSelectedRow();
                if (rowIndex != -1) {

                    // Validate the customer holding value.
                    String newCustomerHold = bookCity.getText().trim();
                    boolean validCustomerHolding = false;
                    if ("".equals(newCustomerHold)) {
                        validCustomerHolding = true;
                    } else if (newCustomerHold.length() == 8) {
                        validCustomerHolding = hasOnlyDigits(newCustomerHold);
                    }

                    // If the entered value for customer holding is valid then update the record.
                    if (validCustomerHolding) {

                        // Try to update the record.
                        updateCustomerHolding(rowIndex, newCustomerHold);

                    } else {

                        // Let the user know that the entered value for customer holding is invalid.
                        JOptionPane.showMessageDialog(null,
                                "Customer holding must be either an 8-digit customer number to book or empty to unbook.",
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
