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
 * DuplicateKeyException is one the exceptions that can be thrown by the DB interface.  It
 * is thrown whenever an attempt is made to insert an occuppied record in the database.
 * @version 1.00, April 12, 2005
 * @author Richard Abbuhl
 */
public class DuplicateKeyException extends Exception {
    public DuplicateKeyException() {
    }

    public DuplicateKeyException(String message) {
        super(message);
    }
}
