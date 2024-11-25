package org.example.Exceptions;

public class IncorrectDataException extends Exception {

    public IncorrectDataException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return String.format("{\n  message: %s \n}", getMessage());
    }
}
