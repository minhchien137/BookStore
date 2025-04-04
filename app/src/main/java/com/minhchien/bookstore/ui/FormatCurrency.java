package com.minhchien.bookstore.ui;

import java.text.NumberFormat;
import java.util.Locale;

public class FormatCurrency {
    public static String formatVND(int value) {
        Locale localeVN = new Locale("vi", "VN");
        NumberFormat currencyVN = NumberFormat.getCurrencyInstance(localeVN);
        String str = currencyVN.format(value);
        return str;
    }
}
