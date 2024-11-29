package org.example.Repository.Interfaces;

import org.example.Exceptions.DatabaseUnavailableException;

public interface DAOwithUpdate<T, R> extends DAO<T, R> {

    void update(R r) throws DatabaseUnavailableException;
}
