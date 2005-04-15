/*
 * Field.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

/**
 * Field represent a column in the customer's non-relational database file.  A collection of fields
 * represents the database schema.
 *
 * @author Richard Abbuhl
 * @version 1.00, April 12, 2005
 */
public class Field {

    /**
     * The name of the column in the database.
     */
    private String name;

    /**
     * The length of the column in the database.
     */
    private short length;

    /**
     * Gets the name of the column.
     *
     * @return the name of the column.
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the column.
     *
     * @param name the name of the column.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Returns the length of the column.
     *
     * @return the length of the column.
     */
    public short getLength() {
        return length;
    }

    /**
     * Sets the length of the column.
     *
     * @param length the length of the column.
     */
    public void setLength(short length) {
        this.length = length;
    }

    /**
     * Dump the value of all attributes.
     */
    public void dump() {
        System.out.println("Name=" + name + ",length=" + length);
    }

}
