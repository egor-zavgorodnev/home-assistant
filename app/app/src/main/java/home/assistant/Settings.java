package home.assistant;

import static voda24.DbHelper.getActualValue;
import static voda24.DbHelper.updateBottlePrice;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class Settings extends AppCompatActivity {

    TextView message;

    EditText bottlePriceInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        message = findViewById(R.id.save_message);
        bottlePriceInput = findViewById(R.id.bottle_price_input);

        bottlePriceInput.setText(getActualValue(getBaseContext()).toString());
    }


    public void saveSettings(View view) {
        updateBottlePrice(getBaseContext(), Integer.valueOf(bottlePriceInput.getText().toString()));
        message.setText("OK!");
    }
}