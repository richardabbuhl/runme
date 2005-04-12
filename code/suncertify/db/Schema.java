/*
 * Schema.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

/**
 * Schema holds the database schema for the customer's non-relational database file.
 * @version 1.00, April 12, 2005
 * @author Richard Abbuhl
 */
public class Schema {

    private int magicCookie;
    private int offset;
    private short numFields;
    private Field[] fields;

    public int getMagicCookie() {
        return magicCookie;
    }

    public void setMagicCookie(int magicCookie) {
        this.magicCookie = magicCookie;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public short getNumFields() {
        return numFields;
    }

    public void setNumFields(short numFields) {
        this.numFields = numFields;
    }

    public int getLengthAllFields() {
        int length = 0;
        for (int i = 0; i < this.numFields; i++) {
            length += this.fields[i].getLength();
        }
        return length;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void dump() {
        System.out.println("Mc=" + magicCookie + ",offset=" + offset + ",nf=" + numFields);
    }
}
