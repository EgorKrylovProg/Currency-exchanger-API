package org.example.Service.interfaces;

import org.example.DTO.ExchangeRateDTO;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;

import java.util.List;
import java.util.Optional;

public interface CreatableAndReadableService<T, R> {

    List<R> readAll() throws DatabaseUnavailableException;

    Optional<R> read(T t) throws DatabaseUnavailableException;

    void create(R r) throws DatabaseUnavailableException, DataDuplicationException, NoDataFoundException;

}
