package home.assistant;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.okhttp.Callback;
import com.squareup.okhttp.MultipartBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import home.assistant.model.BusInfo;
import voda24.DataParser;
import volga.StationsInfoService;
import volga.model.ArrivalInfoResponse;


public class MainActivity extends AppCompatActivity {

    OkHttpClient client;

    TextView vodaBalance;

    Button forthButton;
    Button backButton;

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        vodaBalance = findViewById(R.id.voda_balance);

        forthButton = (Button) findViewById(R.id.forth_button);
        backButton = (Button) findViewById(R.id.back_button);

    }

    @RequiresApi(api = Build.VERSION_CODES.R)
    @Override
    protected void onStart() {
        super.onStart();

        try {
            updateVodaBalance(null);
            updateVolgaForthInfo(null);
            updateVolgaBackInfo(null);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    class VolgaForthUpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            forthButton.setText("...");
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.R)
        protected String doInBackground(String... strings) {
            final Map<String, Set<String>> FORTH_STATIONS_INFO_MAP = Map.of("8182", Set.of("208", "56"), "8183", Set.of("7"));

            StationsInfoService stationsInfoService = new StationsInfoService();

            final String API_URL = "https://api.merlin.tvercard.ru/api/client/v1/stations";

            List<ArrivalInfoResponse> forthStationsInfo = new ArrayList<>();

            for (String currentStation : FORTH_STATIONS_INFO_MAP.keySet()) {
                Request requestForthStation = new Request.Builder()
                        .url(String.format("%s/%s/routes", API_URL, currentStation))
                        .get()
                        .build();

                try {
                    Response response = client.newCall(requestForthStation).execute();

                    forthStationsInfo.addAll(stationsInfoService.getInfo(response.body().string(), FORTH_STATIONS_INFO_MAP.get(currentStation)));

                } catch (IOException e) {
                    forthButton.setText("no internet");
                    return null;
                }

            }

            List<ArrivalInfoResponse> sortedForthStationsInfo = forthStationsInfo.stream()
                    .sorted(Comparator.comparingLong(ArrivalInfoResponse::getArrivalMinutes)).collect(Collectors.toList());

            MainActivity.this.runOnUiThread(() -> {

                if (!sortedForthStationsInfo.isEmpty()) {

                    List<BusInfo> records = new ArrayList<>();

                    sortedForthStationsInfo.forEach(info -> records.add(
                            new BusInfo(info.getBusNumber(),
                                    info.getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                                            : (sortedForthStationsInfo.get(0).getArrivalMinutes()) + " мин")));

                    ListView forthInfoList = findViewById(R.id.forthInfoList);

                    ForthInfoAdapter forthInfoAdapter = new ForthInfoAdapter(MainActivity.this, R.layout.forth_info_item, records);

                    forthInfoList.setAdapter(forthInfoAdapter);
                }
            });

            forthButton.setText(R.string.refresh_sym);

            return null;
        }
    }

    class VolgaBackUpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            backButton.setText("...");
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.R)
        protected String doInBackground(String... strings) {
            final Map<String, Set<String>> BACK_STATIONS_INFO_MAP = Map.of("7795", Set.of("208", "56", "7"));

            StationsInfoService stationsInfoService = new StationsInfoService();

            final String API_URL = "https://api.merlin.tvercard.ru/api/client/v1/stations";

            //piece of shit, need rewrite asap

            List<ArrivalInfoResponse> backStationsInfo = new ArrayList<>();

            for (String currentStation : BACK_STATIONS_INFO_MAP.keySet()) {

                Request requestBackStation = new Request.Builder()
                        .url(String.format("%s/%s/routes", API_URL, "7795"))
                        .get()
                        .build();

                try {
                    Response response = client.newCall(requestBackStation).execute();

                    backStationsInfo.addAll(stationsInfoService.getInfo(response.body().string(), BACK_STATIONS_INFO_MAP.get(currentStation)));

                } catch (IOException e) {
                    backButton.setText("no internet");
                    return null;
                }
            }

            List<ArrivalInfoResponse> sortedBackStationsInfo = backStationsInfo.stream()
                    .sorted(Comparator.comparingLong(ArrivalInfoResponse::getArrivalMinutes)).collect(Collectors.toList());

            MainActivity.this.runOnUiThread(() -> {

                if (!sortedBackStationsInfo.isEmpty()) {

                    List<BusInfo> records = new ArrayList<>();

                    sortedBackStationsInfo.forEach(info -> records.add(
                            new BusInfo(info.getBusNumber(),
                                    info.getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                                            : (sortedBackStationsInfo.get(0).getArrivalMinutes()) + " мин")));

                    ListView backInfoList = findViewById(R.id.backInfoList);

                    BackInfoAdapter backInfoAdapter = new BackInfoAdapter(MainActivity.this, R.layout.back_info_item, records);

                    backInfoList.setAdapter(backInfoAdapter);
                }
            });

            backButton.setText(R.string.refresh_sym);

            return null;
        }
    }

    public void updateVolgaForthInfo(View view) {
        new VolgaForthUpdateAsyncTask().execute();
    }

    public void updateVolgaBackInfo(View view) {
        new VolgaBackUpdateAsyncTask().execute();
    }

    class VodaUpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            vodaBalance.setText("...");
        }

        @Override
        protected String doInBackground(String... strings) {
            final String KEY = "116716";

            final String API_URL = "https://xn--24-6kchk3d.xn--p1acf/api/login";

            RequestBody body = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("phone", KEY)
                    .build();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.this.runOnUiThread(() -> vodaBalance.setText("-"));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String accountInfo = response.body().string();
                    MainActivity.this.runOnUiThread(() -> {
                        vodaBalance.setText(String.valueOf(DataParser.getBalanceFromJson(accountInfo)));
                    });

                }
            });

            return null;
        }
    }

    public void updateVodaBalance(View view) throws IOException {
        new VodaUpdateAsyncTask().execute();
    }

}