/*
 * Data.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Data implements the DB class in order to provide access to the customer data.
 * @version 1.00
 * @author Richard Abbuhl
 */
public class Data implements DB {

    /**
     * The database schema is cached for performance reasons.
     */
    private static Schema schema = null;

    /**
     * A HashMap is used for holding the locking information to lock the database using cookies.
     */
    private static HashMap cookies = new HashMap();

    /**
     * The path to the data file.
     */
    private String filename;

    /**
     * Initialize a Data object so that it contains the path to the data file.
     * @param filename path to the data file.
     */
    public Data(String filename) {
        this.filename = filename;
    }

    /**
     * Read in the schema and return a completed schema object.
     * @param file a readable file object.
     * @return a filled-in schema object.
     * @throws IOException thrown if there are problems accessing the file.
     */
    private Schema readSchema(RandomAccessFile file) throws IOException {
        Schema schema = new Schema();
        schema.setMagicCookie(file.readInt());
        schema.setOffset(file.readInt());
        schema.setNumFields(file.readShort());

        Field[] fields = new Field[schema.getNumFields()];
        for (int i = 0; i < schema.getNumFields(); i++) {
            short nameLength = file.readShort();
            StringBuffer name = new StringBuffer();
            for (int k = 0; k < nameLength; k++) {
                name.append((char) file.readByte());
            }
            fields[i] = new Field();
            fields[i].setName(name.toString());
            fields[i].setLength(file.readShort());
        }
        schema.setFields(fields);
        return schema;
    }

    /**
     * Reads a record from the file and returns an array where each element is a record value.  If the record
     * is locked then read waits until the record is unlocked before proceeding.  This prevents a dirty read
     * of the record.
     * @param recNo record from the file to be read.
     * @return an array where each element is a record value.
     * @throws RecordNotFoundException thrown if there are problems reading the record.
     */
    public String[] read(int recNo) throws RecordNotFoundException {
        synchronized (cookies) {
            Long key = new Long(recNo);
            while (cookies.get(key) != null) {
                try {
                    // Wait for notification to again check the lock.
                    cookies.wait();
                } catch (InterruptedException e) {
                    System.out.println("Exception " + e.toString());
                }
            }

            String[] result = null;
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(filename, "r");
                if (schema == null) {
                    schema = readSchema(file);
                }
                file.seek(schema.getOffset() + recNo * (schema.getLengthAllFields() + 2));
                short flag = file.readShort();
                if (flag == 0) {
                    result = new String[schema.getNumFields()];
                    for (int i = 0; i < schema.getNumFields(); i++) {
                        StringBuffer sb = new StringBuffer();
                        for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                            sb.append((char) file.readByte());
                        }
                        result[i] = sb.toString();
                    }
                } else {
                    throw new RecordNotFoundException("Record " + recNo + " was not found");
                }

            } catch (IOException e) {
                throw new RecordNotFoundException(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (IOException e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }

            return result;
        }
    }

    /**
     * Checks that record is actually locked
     * @param recNo record number whose lock is to be checked.
     * @param lockCookie cookie value that represents the lock.
     * @throws SecurityException thrown if the record is not locked.
     */
    private void lockCheck(int recNo, long lockCookie) throws SecurityException {
        Long key = new Long(recNo);
        Long value = (Long) cookies.get(key);
        if (value == null || value.longValue() != lockCookie) {
            throw new SecurityException("Record " + recNo + " was not locked");
        }
    }

    /**
     * Modifies the fields of a record. The new value for field n appears in data[n].
     * @param recNo record number to be updated.
     * @param data new values for this record.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if the record can not be found in the database.
     * @throws SecurityException thrown if the record is locked with a cookie other than lockCookie.
     */
    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        synchronized (cookies) {
            RandomAccessFile file = null;
            try {
                lockCheck(recNo, lockCookie);
                file = new RandomAccessFile(filename, "rw");
                if (schema == null) {
                    schema = readSchema(file);
                }
                file.seek(schema.getOffset() + recNo * (schema.getLengthAllFields() + 2));
                short flag = file.readShort();
                if (flag == 0) {
                    for (int i = 0; i < schema.getNumFields(); i++) {
                        if (i < data.length && data[i] != null) {
                            file.writeBytes(data[i]);
                            for (int k = 0; k < schema.getFields()[i].getLength() - data[i].length(); k++) {
                                file.writeBytes(" ");
                            }
                        } else {
                            StringBuffer sb = new StringBuffer();
                            for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                                sb.append((char) file.readByte());
                            }
                        }
                    }
                } else {
                    throw new RecordNotFoundException("Record " + recNo + " was not found");
                }

            } catch (FileNotFoundException e) {
                throw new RecordNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new RecordNotFoundException(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }
        }
    }

    /**
     * Deletes a record, making the record number and associated disk storage available for reuse.
     * @param recNo record number to be deleted.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if the record can not be found in the database.
     * @throws SecurityException thrown if the record is locked with a cookie other than lockCookie.
     */
    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        synchronized (cookies) {
            RandomAccessFile file = null;
            try {
                lockCheck(recNo, lockCookie);
                file = new RandomAccessFile(filename, "rw");
                if (schema == null) {
                    schema = readSchema(file);
                }
                file.seek(schema.getOffset() + recNo * (schema.getLengthAllFields() + 2));
                file.writeShort(0x8000);
                for (int i = 0; i < schema.getNumFields(); i++) {
                    for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                        file.writeBytes(" ");
                    }
                }

            } catch (FileNotFoundException e) {
                throw new RecordNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new RecordNotFoundException(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }
        }
    }

    /**
     * Returns an array of record numbers that match the specified criteria. Field n in the database file is described
     * by criteria[n]. A null value in criteria[n] matches any field value. A non-null  value in criteria[n] matches
     * any field value that begins with criteria[n]. (For example, "Fred" matches "Fred" or "Freddy".)
     * @param criteria criteria used for matching records.
     * @return an array of record numbers that match the specified criteria.
     */
    public int[] find(String[] criteria) {
        synchronized (cookies) {
            int[] result = null;
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(filename, "rw");
                if (schema == null) {
                    schema = readSchema(file);
                }
                List resultList = new ArrayList();
                int recNo = 0;
                int nextPos = schema.getOffset() + recNo * (schema.getLengthAllFields() + 2);
                while (nextPos < file.length()) {
                    file.seek(nextPos);
                    short flag = file.readShort();
                    boolean match = false;
                    if (flag == 0) {
                        for (int i = 0; i < schema.getNumFields(); i++) {
                            StringBuffer sb = new StringBuffer();
                            for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                                sb.append((char) file.readByte());
                            }
                            if (criteria[i] == null) {
                                match = true;
                            } else if (criteria[i].length() > 0) {
                                if (sb.toString().startsWith(criteria[i])) {
                                    match = true;
                                } else {
                                    match = false;
                                }
                            }
                        }
                    }
                    if (match) {
                        resultList.add(new Integer(recNo));
                    }
                    recNo++;
                    nextPos = schema.getOffset() + recNo * (schema.getLengthAllFields() + 2);
                }

                if (resultList.size() > 0) {
                    result = new int[resultList.size()];
                    for (int i = 0; i < resultList.size(); i++) {
                        Integer a = (Integer) resultList.get(i);
                        result[i] = a.intValue();
                    }
                }

            } catch (Exception e) {
                System.out.println("Exception " + e.toString());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }

            return result;
        }
    }

    /**
     * Creates a new record in the database (possibly reusing a deleted entry). Inserts the given data, and returns
     * the record number of the new record.
     * @param data values for this new record.
     * @return the record number of the new record.
     * @throws DuplicateKeyException thrown if the record cannot be created.
     */
    public int create(String[] data) throws DuplicateKeyException {
        synchronized (cookies) {
            int recNo = 0;
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(filename, "rw");
                if (schema == null) {
                    schema = readSchema(file);
                }
                int nextPos = schema.getOffset() + recNo * (schema.getLengthAllFields() + 2);
                while (nextPos < file.length()) {
                    file.seek(nextPos);
                    short flag = file.readShort();
                    if (flag == 0x8000) {
                        file.seek(nextPos);
                        file.writeShort(0);
                        for (int i = 0; i < schema.getNumFields(); i++) {
                            if (i < data.length && data[i] != null) {
                                file.writeBytes(data[i]);
                                for (int k = 0; k < schema.getFields()[i].getLength() - data[i].length(); k++) {
                                    file.writeBytes(" ");
                                }
                            } else {
                                for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                                    file.writeBytes(" ");
                                }
                            }
                        }
                        return recNo;
                    }
                    recNo++;
                    nextPos = schema.getOffset() + recNo * (schema.getLengthAllFields() + 2);
                }

                file.seek(file.length());
                file.writeShort(0);
                for (int i = 0; i < schema.getNumFields(); i++) {
                    if (i < data.length && data[i] != null) {
                        file.writeBytes(data[i]);
                        for (int k = 0; k < schema.getFields()[i].getLength() - data[i].length(); k++) {
                            file.writeBytes(" ");
                        }
                    } else {
                        for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                            file.writeBytes(" ");
                        }
                    }
                }

            } catch (Exception e) {
                throw new DuplicateKeyException(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }

            return recNo;
        }
    }

    /**
     * Locks a record so that it can only be updated or deleted by this client. Returned value is a cookie that must
     * be used when the record is unlocked, updated, or deleted. If the specified record is already locked by a
     * different client, the current thread gives up the CPU and consumes no CPU cycles until the record is unlocked.
     * @param recNo record number to be locked.
     * @return cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if the record can not be found in the database.
     */
    public long lock(int recNo) throws RecordNotFoundException {
        synchronized (cookies) {
            Long key = new Long(recNo);
            while (cookies.get(key) != null) {
                try {
                    // Wait for notification to again check the lock.
                    cookies.wait();
                } catch (InterruptedException e) {
                    System.out.println("Exception " + e.toString());                
                }
            }

            int cookie = 0;
            RandomAccessFile file = null;
            try {
                file = new RandomAccessFile(filename, "r");
                if (schema == null) {
                    schema = readSchema(file);
                }
                file.seek(schema.getOffset() + recNo * (schema.getLengthAllFields() + 2));
                short flag = file.readShort();
                if (flag == 0) {

                    cookie = key.hashCode();
                    Long value = new Long(cookie);
                    cookies.put(key, value);

                } else {
                    throw new RecordNotFoundException("Record " + recNo + " was not found");
                }

            } catch (FileNotFoundException e) {
                throw new RecordNotFoundException(e.getMessage());
            } catch (IOException e) {
                throw new RecordNotFoundException(e.getMessage());
            } finally {
                if (file != null) {
                    try {
                        file.close();
                    } catch (Exception e) {
                        System.out.println("Error closing file + " + filename + " " + e.toString());
                    }
                }

                cookies.notify();
            }

            return cookie;
        }
    }

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when the record was locked; otherwise
     * throws SecurityException.
     * @param recNo record number to be unlocked.
     * @param cookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if the record can not be found in the database.
     * @throws SecurityException thrown if the record is not locked.
     */
    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        synchronized (cookies) {
            try {
                Long key = new Long(recNo);
                Long value = (Long) cookies.remove(key);
                if (value == null || value.longValue() != cookie) {
                    throw new SecurityException("Record " + recNo + " cookie invalid");
                }

            } catch (SecurityException e) {
                throw new SecurityException(e.getMessage());
            } catch (Exception e) {
                throw new RecordNotFoundException(e.getMessage());
            } finally {
                cookies.notify();
            }
        }
    }
}
