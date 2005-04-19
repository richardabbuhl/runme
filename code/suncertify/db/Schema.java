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
 *
 * @author Richard Abbuhl
 * @version 1.0.0, April 19, 2005
 */
public class Schema {

    /**
     * Magic cookie for the database.
     */
    private int magicCookie;

    /**
     * Offset to the first record in the database.
     */
    private int offset;

    /**
     * Number of fields in the database.
     */
    private short numFields;

    /**
     * List of all fields in the database.  A field contains the name and length of a column
     * in the database.
     */
    private Field[] fields;

    /**
     * Gets the magic cookie for the database.
     *
     * @return the magic cookie for the database.
     */
    public int getMagicCookie() {
        return magicCookie;
    }

    /**
     * Sets the magic cookie for the database.
     *
     * @param magicCookie the magic cookie for the database.
     */
    public void setMagicCookie(int magicCookie) {
        this.magicCookie = magicCookie;
    }

    /**
     * Gets the offset of the first record in the database.
     *
     * @return the offset of the first record in the database.
     */
    public int getOffset() {
        return offset;
    }

    /**
     * Gets the offset of the first record in the database.
     *
     * @param offset the offset of the first record in the database.
     */
    public void setOffset(int offset) {
        this.offset = offset;
    }

    /**
     * Gets the number of fields in the database.
     *
     * @return the number of fields in the database.
     */
    public short getNumFields() {
        return numFields;
    }

    /**
     * Sets the number of fields in the database.
     *
     * @param numFields the number of fields in the database.
     */
    public void setNumFields(short numFields) {
        this.numFields = numFields;
    }

    /**
     * Gets the length of all fields in the database.
     *
     * @return the length of all fields in the database.
     */
    public int getLengthAllFields() {
        int length = 0;
        for (int i = 0; i < this.numFields; i++) {
            length += this.fields[i].getLength();
        }
        return length;
    }

    /**
     * Gets the list of all fields in the database.
     *
     * @return the list of all fields in the database.
     */
    public Field[] getFields() {
        return fields;
    }

    /**
     * Sets the list of all fields in the database.
     *
     * @param fields the list of all fields in the database.
     */
    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    /**
     * Dump the value of all attributes.
     */
    public void dump() {
        System.out.println("Mc=" + magicCookie + ",offset=" + offset + ",nf=" + numFields);
    }
}
