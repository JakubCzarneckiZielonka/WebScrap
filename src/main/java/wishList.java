import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.IOException;

public class wishList {

    private static final String USER_API_KEY = "your_steam_api_key";
    private static final String USER_STEAM_ID = "76561198101601494";
    private static final String USER_DATA_API_URL = "http://store.steampowered.com/dynamicstore/userdata/";

    public static void main(String[] args) {
        OkHttpClient client = new OkHttpClient();

        // Pobierz listę posiadanych gier
        try {
            String userDataUrl = USER_DATA_API_URL + "?id=" + USER_STEAM_ID;
            String userDataResponseBody = getHttpResponse(client, userDataUrl);

            // Przetwarzamy dane JSON dotyczące wishlisty
            processWishlistJson(userDataResponseBody);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void processWishlistJson(String responseBody) {
        JsonObject userDataJson = JsonParser.parseString(responseBody).getAsJsonObject();
        JsonObject wishlist = userDataJson.getAsJsonObject("wishlist");

        if (wishlist != null) {
            wishlist.entrySet().forEach(entry -> {
                JsonObject gameInfo = entry.getValue().getAsJsonObject();
                String gameName = gameInfo.get("name").getAsString();
                System.out.println("Wishlist Game: " + gameName);
            });
        } else {
            System.out.println("No wishlist found for the user.");
        }
    }

    private static String getHttpResponse(OkHttpClient client, String url) throws IOException {
        Request request = new Request.Builder().url(buildSteamApiUrl(url)).build();

        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        }
    }

    private static String buildSteamApiUrl(String apiUrl) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("key", USER_API_KEY);
        urlBuilder.addQueryParameter("format", "json");
        return urlBuilder.build().toString();
    }
}
