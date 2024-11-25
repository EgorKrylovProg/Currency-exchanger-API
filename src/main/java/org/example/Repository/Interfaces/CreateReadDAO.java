package org.example.Repository.Interfaces;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CreateReadDAO<T, R> {

    List<R> getAll() throws Exception;

    Optional<R> get(T r) throws Exception;

    void set(R t) throws Exception;

}
