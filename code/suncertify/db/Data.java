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

    private static Schema schema = null;
    private String filename;

    public Data(String filename) {
        this.filename = filename;
    }

    private Schema readSchema(DataInputStream file) throws IOException {
        Schema schema = new Schema();
        schema.setMagicCookie(file.readInt());
        schema.setOffset(file.readInt());
        schema.setNumFields(file.readShort());
        //schema.dump();

        Field[] fields = new Field[schema.getNumFields()];
        for (int i = 0; i < schema.getNumFields(); i++) {
            short nameLength = file.readShort();
            StringBuffer name = new StringBuffer();
            for (int k = 0; k < nameLength ; k++){
                name.append((char)file.readByte());
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
            if (schema == null) {
                DataInputStream file = open();
                schema = readSchema(file);
                file.close();
            }
            DataInputStream file = open();
            file.skip(schema.getOffset() + recNo * (schema.getLengthAllFields() + 2));
            short flag = file.readShort();
            for (int i = 0; i < schema.getNumFields(); i++) {
                StringBuffer sb = new StringBuffer();
                for (int k = 0; k < schema.getFields()[i].getLength(); k++) {
                    sb.append((char)file.readByte());
                }
                System.out.println(schema.getFields()[i].getName() + "=" + sb.toString());
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
