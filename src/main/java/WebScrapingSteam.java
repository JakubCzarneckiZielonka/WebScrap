import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

public class WebScrapingSteam {

    public static final String USER_API_KEY = "your_steam_api_key";
    public static final String USER_STEAM_ID = "76561199222400750";
    public static final String APP_LIST_API_URL = "https://api.steampowered.com/ISteamApps/GetAppList/v2/";
    public static final String USER_GAME_LIST_API_URL = "http://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
    public static final String WISHLIST_API_URL = "https://store.steampowered.com/wishlist/profiles/";
    public static final String WISHLIST_API_SUFFIX = "/wishlistdata/";
    public static final String APP_DETAILS_API_URL = "http://store.steampowered.com/api/appdetails/";

    public static Map<Integer, String> appList = new HashMap<>();

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        // Pobierz listę posiadanych gier
        try {
            String userGameListUrl = buildSteamApiUrl(USER_GAME_LIST_API_URL);
            Response userGameListResponse = client.newCall(new Request.Builder().url(userGameListUrl).build()).execute();
            String userGameListResponseBody = userGameListResponse.body().string();

            // Zapisz informacje do pliku
            try (PrintWriter writer = new PrintWriter("owned_games.txt")) {
                processOwnedGamesJson(userGameListResponseBody, writer);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        // Pobierz listę życzeń
        try {
            String wishlistUrl = buildSteamApiUrl(WISHLIST_API_URL + USER_STEAM_ID + WISHLIST_API_SUFFIX);
            Response wishlistResponse = client.newCall(new Request.Builder().url(wishlistUrl).build()).execute();
            String wishlistResponseBody = wishlistResponse.body().string();

            // Przetwarzamy dane JSON z informacjami o liście życzeń
            processWishlistJson(wishlistResponseBody);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String buildSteamApiUrl(String apiUrl) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("key", USER_API_KEY);
        urlBuilder.addQueryParameter("steamid", USER_STEAM_ID);
        urlBuilder.addQueryParameter("format", "json");
        return urlBuilder.build().toString();
    }

    public static void processOwnedGamesJson(String responseBody, PrintWriter writer) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(responseBody).getAsJsonObject();

        if (json.has("response") && json.getAsJsonObject("response").has("games")) {
            JsonArray gamesArray = json.getAsJsonObject("response").getAsJsonArray("games");
            for (JsonElement gameElement : gamesArray) {
                JsonObject gameObject = gameElement.getAsJsonObject();
                int appId = gameObject.get("appid").getAsInt();
                String gameName = getAppName(appId);
                System.out.println("Owned Game: " + gameName);
                // Zapisz informacje do pliku
                writer.println("Owned Game: " + gameName);
            }
        } else {
            System.out.println("No owned games found for the user.");
            writer.println("No owned games found for the user.");
        }
    }

    public static void processWishlistJson(String responseBody) {
        JsonObject wishlistJson = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject wishlist = wishlistJson.getAsJsonObject("wishlist");

        if (wishlist != null) {
            for (Map.Entry<String, JsonElement> entry : wishlist.entrySet()) {
                JsonObject gameInfo = entry.getValue().getAsJsonObject();
                String gameName = gameInfo.get("name").getAsString();
                System.out.println("Wishlist Game: " + gameName);
            }
        } else {
            System.out.println("No wishlist found for the user.");
        }
    }

    public String fetchResponseBody(String url) throws IOException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        try (Response response = client.newCall(request).execute()) {
            if (response.isSuccessful()) {
                return response.body().string();
            } else {
                System.out.println("HTTP request failed with code: " + response.code());
                return null;
            }
        }
    }

    private static String getAppName(int appId) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(APP_DETAILS_API_URL).newBuilder();
        urlBuilder.addQueryParameter("appids", String.valueOf(appId));

        String url = urlBuilder.build().toString();

        try {
            Response response = client.newCall(new Request.Builder().url(url).build()).execute();
            String responseBody = response.body().string();

            if (responseBody != null && !responseBody.trim().isEmpty()) {
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                if (json.has(String.valueOf(appId))) {
                    JsonObject appData = json.getAsJsonObject(String.valueOf(appId));

                    if (appData != null && appData.has("data")) {
                        JsonObject data = appData.getAsJsonObject("data");

                        if (data.has("name")) {
                            return data.get("name").getAsString();
                        } else {
                            System.out.println("No name found for appId: " + appId);
                        }
                    } else {
                        System.out.println("No data found for appId: " + appId);
                    }
                } else {
                    System.out.println("No entry found for appId: " + appId);
                }
            } else {
                System.out.println("Response body is null or empty.");
            }

            return "Unknown";
        } catch (IOException e) {
            e.printStackTrace();
            return "Unknown";
        }
    }
}
