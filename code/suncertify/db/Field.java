package suncertify.db;

/**
 * Created by IntelliJ IDEA.
 * User: RichardAbbuhl
 * Date: Feb 14, 2005
 * Time: 5:03:39 PM
 * To change this template use File | Settings | File Templates.
 */
public class Field {

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

    public void dump() {
        System.out.println("Name=" + name + ",length=" + length);
    }

}
