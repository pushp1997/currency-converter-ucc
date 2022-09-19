package com.example.currencyconverter;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.android.volley.Request;
import com.android.volley.toolbox.JsonObjectRequest;



public class MainActivity extends AppCompatActivity {
    private TextView outputTextView;
    private Spinner fromSpinner;
    private Spinner toSpinner;
    private EditText amount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        outputTextView = findViewById(R.id.textView5);
        fromSpinner = findViewById(R.id.spinner);
        toSpinner = findViewById(R.id.spinner2);
        amount = findViewById(R.id.editTextNumberDecimal);
        Button convertButton = findViewById(R.id.button);

        populateCurrencyDropdowns();

        convertButton.setOnClickListener(v -> convertCurrency(
                amount.getText().toString(),
                fromSpinner.getSelectedItem().toString().substring(0, 3),
                toSpinner.getSelectedItem().toString().substring(0, 3)
        ));
    }

    private void populateCurrencyDropdowns(){
        // Requesting currency symbols from the provider apilayer.com
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, Constants.API_SYMBOLS_URL, null, response -> {
                    ArrayList<String> currencies_array = new ArrayList<>();
                    try {
                        JSONObject symbolsJson = response.getJSONObject("symbols");
                        Iterator<String> keys = symbolsJson.keys();
                        while(keys.hasNext()) {
                            String key = keys.next();
                            currencies_array.add(key +": "+symbolsJson.get(key));
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    // Populating the list of available currencies in the dropdown
                    MainActivity.this.runOnUiThread(() -> {
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this,
                                android.R.layout.simple_spinner_item, currencies_array);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        fromSpinner.setAdapter(adapter);
                        toSpinner.setAdapter(adapter);
                    });
                }, Throwable::printStackTrace){
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String>  params = new HashMap<>();
                params.put("apikey", Constants.API_KEY);
                return params;
            }
        };

        ApiRequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);

    }

    private void convertCurrency(String amount, String from, String to){

        // Requesting conversion from the provider apilayer.com
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                (Request.Method.GET, String.format("%s?to=%s&from=%s&amount=%s", Constants.API_CONVERSION_URL, to, from, amount), null, response -> {
                    try {
                        String result = response.get("result").toString();
                        outputTextView.setText(String.format("Output: %s %s", result, to));
                    } catch (JSONException e) {
                        outputTextView.setText(e.toString());
                    }
                }, Throwable::printStackTrace){
            @Override
            public Map<String, String> getHeaders()  {
                Map<String, String>  params = new HashMap<>();
                params.put("apikey", Constants.API_KEY);
                return params;
            }
        };

        ApiRequestQueueSingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }
}

