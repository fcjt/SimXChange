package com.example.myapplication;

/**
 * Model class representing a currency with its exchange rate.
 */
public class CurrencyItem {
    private String code;
    private String rate;

    public CurrencyItem(String code, String rate) {
        this.code = code;
        this.rate = rate;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }
}
