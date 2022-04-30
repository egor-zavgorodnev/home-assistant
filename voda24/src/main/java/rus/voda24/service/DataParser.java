package rus.voda24.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;

public class DataParser {

    private static final String BALANCE_JSON_PROPERTY = "balance";
    private static Gson gson;


    static {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public static String getBalanceFromJson(String json) {
        JsonObject obj = gson.fromJson(json, JsonObject.class);

        return obj.get(BALANCE_JSON_PROPERTY).getAsString();
    }
}