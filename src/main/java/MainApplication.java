import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class MainApplication {
    public static void main(String[] args) {

        invokeJsoupTest();


        invokeWebScrapingSteam();
    }
    //funkcja testująca
    private static void invokeJsoupTest() {
        JsoupTest jsoupTest = new JsoupTest();

        try {
            jsoupTest.scrapeAndPrintData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void invokeWebScrapingSteam() {
        WebScrapingSteam webScrapingSteam = new WebScrapingSteam();


        try {
            String userGameListUrl = webScrapingSteam.buildSteamApiUrl(WebScrapingSteam.USER_GAME_LIST_API_URL);
            String userGameListResponseBody = webScrapingSteam.fetchResponseBody(userGameListUrl);


            saveOwnedGamesToFile(userGameListResponseBody);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void saveOwnedGamesToFile(String responseBody) {
        String desktopPath = System.getProperty("user.home") + "/Desktop/";
        String filePath = desktopPath + "owned_games.txt";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            WebScrapingSteam.processOwnedGamesJson(responseBody, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}