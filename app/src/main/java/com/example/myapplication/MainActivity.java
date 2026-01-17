package com.example.myapplication;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.myapplication.databinding.ActivityMainBinding;
import com.example.myapplication.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private OkHttpClient client;
    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "JPY", "IDR", "AUD", "CAD"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);

        // Initialize ViewBinding
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Handle system bar insets
        ViewCompat.setOnApplyWindowInsetsListener(binding.main, (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialize OkHttp client
        client = new OkHttpClient();

        // Setup spinners with currency codes
        setupSpinners();

        // Set click listener for convert button
        binding.btnConvert.setOnClickListener(v -> performConversion());

        // Set click listener for swap button (both container and text)
        binding.tvSwap.setOnClickListener(v -> swapCurrencies());
        binding.btnSwapContainer.setOnClickListener(v -> swapCurrencies());

        // Set click listener for View All Rates button
        binding.btnViewRates.setOnClickListener(v -> {
            android.content.Intent intent = new android.content.Intent(this, RatesActivity.class);
            startActivity(intent);
        });
    }

    private void setupSpinners() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(
                this,
                R.layout.spinner_item,
                CURRENCIES
        );
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);

        binding.spinnerFrom.setAdapter(adapter);
        binding.spinnerTo.setAdapter(adapter);

        // Set default selections (USD to EUR)
        binding.spinnerFrom.setSelection(0); // USD
        binding.spinnerTo.setSelection(1);   // EUR
    }

    private void swapCurrencies() {
        int fromPosition = binding.spinnerFrom.getSelectedItemPosition();
        int toPosition = binding.spinnerTo.getSelectedItemPosition();

        binding.spinnerFrom.setSelection(toPosition);
        binding.spinnerTo.setSelection(fromPosition);
    }

    private void performConversion() {
        String amountStr = binding.etAmount.getText().toString().trim();

        // Validate input
        if (amountStr.isEmpty()) {
            Toast.makeText(this, "Please enter an amount", Toast.LENGTH_SHORT).show();
            return;
        }

        double amount;
        try {
            amount = Double.parseDouble(amountStr);
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid amount format", Toast.LENGTH_SHORT).show();
            return;
        }

        if (amount <= 0) {
            Toast.makeText(this, "Amount must be greater than 0", Toast.LENGTH_SHORT).show();
            return;
        }

        String fromCurrency = binding.spinnerFrom.getSelectedItem().toString();
        String toCurrency = binding.spinnerTo.getSelectedItem().toString();

        // Show loading state
        binding.btnConvert.setEnabled(false);
        binding.btnConvert.setText("Converting...");
        binding.tvConvertedAmount.setText("...");
        binding.tvResult.setText("");
        binding.tvExchangeRate.setVisibility(android.view.View.GONE);

        // Build API URL
        String url = String.format(
                Locale.US,
                "https://v6.exchangerate-api.com/v6/%s/pair/%s/%s/%.2f",
                API_KEY,
                fromCurrency,
                toCurrency,
                amount
        );

        // Create request
        Request request = new Request.Builder()
                .url(url)
                .build();

        // Execute request asynchronously
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    binding.btnConvert.setEnabled(true);
                    binding.btnConvert.setText("Convert");
                    binding.tvResult.setText("");
                    Toast.makeText(MainActivity.this,
                            "Network error: " + e.getMessage(),
                            Toast.LENGTH_LONG).show();
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        binding.btnConvert.setEnabled(true);
                        binding.btnConvert.setText("Convert");
                        binding.tvResult.setText("");
                        Toast.makeText(MainActivity.this,
                                "API Error: " + response.code(),
                                Toast.LENGTH_LONG).show();
                    });
                    return;
                }

                String responseBody = response.body().string();

                try {
                    JSONObject json = new JSONObject(responseBody);
                    String result = json.getString("result");

                    if ("success".equals(result)) {
                        double conversionResult = json.getDouble("conversion_result");
                        double conversionRate = json.getDouble("conversion_rate");

                        // Format the numbers nicely
                        DecimalFormat df = new DecimalFormat("#,##0.00");
                        String formattedAmount = df.format(amount);
                        String formattedResult = df.format(conversionResult);

                        String displayText = String.format(
                                Locale.US,
                                "%s %s = %s %s",
                                formattedAmount,
                                fromCurrency,
                                formattedResult,
                                toCurrency
                        );

                        String rateText = String.format(Locale.US, "1 %s = %.4f %s", fromCurrency, conversionRate, toCurrency);

                        runOnUiThread(() -> {
                            binding.btnConvert.setEnabled(true);
                            binding.btnConvert.setText("Convert");
                            binding.tvConvertedAmount.setText(formattedResult);
                            binding.tvResult.setText(displayText);
                            binding.tvExchangeRate.setVisibility(android.view.View.VISIBLE);
                            binding.tvExchangeRate.setText(rateText);
                        });
                    } else {
                        String errorType = json.optString("error-type", "Unknown error");
                        runOnUiThread(() -> {
                            binding.btnConvert.setEnabled(true);
                            binding.btnConvert.setText("Convert");
                            binding.tvResult.setText("");
                            Toast.makeText(MainActivity.this,
                                    "API Error: " + errorType,
                                    Toast.LENGTH_LONG).show();
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        binding.btnConvert.setEnabled(true);
                        binding.btnConvert.setText("Convert");
                        binding.tvResult.setText("");
                        Toast.makeText(MainActivity.this,
                                "Error parsing response: " + e.getMessage(),
                                Toast.LENGTH_LONG).show();
                    });
                }
            }
        });
    }
}