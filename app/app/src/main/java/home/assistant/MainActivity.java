package home.assistant;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Spinner;
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
import volga.data.InMemoryBackStorage;
import volga.data.InMemoryForthStorage;
import volga.data.Storage;
import volga.model.ArrivalInfoResponse;


public class MainActivity extends AppCompatActivity {

    public static final int BOTTLE_AMOUNT = 30;

    OkHttpClient client;

    TextView vodaBalance;
    TextView backPreset;
    TextView forthPreset;
    TextView version;
    TextView vodaBuyErrorText;

    Button forthButton;
    Button backButton;

    Button vodaPayButton;

    String voda24LoginCookie;

    Spinner vodaSpinner;

    final List<String> bottlesCountList = List.of("4", "5", "6", "7", "8", "9");

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        vodaBalance = findViewById(R.id.voda_balance);
        backPreset = findViewById(R.id.backPreset);
        forthPreset = findViewById(R.id.forthPreset);
        version = findViewById(R.id.version);
        vodaBuyErrorText = findViewById(R.id.voda_buy_error_text);

        forthButton = (Button) findViewById(R.id.forth_button);
        backButton = (Button) findViewById(R.id.back_button);
        vodaPayButton = (Button) findViewById(R.id.vodaPay);

        vodaSpinner = findViewById(R.id.spinner);

        ArrayAdapter<String> vodaSpinnerAdapter = new ArrayAdapter<>(this, R.layout.spinner_list, bottlesCountList);
        vodaSpinnerAdapter.setDropDownViewResource(R.layout.spinner_list);

        vodaSpinner.setAdapter(vodaSpinnerAdapter);

        version.setText("v. " + BuildConfig.VERSION_NAME);

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
            Storage storage = new InMemoryForthStorage();
            final Map<String, Set<String>> FORTH_STATIONS_INFO_MAP = storage.getActivePreset().getStationAndBuses();
            MainActivity.this.runOnUiThread(() -> forthPreset.setText("Preset: " + storage.getActivePreset().getName()));

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
                                            : (info.getArrivalMinutes()) + " мин")));

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
            Storage storage = new InMemoryBackStorage();
            final Map<String, Set<String>> BACK_STATIONS_INFO_MAP = storage.getActivePreset().getStationAndBuses();
            MainActivity.this.runOnUiThread(() -> backPreset.setText("Preset: " + storage.getActivePreset().getName()));

            StationsInfoService stationsInfoService = new StationsInfoService();

            final String API_URL = "https://api.merlin.tvercard.ru/api/client/v1/stations";

            //piece of shit

            List<ArrivalInfoResponse> backStationsInfo = new ArrayList<>();

            for (String currentStation : BACK_STATIONS_INFO_MAP.keySet()) {

                Request requestBackStation = new Request.Builder()
                        .url(String.format("%s/%s/routes", API_URL, currentStation))
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
                                            : (info.getArrivalMinutes()) + " мин")));

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
                    voda24LoginCookie = response.headers().get("Set-Cookie");
                    MainActivity.this.runOnUiThread(() -> {
                        vodaBalance.setText(String.valueOf(DataParser.getBalanceFromJson(accountInfo)));
                    });

                }
            });


            return null;
        }
    }

    class VodaBuyAsyncTask extends AsyncTask<String, String, String> {

        private final Integer price;
        private final Button button;

        public VodaBuyAsyncTask(Integer price, Button button) {
            super();
            this.price = price;
            this.button = button;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            button.setText("...");
        }

        @Override
        protected String doInBackground(String... strings) {

            final String API_URL = "https://xn--24-6kchk3d.xn--p1acf/api/initPay";

            RequestBody body = new MultipartBuilder()
                    .type(MultipartBuilder.FORM)
                    .addFormDataPart("amount", String.valueOf(price))
                    .build();

            Request request = new Request.Builder()
                    .url(API_URL)
                    .header("Cookie", voda24LoginCookie)
                    .post(body)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Request request, IOException e) {
                    MainActivity.this.runOnUiThread(() -> vodaBuyErrorText.setText("Ошибка"));
                }

                @Override
                public void onResponse(Response response) throws IOException {
                    final String urlInfo = response.body().string();
                    String formUrlFromJson = DataParser.getFormUrlFromJson(urlInfo);
                    Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(formUrlFromJson));
                    startActivity(browserIntent);
                    voda24LoginCookie = response.headers().get("Set-Cookie");
                    MainActivity.this.runOnUiThread(() -> {
                        button.setText("Оплатить");
                        vodaBuyErrorText.setText("");
                    });
                }
            });


            return null;
        }
    }

    public void updateVodaBalance(View view) throws IOException {
        new VodaUpdateAsyncTask().execute();
    }

    public void buyVoda(View view) {
        Integer bottleCount = Integer.valueOf(vodaSpinner.getSelectedItem().toString());
        new VodaBuyAsyncTask(bottleCount * BOTTLE_AMOUNT, vodaPayButton).execute();
    }

}