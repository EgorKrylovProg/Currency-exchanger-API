package org.example.Service;

import org.example.DTO.CurrencyDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.Entity.Currency;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;
import org.example.Repository.CurrencyDAO;
import org.example.Repository.Interfaces.DAO;
import org.example.Service.interfaces.CreatableAndReadableService;

import java.util.List;
import java.util.Optional;

public class CurrencyService implements CreatableAndReadableService<String, CurrencyDTO> {

    private final DAO<String, Currency> currencyDAO = new CurrencyDAO();

    @Override
    public List<CurrencyDTO> readAll() throws DatabaseUnavailableException {
        return currencyDAO.getAll().stream().map(this::toDto).toList();
    }

    @Override
    public Optional<CurrencyDTO> read(String code) throws DatabaseUnavailableException {
        return currencyDAO.get(code).map(this::toDto);
    }

    @Override
    public void create(CurrencyDTO currencyDTO) throws DatabaseUnavailableException, DataDuplicationException, NoDataFoundException {
        currencyDAO.set(this.toEntity(currencyDTO));
    }

    private CurrencyDTO toDto(Currency currency) {
        return new CurrencyDTO(
                currency.getId(),
                currency.getCode(),
                currency.getFullName(),
                currency.getSign()
        );
    }

    private Currency toEntity(CurrencyDTO dto) {
        return new Currency(
                dto.getId(),
                dto.getCode(),
                dto.getName(),
                dto.getSign()
        );
    }
}
