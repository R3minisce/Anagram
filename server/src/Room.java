import java.io.Serializable;
import java.util.ArrayList;

/**
 * Classe qui permet la création d'un objet de type Room ( représente un salon de jeu ).
 *
 */

public class Room implements Serializable {

    private String roomName;
    private AnagramDictionary dictionary;
    private int maxPlayer;
    private int maxWord;
    private ArrayList<Player> connectedPlayers;
    private ArrayList<String> whitelistedPlayers;

    /**
     * Constructeur standard qui permet l'instanciation d'un object de type Room.
     *
     * @param    roomName       nom du salon
     * @param    maxPlayer      nombre de joueur maximum dans le salon
     * @param    maxWord        nombre de mot proposé dans une partie
     * @param    dictionary     Dictionnaire utilisé dans le salon
     *
     */

    public Room(String roomName, int maxPlayer, int maxWord, AnagramDictionary dictionary) {

        this.setRoomName(roomName);
        this.setMaxPlayer(maxPlayer);
        this.setMaxWord(maxWord);
        this.setConnectedPlayers(new ArrayList<Player>());
        this.setWhitelistedPlayers(new ArrayList<String>());
        this.setDictionary(dictionary);
    }

    // Getters

    public String getRoomName() {
        return roomName;
    }

    public int getMaxPlayer() {
        return maxPlayer;
    }

    public int getMaxWord() {
        return maxWord;
    }

    public ArrayList<Player> getConnectedPlayers() {
        return this.connectedPlayers;
    }

    public ArrayList<String> getWhitelistedPlayers() {
        return whitelistedPlayers;
    }

    public AnagramDictionary getDictionary() {
        return dictionary;
    }

    // Setters

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public void setMaxPlayer(int maxPlayer) {
        this.maxPlayer = maxPlayer;
    }

    public void setMaxWord(int maxWord) {
        this.maxWord = maxWord;
    }

    public void setConnectedPlayers(ArrayList<Player> connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public void clearConnectedPlayers(ArrayList<Player> connectedPlayers) { this.connectedPlayers.clear(); }

    public void setWhitelistedPlayers(ArrayList<String> whitelistedPlayers) {
        this.whitelistedPlayers = whitelistedPlayers;
    }

    public void setDictionary(AnagramDictionary dictionary) {
        this.dictionary = dictionary;
    }

    // Methods

    /**
     * Méthode permettant l'ajout d'un joueur à la liste des joueurs connectés au salon.
     *
     * @param    player      joueur devant être ajouté.
     *
     */

    public void addUsers(Player player) { connectedPlayers.add(player); }

    /**
     * Méthode permettant de retirer un joueur à la liste des joueurs connectés au salon.
     *
     * @param    player      joueur devant être retiré.
     *
     */

    public void removeUsers(Player player) {

        int i = 0;
        while(i < connectedPlayers.size() && !this.connectedPlayers.get(i).getUsername().equals(player.getUsername())) {
            i++;
        }

        this.connectedPlayers.remove(i);
    }
}