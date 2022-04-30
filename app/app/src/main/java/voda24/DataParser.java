package voda24;

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

    public static Long getBalanceFromJson(String json) {
        JsonObject obj = gson.fromJson(json, JsonObject.class);

        return Double.valueOf(obj.get(BALANCE_JSON_PROPERTY).getAsString()).longValue();
    }
}
