package suncertify.db;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Nov 19, 2004
 * Time: 10:36:48 PM
 * To change this template use File | Settings | File Templates.
 */
public class RecordNotFoundException extends Exception {
    public RecordNotFoundException() {
    }

    public RecordNotFoundException(String message) {
        super(message);
    }
}
