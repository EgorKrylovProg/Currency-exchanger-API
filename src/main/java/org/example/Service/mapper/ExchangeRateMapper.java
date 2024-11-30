package org.example.Service.mapper;

import org.example.DTO.CurrencyDTO;
import org.example.DTO.ExchangeRateDTO;
import org.example.DTO.ExchangeRateDtoToUpdate;
import org.example.Entity.Currency;
import org.example.Entity.ExchangeRate;

public class ExchangeRateMapper {

    public static ExchangeRateDTO toDto(ExchangeRate exchangeRate) {
        return new ExchangeRateDTO(
                exchangeRate.getId(),
                new CurrencyDTO(
                        exchangeRate.getBaseCurrency().getId(),
                        exchangeRate.getBaseCurrency().getCode(),
                        exchangeRate.getBaseCurrency().getFullName(),
                        exchangeRate.getBaseCurrency().getSign()
                ),
                new CurrencyDTO(
                        exchangeRate.getTargetCurrency().getId(),
                        exchangeRate.getTargetCurrency().getCode(),
                        exchangeRate.getTargetCurrency().getFullName(),
                        exchangeRate.getTargetCurrency().getSign()
                ),
                exchangeRate.getRate()
        );
    }

    public static ExchangeRate toEntity(ExchangeRateDTO dto) {
        return new ExchangeRate(
                dto.getId(),
                new Currency(
                        dto.getBaseCurrency().getId(),
                        dto.getBaseCurrency().getCode(),
                        dto.getBaseCurrency().getName(),
                        dto.getBaseCurrency().getSign()
                ),
                new Currency(
                        dto.getTargetCurrency().getId(),
                        dto.getTargetCurrency().getCode(),
                        dto.getTargetCurrency().getName(),
                        dto.getTargetCurrency().getSign()
                ),
                dto.getRate()
        );
    }

    public static ExchangeRate toEntity(ExchangeRateDtoToUpdate dto) {
        ExchangeRate exchangeRate = new ExchangeRate();
        exchangeRate.getBaseCurrency().setCode(dto.getBaseCode());
        exchangeRate.getTargetCurrency().setCode(dto.getTargetCode());
        exchangeRate.setRate(dto.getRate());

        return exchangeRate;
    }
}
