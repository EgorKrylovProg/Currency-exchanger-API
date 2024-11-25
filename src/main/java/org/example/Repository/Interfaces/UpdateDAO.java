package org.example.Repository.Interfaces;

import org.example.Exceptions.DatabaseUnavailableException;

public interface UpdateDAO <R> {

    void update(R r) throws DatabaseUnavailableException;
}
