package home.assistant;


import static voda24.DbHelper.createDbIfNotExists;
import static voda24.DbHelper.getActualValue;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
import java.util.List;

import voda24.DataParser;


public class MainActivity extends AppCompatActivity {


    OkHttpClient client;

    TextView vodaBalance;

    TextView version;
    TextView vodaBuyErrorText;

    Button vodaPayButton;

    String voda24LoginCookie;

    Spinner vodaSpinner;

    final List<String> bottlesCountList = List.of("4", "5", "6", "7", "8", "9");

    @Override
    @RequiresApi(api = Build.VERSION_CODES.R)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        createDbIfNotExists(getBaseContext());

        setContentView(R.layout.activity_main);
        client = new OkHttpClient();

        vodaBalance = findViewById(R.id.voda_balance);
        version = findViewById(R.id.version);
        vodaBuyErrorText = findViewById(R.id.voda_buy_error_text);

        vodaPayButton = findViewById(R.id.vodaPay);

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
        } catch (IOException e) {
            e.printStackTrace();
        }

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
        Integer BOTTLE_AMOUNT = getActualValue(getBaseContext());
        Integer bottleCount = Integer.valueOf(vodaSpinner.getSelectedItem().toString());
        new VodaBuyAsyncTask(bottleCount * BOTTLE_AMOUNT, vodaPayButton).execute();
    }

    public void openSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}