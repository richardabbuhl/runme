package suncertify.db;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.FileNotFoundException;
import java.io.BufferedInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Nov 19, 2004
 * Time: 10:39:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Data implements DB {

    private String filename;

    public Data(String filename) {
        this.filename = filename;
    }

    private DataInputStream open() throws FileNotFoundException {
        /* Open the file for reading */
        FileInputStream pfd = new FileInputStream(filename);
        DataInputStream file = new DataInputStream(pfd);
        return file;
    }

    public String[] read(int recNo) throws RecordNotFoundException {
        try {
            DataInputStream file = open();
            int magicCookie = file.readInt();  // 4 bytes
            int offset = file.readInt();  // 4 bytes
            short numFields = file.readShort(); // 2 bytes
            System.out.println("Mc = " + magicCookie + " offset = " + offset + " nf = " + numFields);

            for (int i = 0; i < numFields; i++) {
                short nameLength = file.readShort();
                System.out.print("nameLength = " + nameLength );
                StringBuffer name = new StringBuffer();
                for (int k = 0; k < nameLength ; k++){
                    char c = (char)file.readByte(); 
                    name.append(c);
                }
                System.out.print(", name = " + name);
                short fieldLength = file.readShort();
                System.out.println(", fieldLength = " + fieldLength);
            }

        } catch(Exception e) {
            System.out.println("Error" + e.toString());
            throw new RecordNotFoundException(e.getMessage());
        }
        return new String[0];
    }

    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
        try {
            DataInputStream file = open();
        } catch(Exception e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }

    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
        try {
            DataInputStream file = open();
        } catch(Exception e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }

    public int[] find(String[] criteria) {
        try {
            DataInputStream file = open();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return new int[0];
    }

    public int create(String[] data) throws DuplicateKeyException {
        try {
            DataInputStream file = open();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public long lock(int recNo) throws RecordNotFoundException {
        try {
            DataInputStream file = open();
        } catch(Exception e) {
            System.out.println(e.getMessage());
        }
        return 0;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
