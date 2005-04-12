/*
 * RecordNotFoundException.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

/**
 * RecordNotFoundException is an exceptions that can be thrown by the DB interface.  It
 * is thrown whenever an attempt is made to lock, read, update, or delete a specified
 * record that does not exist or is marked as deleted in the database file.
 * @version 1.00, April 12, 2005
 * @author Richard Abbuhl
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}
