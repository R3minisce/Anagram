import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe qui représente l'interface de détails d'une room.
 */

public class RoomDetailsPanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");
    private static final Color DARK_BLUE_COLOR = Color.decode("#123c5d");

    private ServerController serverController;
    private Player player;
    private Room room;
    private JPanel playerPanel;

    /**
     * Constructeur standard permettant de créer un objet de la classe RoomDetailsPanel
     * @param player Le joueur courant.
     * @param serverController      Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     * @param room La room dans laquelle se trouve le joueur courant.
     */

    public RoomDetailsPanel(Player player, ServerController serverController, Room room) {

        this.serverController = serverController;
        this.player = player;
        this.room = room;

        // Panel attributes
        this.setLayout(null);
        this.setBackground(BLUE_COLOR);

        // Title label
        JLabel titleLabel = new JLabel("ANAGRAM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        titleLabel.setSize(200, 30);
        titleLabel.setLocation(20, 10);

        // Server label
        JLabel serverStatus = new JLabel("Server Status");
        buildLabel(serverStatus);
        serverStatus.setSize(100, 30);
        serverStatus.setLocation(470, 10);

        // Player Panel
        playerPanel = new JPanel();
        playerPanel.setSize(250, 200);
        playerPanel.setLocation(300, 80);
        playerPanel.setBackground(BLUE_COLOR);
        playerPanel.setLayout(new GridLayout(0, 2, 10, 10));

        // Player Button
        ArrayList<Player> playerList = room.getConnectedPlayers();
        this.updatePlayerList(room);

        // Room info Panel
        JPanel roomInfoPanel = new JPanel();
        roomInfoPanel.setLayout(new BoxLayout(roomInfoPanel, BoxLayout.Y_AXIS));
        roomInfoPanel.setBackground(DARK_BLUE_COLOR);
        roomInfoPanel.setLocation(30, 80);
        roomInfoPanel.setSize(250, 100);

        // Room Name label
        JLabel roomNameLabel = new JLabel(room.getRoomName());
        buildLabel(roomNameLabel);

        // NB player label
        JLabel nbPlayerLabel = new JLabel(room.getConnectedPlayers().size() + "/" + room.getMaxPlayer());
        buildLabel(nbPlayerLabel);

        // Lang label
        JLabel langLabel = new JLabel(room.getDictionary().getLanguage());
        buildLabel(langLabel);

        // nbWord label
        JLabel nbWordLabel = new JLabel(String.valueOf(room.getMaxWord()));
        buildLabel(nbWordLabel);

        // return button
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(this::returnButtonListener);
        returnButton.setSize(110, 30);
        returnButton.setLocation(450, 310);

        // adding items to main panel
        this.add(titleLabel);
        this.add(serverStatus);
        this.add(returnButton);
        this.add(roomInfoPanel);
        this.add(playerPanel);

        // adding items to room info Panel
        roomInfoPanel.add(roomNameLabel);
        roomInfoPanel.add(nbPlayerLabel);
        roomInfoPanel.add(langLabel);
        roomInfoPanel.add(nbWordLabel);
    }

    /**
     * Méthode qui crée et définit le style, les attributs des labels de la classe.
     *
     * @param    label       label ciblé
     *
     */

    public void buildLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 0));
    }

    /**
     * Méthode de type ActionEvent qui une fois le salon remplit, charge la fenêtre propre à la partie.
     *
     * @param    room       salon de jeu ciblé
     * @param    word       anagramme envoyé par le serveur
     */

    public void connectRoomButtonListener(Room room, String word) {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        GamePanel game = new GamePanel(player, room, serverController, word);
        frame.update(game);
    }

    /**
     * Méthode de type ActionEvent qui permet le retour vers la page ( panneau ) précédente.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void returnButtonListener(ActionEvent event) {

        this.serverController.leaveRoom(room.getRoomName(), player);

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        LobbyPanel lobbyPanel = new LobbyPanel(player, serverController);
        frame.update(lobbyPanel);

    }

    /**
     * Méthide permettant de rafraichir la liste des joueurs connectés actuellement à cette room.
     * @param room La room courante de l'utilisateur.
     */

    public void updatePlayerList(Room room) {
        this.room = room;
        ArrayList<Player> playerList = room.getConnectedPlayers();

        this.playerPanel.removeAll();

        for (Player connectedPlayer : playerList) {
            JLabel playerLabel = new JLabel(connectedPlayer.getUsername());
            playerLabel.setBackground(DARK_BLUE_COLOR);
            playerLabel.setOpaque(true);
            playerLabel.setForeground(Color.WHITE);
            playerLabel.setFont(new Font("Lucida Console", Font.PLAIN, 12));
            playerLabel.setHorizontalAlignment(JLabel.CENTER);
            playerPanel.add(playerLabel);
        }

        this.revalidate();
        this.repaint();
    }
}
