package com.example.myapplication;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.example.myapplication.databinding.ActivityRatesBinding;
import com.example.myapplication.BuildConfig;

import org.json.JSONObject;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;

import java.util.List;
import java.util.Locale;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Activity displaying live exchange rates for multiple currencies.
 */
public class RatesActivity extends AppCompatActivity {

    private ActivityRatesBinding binding;
    private OkHttpClient client;
    private RatesAdapter adapter;
    private List<CurrencyItem> currencyList;

    private static final String API_KEY = BuildConfig.API_KEY;
    private static final String BASE_CURRENCY = "USD";

    // Currencies to display
    private static final String[] DISPLAY_CURRENCIES = {
            "USD", "EUR", "GBP", "JPY", "AUD", "CAD", "CHF", "CNY", "NZD", "IDR",
            "SGD", "HKD", "KRW", "INR", "MXN", "BRL", "ZAR", "RUB", "SEK", "NOK"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRatesBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Initialize OkHttp client
        client = new OkHttpClient();

        // Initialize RecyclerView
        currencyList = new ArrayList<>();
        adapter = new RatesAdapter(currencyList);
        binding.recyclerViewRates.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerViewRates.setAdapter(adapter);

        // Back button
        binding.btnBack.setOnClickListener(v -> finish());

        // Load rates
        loadRates();
    }

    private void loadRates() {
        // Show loading state
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.recyclerViewRates.setVisibility(View.GONE);
        binding.tvError.setVisibility(View.GONE);

        // Build API URL for latest rates
        String url = String.format(
                Locale.US,
                "https://v6.exchangerate-api.com/v6/%s/latest/%s",
                API_KEY,
                BASE_CURRENCY
        );

        Request request = new Request.Builder()
                .url(url)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.tvError.setVisibility(View.VISIBLE);
                    binding.tvError.setText("Network error: " + e.getMessage());
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (!response.isSuccessful()) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("API Error: " + response.code());
                    });
                    return;
                }

                try {
                    String responseBody = response.body().string();
                    JSONObject json = new JSONObject(responseBody);
                    String result = json.getString("result");

                    if ("success".equals(result)) {
                        JSONObject conversionRates = json.getJSONObject("conversion_rates");
                        List<CurrencyItem> items = new ArrayList<>();

                        DecimalFormat df = new DecimalFormat("#,##0.####");

                        // Iterate through display currencies
                        for (String currencyCode : DISPLAY_CURRENCIES) {
                            if (conversionRates.has(currencyCode)) {
                                double rate = conversionRates.getDouble(currencyCode);
                                items.add(new CurrencyItem(currencyCode, df.format(rate)));
                            }
                        }

                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.recyclerViewRates.setVisibility(View.VISIBLE);
                            currencyList.clear();
                            currencyList.addAll(items);
                            adapter.notifyDataSetChanged();
                        });
                    } else {
                        String errorType = json.optString("error-type", "Unknown error");
                        runOnUiThread(() -> {
                            binding.progressBar.setVisibility(View.GONE);
                            binding.tvError.setVisibility(View.VISIBLE);
                            binding.tvError.setText("API Error: " + errorType);
                        });
                    }
                } catch (Exception e) {
                    runOnUiThread(() -> {
                        binding.progressBar.setVisibility(View.GONE);
                        binding.tvError.setVisibility(View.VISIBLE);
                        binding.tvError.setText("Parse error: " + e.getMessage());
                    });
                }
            }
        });
    }
}
