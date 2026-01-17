# SimXchange

A simple Android currency exchange rate application that allows users to convert between currencies and view live exchange rates.

## Main Libraries Used

| Library | Purpose |
|---------|---------|
| **OkHttp** | HTTP client for API requests |
| **Material Design 3** | Modern UI components and theming |
| **ViewBinding** | Type-safe view access |
| **RecyclerView** | Efficient list display for rates |
| **ConstraintLayout** | Flexible UI layouts |

## API

This app uses [ExchangeRate-API](https://www.exchangerate-api.com/) for live currency data.

## How to Run

1. **Open in Android Studio**
   - File → Open → Select the `SimXChange` folder

2. **Sync Gradle**
   - Android Studio will automatically sync. If not, click "Sync Now" in the notification bar.

3. **Run the App**
   - Select an emulator or connected device from the device dropdown
   - Click the green **Run ▶** button (or press `Shift + F10`)

## Features

- Convert between 7 major currencies (USD, EUR, GBP, JPY, IDR, AUD, CAD)
- View live exchange rates for 20 currencies
- Swap currencies with one tap
- Clean, modern UI with gradient theme
