package ru.tvercard.service;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.tvercard.model.Route;

public class DataParser {

    private static Gson gson;

    static {
        gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .create();
    }

    public static Route[] getRoutesFromJson(String json) {
        return gson.fromJson(json, Route[].class);
    }
}
