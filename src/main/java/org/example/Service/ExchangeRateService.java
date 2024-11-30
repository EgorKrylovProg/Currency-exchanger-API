package org.example.Service;

import org.example.DTO.CurrencyDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.DTO.ExchangeRateDtoToUpdate;
import org.example.Entity.Currency;
import org.example.Entity.ExchangeRate;
import org.example.Exceptions.DataDuplicationException;
import org.example.Exceptions.DatabaseUnavailableException;
import org.example.Exceptions.NoDataFoundException;
import org.example.Repository.ExchangeRateDAO;
import org.example.Repository.Interfaces.DAOwithUpdate;
import org.example.Service.interfaces.CreatableAndReadableService;
import org.example.Service.interfaces.UpdatableService;
import org.example.Service.mapper.ExchangeRateMapper;

import java.util.List;
import java.util.Optional;

public class ExchangeRateService implements UpdatableService<ExchangeRateDtoToUpdate>, CreatableAndReadableService<String, ExchangeRateDTO> {

    private final DAOwithUpdate<String, ExchangeRate> dao = new ExchangeRateDAO();

    @Override
    public List<ExchangeRateDTO> readAll() throws DatabaseUnavailableException {
        return dao.getAll().stream().map(ExchangeRateMapper::toDto).toList();
    }

    @Override
    public Optional<ExchangeRateDTO> read(String codes) throws DatabaseUnavailableException {
        return dao.get(codes).map(ExchangeRateMapper::toDto);
    }

    @Override
    public void create(ExchangeRateDTO currencyDTO) throws DatabaseUnavailableException, DataDuplicationException, NoDataFoundException {
        dao.set(ExchangeRateMapper.toEntity(currencyDTO));
    }

    @Override
    public void update(ExchangeRateDtoToUpdate dto) throws DatabaseUnavailableException {
        dao.update(ExchangeRateMapper.toEntity(dto));
    }
}
