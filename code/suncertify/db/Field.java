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
 * represents the databse schema.
 * @version 1.00, April 12, 2005
 * @author Richard Abbuhl
 */
public class Field {

    private String name = new String();
    private short length;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public short getLength() {
        return length;
    }

    public void setLength(short length) {
        this.length = length;
    }

    public void dump() {
        System.out.println("Name=" + name + ",length=" + length);
    }

}
