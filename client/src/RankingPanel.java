import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collections;

/**
 * Classe qui permet de représenter l'interface de classement à la fin d'une partie.
 */

public class RankingPanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");
    private static final Color DARK_BLUE_COLOR = Color.decode("#123c5d");

    private ServerController serverController;
    private Player player;
    private Room room;

    /**
     * Constructeur standard qui permet de créer un objet de type RankingPanel.
     * @param player Le joueur courant.
     * @param serverController  Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     * @param room La room dans laquelle le joueur se trouve.
     */

    public RankingPanel(Player player, ServerController serverController, Room room) {

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
        serverStatus.setForeground(Color.WHITE);
        serverStatus.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        serverStatus.setSize(100, 30);
        serverStatus.setLocation(470, 10);
        serverStatus.setBackground(Color.white);

        // Rank Panel
        JPanel rankPanel = new JPanel();
        rankPanel.setSize(220, 250);
        rankPanel.setLocation(190, 60);
        rankPanel.setBackground(BLUE_COLOR);
        rankPanel.setLayout(new GridLayout(5, 2,7,7));

        // player label

        Room roomUpdated = this.serverController.findRoom(this.room.getRoomName());
        ArrayList<Player> rankList = roomUpdated.getConnectedPlayers();

        rankList.sort(Player.sortByScore);
        int iRanking = 1;

        for (Player rankPlayer : rankList) {

            JLabel playerName = new JLabel(iRanking + ". " + rankPlayer.getUsername() + " " + rankPlayer.getScore());
            rankPanel.add(playerName);
            playerName.setBackground(DARK_BLUE_COLOR);
            playerName.setOpaque(true);
            playerName.setForeground(Color.WHITE);
            playerName.setFont(new Font("Lucida Console", Font.PLAIN, 12));
            playerName.setHorizontalAlignment(JLabel.CENTER);

            iRanking++;
        }

        // Leave button
        JButton leaveButton = new JButton("leave");
        leaveButton.addActionListener(this::leaveButtonListener);
        leaveButton.setSize(110, 30);
        leaveButton.setLocation(450, 310);

        // adding items to main panel
        this.add(titleLabel);
        this.add(serverStatus);
        this.add(leaveButton);
        this.add(rankPanel);

        this.repaint();
        this.revalidate();

    }

    /**
     * Méthode permettant de quitter l'écran de classement et de retourner à la liste des salons de jeu.
     * @param event L'event de réaction au clic du bouton permettant de retourner à la liste des salons de jeu.
     */

    private void leaveButtonListener(ActionEvent event) {

        this.serverController.leaveRoom(this.room.getRoomName(), this.player);

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        LobbyPanel lobbyPanel = new LobbyPanel(player, serverController);
        player.setScore(0);
        frame.update(lobbyPanel);
    }
}