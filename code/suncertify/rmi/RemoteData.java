/*
 * RemoteDataAdapter.java
 * Copyright (c) 2005 Richard Abbuhl.
 * Haarlem, The Netherlands.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of Richard Abbuhl.
 */

package suncertify.network;

import suncertify.db.Data;
import suncertify.db.RecordNotFoundException;
import suncertify.db.DuplicateKeyException;

import java.rmi.server.UnicastRemoteObject;
import java.rmi.RemoteException;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 22, 2005
 * Time: 1:58:17 PM
 * To change this template use File | Settings | File Templates.
 */
public class RemoteDataAdapter extends UnicastRemoteObject implements RemoteDB {

    protected Data adaptee;

    public RemoteDataAdapter(Data adaptee) throws RemoteException {
        super();
        this.adaptee = adaptee;
    }

    public String[] read(int recNo) throws RecordNotFoundException {
        return adaptee.read(recNo);
    }

    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        adaptee.update(recNo, data, lockCookie);
    }

    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        adaptee.delete(recNo, lockCookie);
    }

    public int[] find(String[] criteria) {
        return adaptee.find(criteria);
    }

    public int create(String[] data) throws DuplicateKeyException {
        return adaptee.create(data);
    }

    public long lock(int recNo) throws RecordNotFoundException {
        return adaptee.lock(recNo);
    }

    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        adaptee.unlock(recNo, cookie);
    }

}
