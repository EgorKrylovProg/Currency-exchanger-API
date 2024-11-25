package org.example.DTO;

public class ExchangeRateDTO {

    private int id;
    private CurrencyDTO baseCurrency = new CurrencyDTO();
    private CurrencyDTO targetCurrency = new CurrencyDTO();
    private double rate;

    public ExchangeRateDTO() {}

    public ExchangeRateDTO(int id, CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, double rate) {
        this.id = id;
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public ExchangeRateDTO(CurrencyDTO baseCurrency, CurrencyDTO targetCurrency, double rate) {
        this.baseCurrency = baseCurrency;
        this.targetCurrency = targetCurrency;
        this.rate = rate;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public CurrencyDTO getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(CurrencyDTO baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public CurrencyDTO getTargetCurrency() {
        return targetCurrency;
    }

    public void setTargetCurrency(CurrencyDTO targetCurrency) {
        this.targetCurrency = targetCurrency;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }

    public void setBaseCurrencyCode(String code) {
        this.baseCurrency.setCode(code);
    }

    public void setTargetCurrencyCode(String code) {
        this.targetCurrency.setCode(code);
    }

    @Override
    public String toString() {
        return String.format(
                "   {\n" +
                "       \"id\": %d,\n" +
                "       \"baseCurrency\": " + baseCurrency.toString() + ",\n" +
                "       \"targetCurrency\": " + targetCurrency.toString() + "\n" +
                "       \"rate\": %s" +
                "    }", id, rate);

    }
}
