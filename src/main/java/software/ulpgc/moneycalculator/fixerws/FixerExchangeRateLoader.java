package software.ulpgc.moneycalculator.fixerws;

import com.google.gson.JsonElement;
import software.ulpgc.moneycalculator.Currency;
import software.ulpgc.moneycalculator.ExchangeRate;
import software.ulpgc.moneycalculator.ExchangeRateLoader;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
public class FixerExchangeRateLoader implements ExchangeRateLoader {
    @Override
    public ExchangeRate load(Currency from, Currency to) {
        try {
            String json = fetchExchangeRatesJson();
            return parseExchangeRate(json, from, to);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private String fetchExchangeRatesJson() throws IOException {
        URL url = new URL("http://data.fixer.io/api/latest?access_key=" + FixerAPI.key);
        try (InputStream is = url.openStream()) {
            return new String(is.readAllBytes());
        }
    }
    private ExchangeRate parseExchangeRate(String json, Currency from, Currency to) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        JsonObject rates = jsonObject.getAsJsonObject("rates");

        double rateFrom = rates.get(from.code()).getAsDouble();
        double rateTo = rates.get(to.code()).getAsDouble();

        return new ExchangeRate(from, to, LocalDate.now(), rateTo / rateFrom);
    }
    public LocalDate getLastUpdateDate(String json) {
        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        return LocalDate.parse(jsonObject.get("date").getAsString());
    }
}

