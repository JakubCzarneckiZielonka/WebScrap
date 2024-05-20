import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
/*
Klasa pobierająca i zapisująca pobrane oferty ze strony
 */
public class JsoupTest {
    public static void main(String[] args) {
        // Rozpoczęcie procesu pobierania informacji z internetu
        try {
            scrapeAndPrintData();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void scrapeAndPrintData() throws IOException {

        String baseUrl = "https://dostepy.com/pl/nowe-produkty?page=";
        //liczba produktów
        int totalOffers = 0;


        String desktopPath = System.getProperty("user.home") + "/Desktop/";

        String filePath = desktopPath + "scraped_data.txt";

        // Otwarcie strumienia do zapisu danych do pliku o ścieżce filePath.
        try (PrintWriter writer = new PrintWriter(new FileWriter(filePath))) {
            for (int page = 1; page <= 100; page++) {
                String url = baseUrl + page;
                Document document = Jsoup.connect(url).get();

                for (Element product : document.select(".product-description")) {
                    String offerLink = product.select("h2.product-title a").attr("href");
                    String productName = product.select("h2.product-title a").text(); // Zmieniony selektor
                    String price = product.select(".price").text();

                    // Wyświetl informacje w terminalu
                    System.out.println("Nazwa produktu: " + productName);
                    System.out.println("Link do oferty: " + offerLink);
                    System.out.println("Cena: " + price);
                    System.out.println("-------------------------------");

                    // Zapisz informacje do pliku
                    writer.println("Nazwa produktu: " + productName);
                    writer.println("Link do oferty: " + offerLink);
                    writer.println("Cena: " + price);
                    writer.println("-------------------------------");

                    totalOffers++;
                }
            }
        }


        System.out.println("Liczba ofert: " + totalOffers);
    }
}
