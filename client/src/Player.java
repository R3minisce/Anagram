import java.io.Serializable;
import java.util.Comparator;

/**
 * Classe permettant de représenter un joueur dans le système.
 */

public class Player implements Serializable {

    private String username;
    private int score;
    private int globalScore;
    private IClient client;

    /**
     * Constructeur standard permettant de créer un objet de type Player.
     * @param username Le nom du joueur à créer.
     * @param globalScore Le score global du joueur à créer.
     */

    public Player(String username, int globalScore) {
        this.setUsername(username);
        this.setScore(0);
        this.setGlobalScore(globalScore);
    }

    // Getters

    public String getUsername() {
        return username;
    }

    public int getScore() {
        return score;
    }

    public int getGlobalScore() {
        return globalScore;
    }

    public IClient getClient() {
        return client;
    }

    // Setters

    public void setGlobalScore(int globalScore) {
        this.globalScore = globalScore;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public void setClient(IClient client) {
        this.client = client;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * Méthode statique à appeler si l'on veut trier les joueurs par rapport à leurs scores.
     */

    public static Comparator<Player> sortByScore = new Comparator<Player>() {

        public int compare(Player s1, Player s2) {

            int scorePlayer1 = s1.getScore();
            int scorePlayer2 = s2.getScore();

            return scorePlayer2-scorePlayer1;
        }};
}