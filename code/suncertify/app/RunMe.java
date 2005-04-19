/*
 * RunMe.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.app;

import suncertify.rmi.RemoteData;
import suncertify.ui.RunMeFrame;
import suncertify.db.Data;
import suncertify.db.DB;

import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;
import java.util.Properties;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.FileOutputStream;

/**
 * RunMe is the driver for the runme application.  Based on the mode parameter it either starts the application
 * in server mode (starts only the server), standalone mode (starts both the server and client), or the default
 * mode (starts only the client).
 *
 * @author Richard Abbuhl
 * @version 1.0.0, April 19, 2005
 */
public class RunMe {

    private static final int SERVER_MODE = 1;
    private static final int STANDALONE_MODE = 2;
    private static final int CLIENT_MODE = 3;

    private static int mode = CLIENT_MODE;

    /**
     * Define a constant for the properties file filename.
     */
    public static final String PROPERTIES_FILE = "suncertify.properties";

    /**
     * Gets the property value from the properties file identified by key.
     * @param key key of the property value to be returned.
     * @param defaultValue default value to be returned if the key does not exist in the properties file.
     * @return the property value from the properties file identified by key.
     */
    public static String getProperty(String key, String defaultValue) {
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
    public static void setProperty(String key, String value) {
        Properties properties = new Properties();

        // Load the properties file.
        try {
            properties.load(new FileInputStream(PROPERTIES_FILE));
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        }

        // Update the desired properties.
        properties.setProperty(key, value);

        // Save the properties file.
        try {
            properties.store(new FileOutputStream(PROPERTIES_FILE), null);
        } catch (IOException e) {
            System.out.println("Exception " + e.toString());
        }
    }

    /**
     * Starts the server and listens for requests on the default port (1099).
     */
    public void createServer() {
        try {
            System.out.println("Creating a local RMI registry on the default port.");
            Registry localRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

            System.out.println("Creating local object and remote adapter.");
            String localDBPath = RunMe.getProperty("localdb-path", "db-2x2.db");
            System.out.println("Using DB location " + localDBPath);
            Data adaptee = new Data(localDBPath);
            RemoteData adapter = new RemoteData(adaptee);

            System.out.println("Publishing service \"" + DB.SERVICENAME + "\" in local registry.");
            localRegistry.rebind(DB.SERVICENAME, adapter);

            System.out.println("Published RemoteDB as service \"" + DB.SERVICENAME + "\". Ready.");

        } catch (RemoteException e) {
            System.out.println("Problem starting the server" + e);
            System.exit(0);
        }
    }

    /**
     * Show the command-line options to the user.
     */
    public static void showUsage() {
        System.out.println("Usage: java -jar runme.jar [server|alone]");
        System.out.println("  server - indicates server mode and that the server must run.");
        System.out.println("  alone - indicates standalone mode and that both the client and server must run.");
        System.out.println("  default mode indicates client mode and that the client must run.");
    }

    /**
     * Driver for the RunMe application.
     *
     * @param args command-line arguments, i.e. either "server", "alone", or no parameters for default mode.
     */
    public static void main(String[] args) {
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
         * Show the usage if the user did not follow the command-line conventions and then exit.
         */
        if (displayUsage) {
            showUsage();
            return;
        }

        /**
         * Start the RunMe application in the specified mode.
         */
        if (mode == SERVER_MODE) {

            /**
             * Start the application in server mode (starts the server).  This mode allow remote clients to connect 
             * to the DB.
             */
            RunMe app = new RunMe();
            app.createServer();

        } else {

            /**
             * Schedule a job for the event-dispatching thread:  creating and showing this application's GUI.
             */
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    RunMeFrame frame = new RunMeFrame();
                    if (mode == CLIENT_MODE) {
                        /**
                         * Start the application in the default mode (starts the client).  The DB connection is remote.
                         */
                        frame.setDbRemote(true);
                    } else {
                        /**
                         * Start the application in standalone mode (starts the client).  The DB connection is local.
                         */
                        frame.setDbRemote(false);
                    }
                    frame.createUI();
                }
            });
        }
    }
}
