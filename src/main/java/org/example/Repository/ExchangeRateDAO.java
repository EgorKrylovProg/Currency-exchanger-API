package org.example.Repository;

import org.example.DTO.CurrencyDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;
import org.example.Repository.Interfaces.CreateReadDAO;
import org.example.Repository.Interfaces.UpdateDAO;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ExchangeRateDAO implements CreateReadDAO<String, ExchangeRateDTO>, UpdateDAO<ExchangeRateDTO> {

    DataBaseConnection conn = new DataBaseConnection();

    @Override
    public List<ExchangeRateDTO> getAll() throws DatabaseUnavailableException {
        List<ExchangeRateDTO> exchangeRates = new ArrayList<>();
        CurrencyDTO baseCurrencyDTO;
        CurrencyDTO targetCurrencyDTO;

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

                baseCurrencyDTO = new CurrencyDTO();
                targetCurrencyDTO = new CurrencyDTO();

                id = resultSet.getInt("id");

                baseCurrencyDTO.setId(resultSet.getInt("Base_currency_id"));
                baseCurrencyDTO.setCode(resultSet.getString("Base_currency_code"));
                baseCurrencyDTO.setName(resultSet.getString("Base_currency_name"));
                baseCurrencyDTO.setSign(resultSet.getString("Base_currency_sign"));

                targetCurrencyDTO.setId(resultSet.getInt("Target_currency_id"));
                targetCurrencyDTO.setCode(resultSet.getString("Target_currency_code"));
                targetCurrencyDTO.setName(resultSet.getString("Target_currency_name"));
                targetCurrencyDTO.setSign(resultSet.getString("Target_currency_sign"));

                rate = resultSet.getDouble("rate");

                exchangeRates.add(new ExchangeRateDTO(id, baseCurrencyDTO, targetCurrencyDTO, rate));
            }

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return exchangeRates;
    }

    @Override
    public Optional<ExchangeRateDTO> get(String codes) throws DatabaseUnavailableException {
        Optional<ExchangeRateDTO> optionalExchangeRate = Optional.empty();
        CurrencyDTO baseCurrencyDTO = new CurrencyDTO();
        CurrencyDTO targetCurrencyDTO = new CurrencyDTO();

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
                optionalExchangeRate = Optional.of(new ExchangeRateDTO());

                optionalExchangeRate.get().setId(resultSet.getInt("id"));

                baseCurrencyDTO.setId(resultSet.getInt("Base_currency_id"));
                baseCurrencyDTO.setCode(resultSet.getString("Base_currency_code"));
                baseCurrencyDTO.setName(resultSet.getString("Base_currency_name"));
                baseCurrencyDTO.setSign(resultSet.getString("Base_currency_sign"));
                optionalExchangeRate.get().setBaseCurrency(baseCurrencyDTO);

                targetCurrencyDTO.setId(resultSet.getInt("Target_currency_id"));
                targetCurrencyDTO.setCode(resultSet.getString("Target_currency_code"));
                targetCurrencyDTO.setName(resultSet.getString("Target_currency_name"));
                targetCurrencyDTO.setSign(resultSet.getString("Target_currency_sign"));
                optionalExchangeRate.get().setTargetCurrency(targetCurrencyDTO);

                optionalExchangeRate.get().setRate(resultSet.getDouble("rate"));
            }

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }

        return optionalExchangeRate;
    }

    @Override
    public void set(ExchangeRateDTO exchangeRateDTO) throws DataDuplicationException, DatabaseUnavailableException, NoDataFoundException {
        int baseCurrencyId = -1;
        int targetCurrencyId = -1;

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, code FROM currencies WHERE code = ? OR code = ?;");
            preparedStatement.setString(1, exchangeRateDTO.getBaseCurrency().getCode());
            preparedStatement.setString(2, exchangeRateDTO.getTargetCurrency().getCode());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                if(resultSet.getString("code").equals(exchangeRateDTO.getBaseCurrency().getCode())) {
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
            preparedStatement.setDouble(3, exchangeRateDTO.getRate());

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_UNIQUE")) throw new DataDuplicationException("The exchange rate already exists!");
            if (e.getErrorCode() == 19 && e.getMessage().toUpperCase().contains("SQLITE_CONSTRAINT_FOREIGNKEY")) throw new NoDataFoundException("One (or both) currency from the currency pair does not exist in the database!");
            throw new DatabaseUnavailableException("Problems accessing the database!" + e.getMessage());
        }
    }

    @Override
    public void update(ExchangeRateDTO exchangeRateDTO) throws DatabaseUnavailableException {
        int baseCurrencyId = -1;
        int targetCurrencyId = -1;

        try (Connection connection = conn.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT id, code FROM currencies WHERE code = ? OR code = ?;");
            preparedStatement.setString(1, exchangeRateDTO.getBaseCurrency().getCode());
            preparedStatement.setString(2, exchangeRateDTO.getTargetCurrency().getCode());

            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {

                if(resultSet.getString("code").equals(exchangeRateDTO.getBaseCurrency().getCode())) {
                    baseCurrencyId = resultSet.getInt("id");
                    continue;
                }
                targetCurrencyId = resultSet.getInt("id");
            }


            preparedStatement = connection.prepareStatement(
                        "UPDATE exchange_rate " +
                            "SET rate = ? " +
                            "WHERE base_currency_id = ? AND target_currency_id = ?;");
            preparedStatement.setDouble(1, exchangeRateDTO.getRate());
            preparedStatement.setInt(2, baseCurrencyId);
            preparedStatement.setDouble(3, targetCurrencyId);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            throw new DatabaseUnavailableException("Problems accessing the database!");
        }
    }


}
