package com.desertpetrol.converter;

//This class stores all relevant information for the conversion. As well as getters and setters
public class Currency {
        //Value Storage
        private static String fromCode;
        private static String toCode;
        private static double amount = 1302;

        //Getters && setters
        public static String getFromCode() {
                return fromCode;
        }

        public static void setFromCode(String fCurrency) {
                fromCode = fCurrency;
        }

        public static String getToCode() {
                return toCode;
        }

        public static void setToCode(String tCurrency) {
                toCode = tCurrency ;
        }

        public static double getAmount() {
                return amount;
        }

        public static void setAmount(double cAmount) {
                Currency.amount = cAmount;
        }

}
