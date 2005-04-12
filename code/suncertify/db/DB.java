/*
 * DB.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.db;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * DB is the interface class provided for accessing the customer's non-relational database file.
 *
 * @author Richard Abbuhl
 * @version 1.00, April 12, 2005
 */
public interface DB extends Remote {
    /**
     * Defines a constant to identify this service.
     */
    public static final String SERVICENAME = "DB";

    /**
     * Reads a record from the file. Returns an array where each element is a record value.
     * @param recNo record from the file to be read.
     * @return an array where each element is a record value.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public String[] read(int recNo) throws RecordNotFoundException, RemoteException;

    /**
     * Modifies the fields of a record. The new value for field n appears in data[n]. Throws SecurityException
     * if the record is locked with a cookie other than lockCookie.
     * @param recNo record number to be updated.
     * @param data new values for this record.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if if recNo cannot be found.
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException, RemoteException;

    /**
     * Deletes a record, making the record number and associated disk storage available for reuse.  Throws
     * SecurityException if the record is locked with a cookie other than lockCookie.
     * @param recNo record number to be deleted.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException, RemoteException;

    /**
     * Returns an array of record numbers that match the specified criteria. Field n in the database file is
     * described by criteria[n]. A null value in criteria[n] matches any field value. A non-null value in
     * criteria[n] matches any field value that begins with criteria[n]. (For example, "Fred" matches "Fred"
     * or "Freddy".)
     * @param criteria criteria used for matching records.
     * @return an array of record numbers that match the specified criteria
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public int[] find(String[] criteria) throws RemoteException;

    /**
     * Creates a new record in the database (possibly reusing a deleted entry). Inserts the given data, and returns
     * the record number of the new record.
     * @param data values for this new record.
     * @return the record number of the new record.
     * @throws DuplicateKeyException thrown if the record cannot be created.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public int create(String[] data) throws DuplicateKeyException, RemoteException;

    /**
     * Locks a record so that it can only be updated or deleted by this client. Returned value is a cookie that must
     * be used when the record is unlocked, updated, or deleted. If the specified record is already locked by a
     * different client, the current thread gives up the CPU and consumes no CPU cycles until the record is unlocked.
     * @param recNo record number to be locked.
     * @return cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public long lock(int recNo) throws RecordNotFoundException, RemoteException;

    /**
     * Releases the lock on a record. Cookie must be the cookie returned when the record was locked; otherwise
     * throws SecurityException.
     * @param recNo record number to be unlocked.
     * @param cookie cookie value that represents the lock.
     * @throws RecordNotFoundException
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void unlock(int recNo, long cookie)
            throws RecordNotFoundException, SecurityException, RemoteException;
}
