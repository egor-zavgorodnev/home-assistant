package home.assistant;


import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
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

import voda24.DataParser;
import volga.StationsInfoService;
import volga.model.ArrivalInfoResponse;


public class MainActivity extends AppCompatActivity {

    OkHttpClient client;

    TextView vodaBalance;

    TextView forthVehicle1;
    TextView forthTime1;

    TextView forthVehicle2;
    TextView forthTime2;

    TextView forthVehicle3;
    TextView forthTime3;

    TextView backVehicle1;
    TextView backTime1;

    TextView backVehicle2;
    TextView backTime2;

    TextView backVehicle3;
    TextView backTime3;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        vodaBalance = findViewById(R.id.voda_balance);

        forthVehicle1 = findViewById(R.id.forthVehicle1);
        forthTime1 = findViewById(R.id.forthTime1);

        forthVehicle2 = findViewById(R.id.forthVehicle2);
        forthTime2 = findViewById(R.id.forthTime2);

        forthVehicle3 = findViewById(R.id.forthVehicle3);
        forthTime3 = findViewById(R.id.forthTime3);

        backVehicle1 = findViewById(R.id.backVehicle1);
        backTime1 = findViewById(R.id.backTime1);

        backVehicle2 = findViewById(R.id.backVehicle2);
        backTime2 = findViewById(R.id.backTime2);

        backVehicle3 = findViewById(R.id.backVehicle3);
        backTime3 = findViewById(R.id.backTime3);

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


    class VolgaBackUpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            backTime1.setText("...");
            backTime2.setText("...");
            backTime3.setText("...");
        }

        @Override
        @RequiresApi(api = Build.VERSION_CODES.R)
        protected String doInBackground(String... strings) {
            final Map<String, Set<String>> BACK_STATIONS_INFO_MAP = Map.of("7795", Set.of("208", "56", "7"));

            StationsInfoService stationsInfoService = new StationsInfoService();

            final String API_URL = "https://api.merlin.tvercard.ru/api/client/v1/stations";

            //piece of shit, need rewrite asap

            Request requestFirstBackStation = new Request.Builder()
                    .url(String.format("%s/%s/routes", API_URL, "7795"))
                    .get()
                    .build();

            client.newCall(requestFirstBackStation).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    backTime1.setText("-");
                    backVehicle1.setText("-");

                    backTime2.setText("-");
                    backVehicle2.setText("-");

                    backTime3.setText("-");
                    backVehicle3.setText("-");
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    List<ArrivalInfoResponse> backStationsInfo =
                            stationsInfoService.getInfo(response.body().string(), BACK_STATIONS_INFO_MAP.get("7795"));

                    MainActivity.this.runOnUiThread(() -> {
                        if (!backStationsInfo.isEmpty()) {
                            backVehicle1.setText(backStationsInfo.get(0).getBusNumber());
                            backTime1.setText(backStationsInfo.get(0).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                                    : (backStationsInfo.get(0).getArrivalMinutes()) + " мин");

                            backVehicle2.setText(backStationsInfo.get(1).getBusNumber());
                            backTime2.setText(backStationsInfo.get(1).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                                    : (backStationsInfo.get(1).getArrivalMinutes()) + " мин");

                            backVehicle3.setText(backStationsInfo.get(2).getBusNumber());
                            backTime3.setText(backStationsInfo.get(2).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                                    : (backStationsInfo.get(2).getArrivalMinutes()) + " мин");

                        }
                    });

                }
            });

            return null;
        }
    }

    class VolgaForthUpdateAsyncTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            forthTime1.setText("...");
            forthTime2.setText("...");
            forthTime3.setText("...");
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
                    forthTime1.setText("-");
                    forthVehicle1.setText("-");

                    forthTime2.setText("-");
                    forthVehicle2.setText("-");

                    forthTime3.setText("-");
                    forthVehicle3.setText("-");

                    return null;
                }

            }

            List<ArrivalInfoResponse> sortedForthStationsInfo = forthStationsInfo.stream()
                    .sorted(Comparator.comparingLong(ArrivalInfoResponse::getArrivalMinutes)).collect(Collectors.toList());

            MainActivity.this.runOnUiThread(() -> {

                if (!forthStationsInfo.isEmpty()) {
                    forthVehicle1.setText(sortedForthStationsInfo.get(0).getBusNumber());
                    forthTime1.setText(sortedForthStationsInfo.get(0).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                            : (sortedForthStationsInfo.get(0).getArrivalMinutes()) + " мин");

                    forthVehicle2.setText(sortedForthStationsInfo.get(1).getBusNumber());
                    forthTime2.setText(sortedForthStationsInfo.get(1).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                            : (sortedForthStationsInfo.get(1).getArrivalMinutes()) + " мин");

                    forthVehicle3.setText(sortedForthStationsInfo.get(2).getBusNumber());
                    forthTime3.setText(sortedForthStationsInfo.get(2).getArrivalMinutes() == Long.MAX_VALUE ? "прибывает/очень далеко"
                            : (sortedForthStationsInfo.get(2).getArrivalMinutes()) + " мин");

                }
            });

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