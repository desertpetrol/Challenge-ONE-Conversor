package com.desertpetrol.converter;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URI;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

//this class handles the conversion and if necessary uses an offline conversion method.
public class Converter {
        private final static double offlineBaseDollar = 1.0;
        private final static double offlineReal = 5.18;
        private final static double offlineLibraEst = 0.83;
        private final static double offlinePesoArg = 197.57;
        private final static double offlinePesoChi = 811.07;
        private final static double offlineEuro = 0.94;
        private static double conversionResult = 0;
        private static double conversionRate = 0;
        private static String conversionDate = "";

        public static void offlineConversion() {
                double fCurrencyRate, tCurrencyRate;
                fCurrencyRate = switch (Currency.getFromCode()) {
                        case "USD" -> offlineBaseDollar;
                        case "BRL" -> offlineReal;
                        case "GBP" -> offlineLibraEst;
                        case "ARS" -> offlinePesoArg;
                        case "CLP" -> offlinePesoChi;
                        case "EUR" -> offlineEuro;
                        default -> throw new IllegalArgumentException("Invalid from currency: ");
                };

                tCurrencyRate = switch (Currency.getToCode()) {
                        case "USD" -> offlineBaseDollar;
                        case "BRL" -> offlineReal;
                        case "GBP" -> offlineLibraEst;
                        case "ARS" -> offlinePesoArg;
                        case "CLP" -> offlinePesoChi;
                        case "EUR" -> offlineEuro;
                        default -> throw new IllegalArgumentException("Invalid to currency: ");
                };

                double rate = offlineBaseDollar * tCurrencyRate / fCurrencyRate;
                setConversionRate(Math.round(rate * 1e6)/1e6);

                double result = getConversionRate() * Currency.getAmount();
                conversionResult = Math.round(result * 1e6)/1e6;

                setConversionDate("Offline : 2023-01-03");

        }

        public static void sendHttpGETRequest() throws IOException, InterruptedException {
                String requestUrl = "convert?from=" + Currency.getFromCode() + "&to=" + Currency.getToCode() +
                                        "&amount=" + Currency.getAmount();

                String getUrl = "https://api.exchangerate.host/" + requestUrl;

                HttpClient httpClient = HttpClient.newHttpClient();
                HttpRequest httpRequest = HttpRequest.newBuilder()
                                                        .uri(URI.create(getUrl))
                                                        .GET()
                                                        .build();

                HttpResponse<String> httpResponse = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

                int responseCode = httpResponse.statusCode();

                if (responseCode != HttpURLConnection.HTTP_OK) {
                        System.out.println("Error. Code: " + responseCode);
                } else {
                        JsonParser jp = new JsonParser();
                        JsonElement root = jp.parse(httpResponse.body());
                        JsonObject jsonobj = root.getAsJsonObject();
                        System.out.println(jsonobj);

                        conversionResult = jsonobj.get("result").getAsDouble();
                        conversionRate = (jsonobj.getAsJsonObject("info").get("rate").getAsDouble());
                        conversionDate = jsonobj.get("date").getAsString();
                }
        }
        public static void handleConversion() {
                try { sendHttpGETRequest(); }
                catch (Exception e) { offlineConversion(); }
        }

        public static double getConversionResult() {
                return conversionResult;
        }

        public static void setConversionResult(double cResult) {
                conversionResult = cResult;
        }

        public static double getConversionRate() {
                return conversionRate;
        }

        public static void setConversionRate(double cRate) {
                conversionRate = cRate;
        }

        public static String getConversionDate() {
                return conversionDate;
        }

        public static void setConversionDate(String cDate) {
                conversionDate = cDate;
        }
}
//        private static String fromCurrency = Currency.getFromCurrency();
//        private static String toCurrency = Currency.getToCurrency();
//        private static double currencyAmount = Currency.getCurrencyAmount();
//        private static double currencyResult = Currency.getCurrencyResult();
//        private static double currencyRate = Currency.getCurrencyRate();
//        private static String currencyDate = Currency.getCurrencyDate();
