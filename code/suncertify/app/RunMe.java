/*
 * RunMe.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.app;

import suncertify.network.RemoteDataAdapter;
import suncertify.network.RemoteDB;
import suncertify.ui.RunMeFrame;
import suncertify.db.Data;

import java.rmi.Naming;
import java.rmi.RMISecurityManager;
import java.rmi.RemoteException;
import java.rmi.registry.Registry;
import java.rmi.registry.LocateRegistry;

/**
 * RunMe is the driver for the runme application.  Based on the mode parameter it either starts the application
 * in server mode (starts the server), standalone mode (starts the server and client), or the default mode (starts
 * the client).
 * @version 1.00
 * @author Richard Abbuhl
 */
public class RunMe {

    private static final int SERVER_MODE = 1;
    private static final int STANDALONE_MODE = 2;
    private static final int CLIENT_MODE = 3;

    /**
     * Start the server.
     * @param hostName hostname to user for starting the server.
     */
    public void createServer(String hostName) {
//        if (System.getSecurityManager() == null) {
//            System.setSecurityManager(new RMISecurityManager());
//        }
//
//        try {
//            String name = "//" + hostName + "/RemoteData";
//            RemoteDataAdapter data = (RemoteDataAdapter) Naming.lookup(name);
//        } catch (Exception e) {
//            System.err.println("Exception: " + e.getMessage());
//            e.printStackTrace();
//        }


        try {
              System.out.println("Creating a local RMI registry on the default port.");
              Registry localRegistry = LocateRegistry.createRegistry(Registry.REGISTRY_PORT);

              System.out.println("Creating local object and remote adapter.");
              Data adaptee = new Data("db-2x2.db");
              RemoteDataAdapter adapter = new RemoteDataAdapter(adaptee);

              System.out.println("Publishing service \"" + RemoteDB.SERVICENAME + "\" in local registry.");
              localRegistry.rebind(RemoteDB.SERVICENAME, adapter);

              System.out.println("Published RemoteDB as service \"" + RemoteDB.SERVICENAME + "\". Ready.");

           } catch (RemoteException e) {
              System.out.println( "Problem with remote object" + e );
           }
    }

    /**
     * Show the command-line options to the user.
     */
    private static void showUsage() {
        System.out.println("Usage: java -jar runme.jar [server|alone]");
        System.out.println("  server - indicates server mode and that the server must run.");
        System.out.println("  alone - indicates standalone mode and that both the client and server must run.");
        System.out.println("  default mode indicates client mode and that the client must run.");
    }

    /**
     * Driver for the RunMe application.
     * @param args command-line arguments.
     */
    public static void main(String[] args) {
        int mode = CLIENT_MODE;
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

            RunMe app = new RunMe();
            app.createServer("localhost");

        } else if (mode == CLIENT_MODE) {

            //Schedule a job for the event-dispatching thread:  creating and showing this application's GUI.
            javax.swing.SwingUtilities.invokeLater(new Runnable() {
                public void run() {
                    RunMeFrame frame = new RunMeFrame();
                    frame.createUI();
                }
            });
        }
    }
}
