package suncertify.db;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 14, 2005
 * Time: 4:26:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class Schema {

    private int magicCookie;
    private int offset;
    private short numFields;
    private Field[] fields;

    public static class Field {
        private String name = new String();
        private short length;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public short getLength() {
            return length;
        }

        public void setLength(short length) {
            this.length = length;
        }

        public void print() {
            System.out.println("Name = " + name + ", length = " + length);
        }
    }

    public int getMagicCookie() {
        return magicCookie;
    }

    public void setMagicCookie(int magicCookie) {
        this.magicCookie = magicCookie;
    }

    public int getOffset() {
        return offset;
    }

    public void setOffset(int offset) {
        this.offset = offset;
    }

    public short getNumFields() {
        return numFields;
    }

    public void setNumFields(short numFields) {
        this.numFields = numFields;
    }

    public Field[] getFields() {
        return fields;
    }

    public void setFields(Field[] fields) {
        this.fields = fields;
    }

    public void printHeader() {
        System.out.println("Mc = " + magicCookie + " offset = " + offset + " nf = " + numFields);
    }
}
