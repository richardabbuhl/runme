package suncertify.db;

/**
 * Created by IntelliJ IDEA.
 * User: rabbuhl
 * Date: Nov 19, 2004
 * Time: 10:37:49 PM
 * To change this template use File | Settings | File Templates.
 */
public class DuplicateKeyException extends Exception {
    public DuplicateKeyException() {
    }

    public DuplicateKeyException(String message) {
        super(message);
    }
}
