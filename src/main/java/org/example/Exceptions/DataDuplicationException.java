package org.example.Exceptions;

public class DataDuplicationException extends Exception {

    public DataDuplicationException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return String.format("{\n  message: %s \n}", getMessage());
    }
}
