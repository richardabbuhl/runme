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
import javax.swing.text.JTextComponent;
import javax.swing.event.TableModelListener;
import javax.swing.event.TableModelEvent;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.Vector;
import java.util.Properties;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 23, 2005
 * Time: 4:44:40 PM
 * To change this template use File | Settings | File Templates.
 */
public class RunMeFrame extends JFrame {

    private static final String PROPERTIES_FILE = "suncertify.properties";
    private JTextField subcontractorName = new JTextField();
    private JTextField subcontractorCity = new JTextField();
    private Button searchButton = new Button("Search");
    private Button updateButton = new Button("Update");
    private String[] columnNames = {"#",
                                    "Subcontractor Name",
                                    "City",
                                    "Types of work performed",
                                    "Number of staff in organization",
                                    "Hourly charge",
                                    "Customer holding this record"};
    private JTable resultsTable = new JTable();
    private MyTableModel resultsModel = new MyTableModel(new Vector());
    private boolean dbRemote = false;

    private class MyTableModel extends AbstractTableModel {
        private Vector v = new Vector();

        MyTableModel(Vector v){
            super();
            this.v = v;
        }

        public String getColumnName(int colIndex) {
            return columnNames[colIndex];
        }

        public int getColumnCount() {
            return columnNames.length;
        }

        public int getRowCount() {
            return v.size();
        }

        public Object getValueAt(int rowIndex, int colIndex) {
            return ((String[])v.get(rowIndex))[colIndex];
        }

        public void setValueAt(Object aValue, int rowIndex, int colIndex) {
            ((String[])v.get(rowIndex))[colIndex] = (String)aValue;
        }

        public Class getColumnClass(int c) {
            return getValueAt(0, c).getClass();
        }

        public boolean isCellEditable(int row, int col) {
            if (col == 6) {
                return true;
            } else {
                return false;
            }
        }
    }

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

    private DB getDB() {
        DB data = null;
        try {
            if (dbRemote) {
                String remoteHost = getProperty("remote-host", "localhost");
                Registry remoteRegistry = LocateRegistry.getRegistry(remoteHost);
                data = (DB)remoteRegistry.lookup(DB.SERVICENAME);
            } else {
                String localDBPath = getProperty("localdb-path", "db-2x2.db");
                data = new Data(localDBPath);
            }
        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error reading " + PROPERTIES_FILE + " " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
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
        menuItem = new JMenuItem("Options", KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Options");
        menu.add(menuItem);

        menuItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (dbRemote) {
                    String remoteHost = getProperty("remote-host", "localhost");
                    String result = (String)JOptionPane.showInputDialog(
                                                null,
                                                "Local DB Path:",
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
                    String result = (String)JOptionPane.showInputDialog(
                                                null,
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
                    String [] fullResults = new String[result.length + 1];
                    fullResults[0] = String.valueOf(matches[i]);
                    for (int j = 0; j < result.length; j++) {
                        fullResults[j + 1] = result[j].trim();
                    }
                    v.add(fullResults);
                }
            }

        } catch (Exception e) {
            System.out.println("Exception " + e.toString());
            JOptionPane.showMessageDialog(null, "Error reading DB " + e.toString(),
                    "alert", JOptionPane.ERROR_MESSAGE);
        }

        return v;
    }

    private JPanel addSearchComponents() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridLayout(0, 1));
        pane.add(new JLabel("Subcontractor Name:"));
        pane.add(subcontractorName);
        pane.add(new JLabel("Subcontractor City:"));
        pane.add(subcontractorCity);
        pane.add(searchButton);
        pane.add(updateButton);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector o = matchTest();
                resultsTable.setModel(new MyTableModel(o));
            }
        });

        updateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (resultsTable.isEditing()) {
                    String customerHold = ((JTextComponent)resultsTable.getEditorComponent()).getText();
                    int rowIndex = resultsTable.getSelectedRow();
                    int colIndex = resultsTable.getSelectedColumn();
                    resultsTable.getCellEditor().cancelCellEditing();

                    try {
                        DB data = getDB();
                        String recNo = (String)resultsTable.getModel().getValueAt(rowIndex, 0);
                        System.out.println("Update started recNo " + recNo + " customer " + customerHold);
                        long cookie = data.lock(Integer.parseInt(recNo));
                        String [] d = { null, null, null, null, null, customerHold };
                        data.update(Integer.parseInt(recNo), d, cookie);
                        data.unlock(Integer.parseInt(recNo), cookie);
                        System.out.println("Update commited recNo " + recNo + " customer " + customerHold);

                        resultsTable.getModel().setValueAt(customerHold, rowIndex, colIndex);
                        resultsModel.fireTableCellUpdated(rowIndex, colIndex);

                        JOptionPane.showMessageDialog(null, "Updated recNo " + recNo + " customer " + customerHold,
                                "alert", JOptionPane.INFORMATION_MESSAGE);

                    } catch (Exception ex) {
                        System.out.println("Exception " + e.toString());
                        JOptionPane.showMessageDialog(null, "Error writing DB " + ex.toString(),
                                "alert", JOptionPane.ERROR_MESSAGE);
                    }

                } else {
                    JOptionPane.showMessageDialog(null,
                            "No customer data is being edited. Edit a customer record then press update again ",
                            "alert", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        return pane;
    }

    private JScrollPane addTableComponents() {
        // Create the scroll pane and add the table to it.
        resultsTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        resultsTable.setModel(resultsModel);
        resultsTable.setPreferredScrollableViewportSize(new Dimension(800, 500));
        JScrollPane scrollPane = new JScrollPane(resultsTable);

        // Return the scroll pane.
        return scrollPane;
    }

    public void createUI() {
        // Create the top-level container and add contents to it.
        setTitle("Sun Certified Developer for the Java 2 Platform: Application Submission");
        JFrame.setDefaultLookAndFeelDecorated(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setJMenuBar(createMenuBar());
        getContentPane().setLayout(new FlowLayout());
        getContentPane().add(addSearchComponents());
        getContentPane().add(addTableComponents());

        pack();
        setVisible(true);
    }
}
