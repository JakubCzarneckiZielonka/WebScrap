import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashSet;
import java.util.Set;

public class OwnedGamesFilter {

    public static void main(String[] args) {
        filterOwnedGames("scraped_data.txt", "owned_games.txt", "filtered_games.txt");
    }

    public static void filterOwnedGames(String scrapedDataFile, String ownedGamesFile, String filteredGamesFile) {
        try {
            // Wczytaj posiadane gry z pliku
            Set<String> ownedGames = readOwnedGames(ownedGamesFile);

            // Wczytaj wszystkie gry ze scraped_data
            Set<String> allGames = readAllGames(scrapedDataFile);

            // Odfiltruj gry, zostawiając tylko te, których nie masz
            Set<String> filteredGames = new HashSet<>();
            for (String game : allGames) {

                String filteredGame = game.replaceAll("Link do oferty: .*", "")
                        .replaceAll("Cena: .*", "")
                        .trim();

                // Usuń "Owned Game:" i zamień specjalne znaki
                if (filteredGame.startsWith("Owned Game: ")) {
                    filteredGame = filteredGame.replace("Owned Game: ", "")
                            .replaceAll("®", "")
                            .toUpperCase();
                }

                filteredGames.add(filteredGame);
            }

            filteredGames.removeAll(ownedGames);


            saveFilteredGames(filteredGames, filteredGamesFile);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static Set<String> readOwnedGames(String ownedGamesFile) throws IOException {
        Set<String> ownedGames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(ownedGamesFile))) {
            String line;
            while ((line = reader.readLine()) != null) {

                ownedGames.add(line.trim());
            }
        }
        return ownedGames;
    }

    private static Set<String> readAllGames(String scrapedDataFile) throws IOException {
        Set<String> allGames = new HashSet<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(scrapedDataFile))) {
            String line;
            while ((line = reader.readLine()) != null) {

                allGames.add(line.trim());
            }
        }
        return allGames;
    }

    private static void saveFilteredGames(Set<String> filteredGames, String filteredGamesFile) throws IOException {
        try (PrintWriter writer = new PrintWriter(new FileWriter(filteredGamesFile))) {
            for (String game : filteredGames) {

                writer.println(game);
            }
        }
    }
}
