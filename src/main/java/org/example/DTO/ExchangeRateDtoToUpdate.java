package org.example.DTO;

public class ExchangeRateDtoToUpdate {

    private String baseCode;
    private String targetCode;
    private double rate;

    public ExchangeRateDtoToUpdate(String baseCode) {
        this.baseCode = baseCode;
    }

    public ExchangeRateDtoToUpdate() {

    }

    public String getBaseCode() {
        return baseCode;
    }

    public void setBaseCode(String baseCode) {
        this.baseCode = baseCode;
    }

    public String getTargetCode() {
        return targetCode;
    }

    public void setTargetCode(String targetCode) {
        this.targetCode = targetCode;
    }

    public double getRate() {
        return rate;
    }

    public void setRate(double rate) {
        this.rate = rate;
    }
}
