package org.example.Repository;

import org.example.DTO.CurrencyDTO;
import org.example.Entity.Currency;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Repository.Interfaces.DAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CurrencyDAO implements DAO<String, Currency> {

    DataBaseConnection conn = new DataBaseConnection();

    @Override
    public List<Currency> getAll() throws DatabaseUnavailableException {
        List<Currency> currencies = new ArrayList<>();

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

                currencies.add(new Currency(id, code, name, sign));
            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return currencies;
    }

    @Override
    public Optional<Currency> get(String code) throws DatabaseUnavailableException {
        Optional<Currency> returningCurrency = Optional.empty();

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM currencies WHERE code = ?;");
            preparedStatement.setString(1, code);

            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                returningCurrency = Optional.of(new Currency(
                        resultSet.getInt("id"),
                        resultSet.getString("code"),
                        resultSet.getString("full_name"),
                        resultSet.getString("sign")
                        ));

            }
        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return returningCurrency;
    }

    @Override
    public void set(Currency currency) throws DatabaseUnavailableException, DataDuplicationException {

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO currencies(code, full_name, sign)" +
                    "VALUES(?, ?, ?);");

            preparedStatement.setString(1, currency.getCode());
            preparedStatement.setString(2, currency.getFullName());
            preparedStatement.setString(3, currency.getSign());

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_UNIQUE")) {
                throw new DataDuplicationException("A currency with this code already exists!");
            }
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

    }


}