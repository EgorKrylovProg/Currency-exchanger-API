package org.example.Service.interfaces;

import org.example.Exceptions.DatabaseUnavailableException;

public interface UpdatableService <T> {

    void update(T t) throws DatabaseUnavailableException;
}
