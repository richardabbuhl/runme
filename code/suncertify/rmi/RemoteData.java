/*
 * RemoteDataAdapter.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.rmi;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.DuplicateKeyException;
import suncertify.db.DB;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;
import java.io.IOException;

/**
 * RemoteData implements the DB class as a remote interface to the Data class using the adopter / adoptee pattern.
 *
 * @author Richard Abbuhl
 * @version 1.00
 */
public class RemoteData extends UnicastRemoteObject implements DB {

    /**
     * Adoptee for the remote interface.
     */
    protected Data adaptee;

    /**
     * Constructs a RemoteData object.
     * @param adaptee Adaptee for the remote interface.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public RemoteData(Data adaptee) throws RemoteException {
        super();
        this.adaptee = adaptee;
    }

    /**
     * Executes a remote call to reads a record from the file.
     * @param recNo record from the file to be read.
     * @return an array where each element is a record value.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public String[] read(int recNo)
            throws RecordNotFoundException, IOException, RemoteException {
        return adaptee.read(recNo);
    }

    /**
     * Executes a remote call to modify the fields of a record.
     *
     * @param recNo record number to be updated.
     * @param data new values for this record.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if if recNo cannot be found.
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void update(int recNo, String[] data, long lockCookie)
            throws RecordNotFoundException, SecurityException, IOException, RemoteException {
        adaptee.update(recNo, data, lockCookie);
    }

    /**
     * Executes a remote call to delete a record.
     *
     * @param recNo record number to be deleted.
     * @param lockCookie cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void delete(int recNo, long lockCookie)
            throws RecordNotFoundException, SecurityException, IOException, RemoteException {
        adaptee.delete(recNo, lockCookie);
    }

    /**
     * Executes a remote call to returns an array of record numbers that match the specified criteria.
     *
     * @param criteria criteria used for matching records.
     * @return an array of record numbers that match the specified criteria
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public int[] find(String[] criteria) throws IOException, RemoteException {
        return adaptee.find(criteria);
    }

    /**
     * Executes a remote call to create a new record in the database.
     *
     * @param data values for this new record.
     * @return the record number of the new record.
     * @throws DuplicateKeyException thrown if the record already exists in the database.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public int create(String[] data)
            throws DuplicateKeyException, IOException, RemoteException {
        return adaptee.create(data);
    }

    /**
     * Executes a remote call to lock a record.
     *
     * @param recNo record number to be locked.
     * @return cookie value that represents the lock.
     * @throws RecordNotFoundException thrown if recNo cannot be found.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public long lock(int recNo)
            throws RecordNotFoundException, IOException, RemoteException {
        return adaptee.lock(recNo);
    }

    /**
     * Executes a remote call to releases the lock on a record.
     *
     * @param recNo record number to be unlocked.
     * @param cookie cookie value that represents the lock.
     * @throws RecordNotFoundException
     * @throws SecurityException thrown if the record is not locked by cookie.
     * @throws IOException thrown if an error occurs accessing the database file.
     * @throws RemoteException thrown if a problem occurs during execution of the remote method call.
     */
    public void unlock(int recNo, long cookie)
            throws RecordNotFoundException, SecurityException, IOException, RemoteException {
        adaptee.unlock(recNo, cookie);
    }

}
