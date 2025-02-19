package com.swiggy.walletapp.enums;

public enum Currency {
    INR(1.0), USD(83.0), EUR(90.0);

    private final double conversionRate;

    Currency(double conversionRate){
        this.conversionRate = conversionRate;
    }

    public double getConversionRate(){
        return this.conversionRate;
    }

    public double convertTo(Currency currency, double amount){
        return amount * this.conversionRate / currency.getConversionRate();
    }
}
