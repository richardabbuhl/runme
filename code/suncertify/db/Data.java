package suncertify.db;

import java.io.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Nov 19, 2004
 * Time: 10:39:04 PM
 * To change this template use File | Settings | File Templates.
 */
public class Data implements DB {

    private static Schema schema = null;
    private static HashMap cookies = new HashMap();
    private String filename;

    public Data(String filename) {
        this.filename = filename;
    }

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

    public String[] read(int recNo) throws RecordNotFoundException {
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
                }
            }
        }
        return result;
    }

    private void lockCheck(int recNo, long lockCookie) throws SecurityException {
        Long key = new Long(recNo);
        Long value = (Long) cookies.get(key);
        if (value == null || value.longValue() != lockCookie) {
            throw new SecurityException("Record " + recNo + " was not locked");
        }
    }

    public void update(int recNo, String[] data, long lockCookie) throws RecordNotFoundException, SecurityException {
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
                    System.out.println("Error" + e.toString());
                }
            }
        }
    }

    public void delete(int recNo, long lockCookie) throws RecordNotFoundException, SecurityException {
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
                    System.out.println("Error" + e.toString());
                }
            }
        }
    }

    public int[] find(String[] criteria) {
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
                        if (criteria[i] != null) {
                            if ("*".equals(criteria[i]) || sb.toString().startsWith(criteria[i])) {
                                match = true;
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
                    System.out.println("Error" + e.toString());
                }
            }
        }

        return result;
    }

    public int create(String[] data) throws DuplicateKeyException {
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
                    System.out.println("Error" + e.toString());
                }
            }
        }

        return recNo;
    }

    public long lock(int recNo) throws RecordNotFoundException {
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

                Long key = new Long(recNo);
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
                    System.out.println("Error" + e.toString());
                }
            }
        }

        return cookie;
    }

    public void unlock(int recNo, long cookie) throws RecordNotFoundException, SecurityException {
        try {
            Long key = new Long(recNo);
            Long value = (Long) cookies.remove(key);
            if (value.longValue() != cookie) {
                throw new SecurityException("Record " + recNo + " cookie invalid");
            }

        } catch (Exception e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }
}
