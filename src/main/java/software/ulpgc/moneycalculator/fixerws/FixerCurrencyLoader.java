package software.ulpgc.moneycalculator.fixerws;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import software.ulpgc.moneycalculator.Currency;
import software.ulpgc.moneycalculator.CurrencyLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;

public class FixerCurrencyLoader implements CurrencyLoader {
    @Override
    public List<Currency> load() {
        try {
            return toList(loadJson());
        } catch (IOException e) {
            return emptyList();
        }
    }

    private List<Currency> toList(String json) {

        JsonObject jsonObject = new Gson().fromJson(json, JsonObject.class);
        List<Currency> currencies = jsonObject.get("supported_codes")
                .getAsJsonArray().asList().stream()
                .map(currency -> {
                    JsonArray ratesArray = currency.getAsJsonArray();
                    return new Currency(ratesArray.get(0).getAsString(), ratesArray.get(1).getAsString());
                }).collect(Collectors.toList());
        return currencies;
    }


    private String loadJson() throws IOException {
        URL url = new URL("https://v6.exchangerate-api.com/v6/" + FixerAPI.key + "/codes");
        try (InputStream is = url.openStream()) {
            return new String(is.readAllBytes());
        }
    }
}
