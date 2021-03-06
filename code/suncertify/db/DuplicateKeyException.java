/*
 * DuplicateKeyException.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

/**
 * DuplicateKeyException is an exception that can be thrown by the DB interface.  It
 * is thrown whenever an attempt is made to insert an occuppied record in the database.
 *
 * @author Richard Abbuhl
 * @version 1.0.0, April 19, 2005
 */
public class DuplicateKeyException extends Exception {

    /**
     * Default constructor for DuplicateKeyException.
     */
    public DuplicateKeyException() {
    }

    /**
     * DuplicateKeyException constructor which allows a detailed message to be specified.
     *
     * @param message the detailed message.
     */
    public DuplicateKeyException(String message) {
        super(message);
    }
}
