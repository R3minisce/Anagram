import java.rmi.RemoteException;
import java.util.ArrayList;

/**
 * Classe qui permet la création d'un objet GameThread ( représente une partie en cours ).
 *
 */

public class GameThread implements Runnable {

    private Room room;
    private ArrayList<String> words;

    /**
     * Constructeur standard permettant de créer l'object GameThread
     *
     * @param    room        salon de jeu dont la partie doit être lancée
     * @param    words       liste des anagrammes proposé durant la partie.
     *
     */

    public GameThread(Room room, ArrayList<String> words) {
        this.setRoom(room);
        this.setWords(words);
    }

    // Getters

    public ArrayList<String> getWords() {
        return words;
    }

    public Room getRoom() {
        return room;
    }

    // Setters

    public void setWords(ArrayList<String> words) {
        this.words = words;
    }

    public void setRoom(Room room) {
        this.room = room;
    }
    // Methods
    /**
     * Méthode exécutée par le thread.
     * Met à jour l'anagramme affiché du côté client toute les 10 secondes.
     * En fin de partie, incrémente le score des joueurs et appelle le panneau de classement.
     *
     */

    @Override
    public void run() {
        try {
            for (String word : words) {
                for (Player player : room.getConnectedPlayers()) {
                    player.getClient().createGamePanel(room, word);
                }
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    // Exception attendue et donc normale
                }
            }
            for (Player player : room.getConnectedPlayers()) {
                player.setGlobalScore(player.getGlobalScore() + player.getScore());
                player.getClient().createRankingPanel(room);
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
