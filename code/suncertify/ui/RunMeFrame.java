package suncertify.ui;

import suncertify.db.Data;

import javax.swing.*;
import javax.swing.table.TableModel;
import javax.swing.table.AbstractTableModel;
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

    private Data data = new Data("db-2x2.db");
    private JTextField subcontractorName = new JTextField();
    private Button searchButton = new Button("Search");
    private String[] columnNames = {"Subcontractor Name",
                                    "City",
                                    "Types of work performed",
                                    "Number of staff in organization",
                                    "Hourly charge",
                                    "Customer holding this record"};
    JTable resultsTable = new JTable();
    MyTableModel resultsModel = new MyTableModel(new Vector());

    public class MyTableModel extends AbstractTableModel {
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

    public JMenuBar createMenuBar() {
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
            d[0] = subcontractorName.getText().trim().length() > 0 ? subcontractorName.getText().trim() : null;
            System.out.println("subcontractorName = " + subcontractorName.getText().trim());
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
        pane.setBorder(BorderFactory.createEmptyBorder(50, //top
                50, //left
                10, //bottom
                50) //right
        );
        pane.setLayout(new GridLayout());
        pane.add(searchButton);
        pane.add(subcontractorName);

        searchButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Vector o = matchTest();
                resultsTable.setModel(new MyTableModel(o));
//                searchData = new Object[o.size()][6];
//                for (int i = 0; i < o.size(); i++) {
//                    String [] t = (String[])o.get(i);
//                    for (int j = 0; j < 6; j++) {
//                        searchData[i][j] = t[j].trim();
//                    }
//                }
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
