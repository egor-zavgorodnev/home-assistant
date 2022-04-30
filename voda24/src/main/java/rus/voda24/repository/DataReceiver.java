package rus.voda24.repository;

import com.squareup.okhttp.*;
import org.apache.commons.configuration2.FileBasedConfiguration;
import org.apache.commons.configuration2.PropertiesConfiguration;
import org.apache.commons.configuration2.builder.FileBasedConfigurationBuilder;
import org.apache.commons.configuration2.builder.fluent.Parameters;
import org.apache.commons.configuration2.ex.ConfigurationException;

import java.io.IOException;

public class DataReceiver {

    private final String CONFIGURATION_FILE = "voda.properties";
    private final String API_PROP = "balance_api";
    private final String KEY = "key";
    private final String FORM_DATA_PARAM_NAME = "phone";

    private FileBasedConfiguration configuration;

    public DataReceiver() {
        Parameters params = new Parameters();

        FileBasedConfigurationBuilder<FileBasedConfiguration> builder =
                new FileBasedConfigurationBuilder<FileBasedConfiguration>(PropertiesConfiguration.class)
                        .configure(params.properties()
                                .setFileName(CONFIGURATION_FILE));
        try {
            configuration = builder.getConfiguration();
        } catch (ConfigurationException e) {
            e.printStackTrace();
        }
    }

    public String getAccountInfo() throws IOException {

        OkHttpClient client = new OkHttpClient();

        RequestBody body = new MultipartBuilder()
                .type(MultipartBuilder.FORM)
                .addFormDataPart(FORM_DATA_PARAM_NAME, configuration.getString(KEY))
                .build();

        Request request = new Request.Builder()
                .url(configuration.getString(API_PROP))
                .post(body)
                .build();

        Response response = client.newCall(request).execute();

        return response.body().string();
    }

}
