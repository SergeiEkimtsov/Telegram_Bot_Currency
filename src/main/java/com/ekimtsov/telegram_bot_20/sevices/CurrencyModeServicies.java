package com.ekimtsov.telegram_bot_20.sevices;

public interface CurrencyModeServicies {
    static CurrencyModeServicies  getInstance(){
                return  new ParsingWebPageJsoup();
    }
    double getRate(String currency);
}
