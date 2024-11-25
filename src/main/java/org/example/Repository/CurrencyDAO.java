package org.example.Repository;

import org.example.DTO.CurrencyDTO;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Repository.Interfaces.CreateReadDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO implements CreateReadDAO<String, CurrencyDTO> {

    DataBaseConnection conn = new DataBaseConnection();

    @Override
    public List<CurrencyDTO> getAll() throws DatabaseUnavailableException {
        List<CurrencyDTO> currencies = new ArrayList<>();

        try (Connection connection = conn.getConnection()) {

            int id;
            String code, name, sign;

            Statement statement = connection.createStatement();

            ResultSet result = statement.executeQuery("SELECT * FROM currencies;");

            while (result.next()) {

                id = result.getInt("id");
                code = result.getString("code");
                name = result.getString("full_name");
                sign = result.getString("sign");

                currencies.add(new CurrencyDTO(id, code, name, sign));
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return currencies;
    }

    @Override
    public Optional<CurrencyDTO> get(String code) throws DatabaseUnavailableException {
        Optional<CurrencyDTO> returningCurrencyDTO = Optional.empty();

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies WHERE code = ?;");
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                returningCurrencyDTO = Optional.of(new CurrencyDTO());

                returningCurrencyDTO.get().setId(resultSet.getInt("id"));
                returningCurrencyDTO.get().setCode(resultSet.getString("code"));
                returningCurrencyDTO.get().setName(resultSet.getString("full_name"));
                returningCurrencyDTO.get().setSign(resultSet.getString("sign"));
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return returningCurrencyDTO;
    }

    @Override
    public void set(CurrencyDTO currencyDTO) throws DatabaseUnavailableException, DataDuplicationException {

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencies(code, full_name, sign)" +
                    "VALUES(?, ?, ?);");

            preparedStatement.setString(1, currencyDTO.getCode());
            preparedStatement.setString(2, currencyDTO.getName());
            preparedStatement.setString(3, currencyDTO.getSign());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new DataDuplicationException("A currency with this code already exists!");
            }
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

    }


}