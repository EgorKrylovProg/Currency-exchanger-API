package org.example.Repository.Interfaces;

import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;

import java.util.List;
import java.util.Optional;

public interface DAO<T, R> {

    List<R> getAll() throws DatabaseUnavailableException;

    Optional<R> get(T r) throws DatabaseUnavailableException;

    void set(R t) throws DatabaseUnavailableException, DataDuplicationException, NoDataFoundException;

}
