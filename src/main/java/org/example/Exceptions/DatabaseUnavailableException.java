package org.example.Exceptions;

import java.sql.SQLException;

public class DatabaseUnavailableException extends Exception {

    public DatabaseUnavailableException(String reason) {
        super(reason);
    }

    @Override
    public String toString() {
        return String.format("{\n  message: %s \n}", getMessage());
    }
}
