package suncertify.db;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Nov 19, 2004
 * Time: 10:39:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Data implements DB {
    public String[] read(int recNo) throws RecordNotFoundException {
        return new String[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public int[] find(String[] criteria) {
        return new int[0];  //To change body of implemented methods use File | Settings | File Templates.
    }

    public int create(String[] data) throws DuplicateKeyException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long lock(int recNo) throws RecordNotFoundException {
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
