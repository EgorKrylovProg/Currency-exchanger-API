package org.example.Repository;

import org.example.DTO.CurrencyDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entity.Currency;
import org.example.Entity.ExchangeRate;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;
import org.example.Repository.Interfaces.DAO;
import org.example.Repository.Interfaces.DAOwithUpdate;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO implements DAOwithUpdate<String, ExchangeRate> {

    DataBaseConnection conn = new DataBaseConnection();

    @Override
    public List<ExchangeRate> getAll() throws DatabaseUnavailableException {
        List<ExchangeRate> exchangeRates = new ArrayList<>();
        Currency baseCurrency;
        Currency targetCurrency;

        try (Connection connection = conn.getConnection()) {

            int id;
            double rate;
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery(
                    "SELECT er.id AS 'id', " +
                            "c.id AS 'Base_currency_id', " +
                            "c.code AS 'Base_currency_code', " +
                            "c.full_name AS 'Base_currency_name', " +
                            "c.sign AS 'Base_currency_sign', " +
                            "ci.id AS 'Target_currency_id', " +
                            "ci.code AS 'Target_currency_code', " +
                            "ci.full_name AS 'Target_currency_name', " +
                            "ci.sign AS 'Target_currency_sign', " +
                            "er.rate AS 'rate' FROM exchange_rate AS er " +
                            "JOIN currencies AS c ON er.base_currency_id = c.id " +
                            "JOIN currencies AS ci ON er.target_currency_id = ci.id;");

            while (resultSet.next()) {

                id = resultSet.getInt("id");
                baseCurrency = new Currency(
                        resultSet.getInt("Base_currency_id"),
                        resultSet.getString("Base_currency_code"),
                        resultSet.getString("Base_currency_name"),
                        resultSet.getString("Base_currency_sign")
                );

                targetCurrency = new Currency(
                        resultSet.getInt("Target_currency_id"),
                        resultSet.getString("Target_currency_code"),
                        resultSet.getString("Target_currency_name"),
                        resultSet.getString("Target_currency_sign")
                );
                rate = resultSet.getDouble("rate");

                exchangeRates.add(new ExchangeRate(id, baseCurrency, targetCurrency, rate));
            }

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRate> get(String codes) throws DatabaseUnavailableException {
        Optional<ExchangeRate> optionalExchangeRate = Optional.empty();

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT er.id AS 'id', " +
                            "c.id AS 'Base_currency_id', " +
                            "c.code AS 'Base_currency_code', " +
                            "c.full_name AS 'Base_currency_name', " +
                            "c.sign AS 'Base_currency_sign', " +
                            "ci.id AS 'Target_currency_id', " +
                            "ci.code AS 'Target_currency_code', " +
                            "ci.full_name AS 'Target_currency_name', " +
                            "ci.sign AS 'Target_currency_sign', " +
                            "er.rate AS 'rate' FROM exchange_rate AS er " +
                            "JOIN currencies AS c ON er.base_currency_id = c.id " +
                            "JOIN currencies AS ci ON er.target_currency_id = ci.id " +
                            "WHERE c.code = ? and ci.code = ?;");

            preparedStatement.setString(1, codes.substring(0, 3));
            preparedStatement.setString(2, codes.substring(3, 6));

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                int id;
                Currency baseCurrency, targetCurrency;
                double rate;

                id = resultSet.getInt("id");
                baseCurrency = new Currency(
                        resultSet.getInt("Base_currency_id"),
                        resultSet.getString("Base_currency_code"),
                        resultSet.getString("Base_currency_name"),
                        resultSet.getString("Base_currency_sign")
                );

                targetCurrency = new Currency(
                        resultSet.getInt("Target_currency_id"),
                        resultSet.getString("Target_currency_code"),
                        resultSet.getString("Target_currency_name"),
                        resultSet.getString("Target_currency_sign")
                );
                rate = resultSet.getDouble("rate");

                optionalExchangeRate = Optional.of(new ExchangeRate(id, baseCurrency, targetCurrency, rate));
            }

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return optionalExchangeRate;
    }

    @Override
    public void set(ExchangeRate exchangeRate) throws DataDuplicationException, DatabaseUnavailableException, NoDataFoundException {
        int baseCurrencyId = -1;
        int targetCurrencyId = -1;

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, code FROM currencies WHERE code = ? OR code = ?;");
            preparedStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            preparedStatement.setString(2, exchangeRate.getTargetCurrency().getCode());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                if(resultSet.getString("code").equals(exchangeRate.getBaseCurrency().getCode())) {
                    baseCurrencyId = resultSet.getInt("id");
                    continue;
                }
                targetCurrencyId = resultSet.getInt("id");
            }


            preparedStatement = connection.prepareStatement(
                        "INSERT INTO exchange_rate(base_currency_id, target_currency_id, rate)" +
                                "VALUES(?, ?, ?);");
            preparedStatement.setInt(1, baseCurrencyId);
            preparedStatement.setInt(2, targetCurrencyId);
            preparedStatement.setDouble(3, exchangeRate.getRate());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_UNIQUE")) throw new DataDuplicationException("The exchange rate already exists!");
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) throw new NoDataFoundException("One (or both) currency from the currency pair does not exist in the database!");
            throw new DatabaseUnavailableException("Problems accessing the database!" + e.getMessage());
        }
    }

    @Override
    public void update(ExchangeRate exchangeRate) throws DatabaseUnavailableException {
        int baseCurrencyId = -1;
        int targetCurrencyId = -1;

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, code FROM currencies WHERE code = ? OR code = ?;");
            preparedStatement.setString(1, exchangeRate.getBaseCurrency().getCode());
            preparedStatement.setString(2, exchangeRate.getTargetCurrency().getCode());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                if(resultSet.getString("code").equals(exchangeRate.getBaseCurrency().getCode())) {
                    baseCurrencyId = resultSet.getInt("id");
                    continue;
                }
                targetCurrencyId = resultSet.getInt("id");
            }


            preparedStatement = connection.prepareStatement(
                        "UPDATE exchange_rate " +
                            "SET rate = ? " +
                            "WHERE base_currency_id = ? AND target_currency_id = ?;");
            preparedStatement.setDouble(1, exchangeRate.getRate());
            preparedStatement.setInt(2, baseCurrencyId);
            preparedStatement.setDouble(3, targetCurrencyId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }
    }


}
