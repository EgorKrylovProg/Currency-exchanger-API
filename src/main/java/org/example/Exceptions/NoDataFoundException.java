package org.example.Exceptions;

public class NoDataFoundException extends Exception {

    public NoDataFoundException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return String.format("{\n  message: %s \n}", getMessage());
    }
}
