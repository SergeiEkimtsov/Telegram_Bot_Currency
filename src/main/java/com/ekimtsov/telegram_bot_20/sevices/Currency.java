package com.ekimtsov.telegram_bot_20.sevices;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum Currency {
    USD(840),EUR(978), RUB;
    private int id;

    Currency(int id) {
        this.id = id;

    }

}
