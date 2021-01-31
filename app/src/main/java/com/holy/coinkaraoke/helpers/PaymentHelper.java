package com.holy.coinkaraoke.helpers;


import java.time.Duration;

public class PaymentHelper {

    public static int COIN_PER_HOUR = 2000;

    public static int getAvailableHours(int coin) {

        return (coin / COIN_PER_HOUR);
    }

    public static int getAvailableMinutes(int coin) {

        if (coin % COIN_PER_HOUR >= COIN_PER_HOUR / 2) {
            return 30;
        }
        return 0;
    }

    public static int getPrice(Duration duration) {

        int totalMinutes = (int)duration.getSeconds() / 60;
        int hours = totalMinutes / 60;
        int minutes = totalMinutes % 60;

        int coin = hours * COIN_PER_HOUR;
        if (minutes > 0) {
            coin += COIN_PER_HOUR / 2;
            if (minutes > 30) {
                coin += COIN_PER_HOUR / 2;
            }
        }

        return coin;
    }

}
