package org.jenkinsci.plugins.karotz;

/**
 * Karotz Exception
 *
 * @author Seiji Sogabe
 */
public class KarotzException extends Exception {

    public KarotzException(Throwable cause) {
        super(cause);
    }

    public KarotzException(String message, Throwable cause) {
        super(message, cause);
    }

    public KarotzException(String message) {
        super(message);
    }

    public KarotzException() {
    }
}
