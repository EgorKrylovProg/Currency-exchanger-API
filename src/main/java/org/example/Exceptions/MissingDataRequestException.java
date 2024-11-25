package org.example.Exceptions;

public class MissingDataRequestException extends Exception {

    public MissingDataRequestException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return String.format("{\n  message: %s \n}", getMessage());
    }
}
