package suncertify.db;

import java.io.*;

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

    private Schema readSchema(DataInputStream file) throws IOException {
        Schema schema = new Schema();
        schema.setMagicCookie(file.readInt());
        schema.setOffset(file.readInt());
        schema.setNumFields(file.readShort());
        schema.dump();

        Field[] fields = new Field[schema.getNumFields()];
        for (int i = 0; i < schema.getNumFields(); i++) {
            short nameLength = file.readShort();
            StringBuffer name = new StringBuffer();
            for (int k = 0; k < nameLength ; k++){
                char c = (char)file.readByte();
                name.append(c);
            }
            fields[i] = new Field();
            fields[i].setName(name.toString());
            fields[i].setLength(file.readShort());
            fields[i].dump();
        }
        schema.setFields(fields);
        return schema;
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
            Schema schema = readSchema(file);
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
