import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
* Classe dont le rôle est la création de l'interface propre aux lobbys ( affichage des salons et du profil utilisteur ).
*/

public class LobbyPanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");
    private static final Color DARK_BLUE_COLOR = Color.decode("#123c5d");

    private ServerController serverController;
    private Player player;

    /**
     * Constructeur standard permettant de créer un objet de type ConnectPanel.
     *
     * @param player    Le joueur courant connecté.
     * @param serverController  Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     */

    public LobbyPanel(Player player, ServerController serverController) {

        this.serverController = serverController;
        this.player = player;

        HashMap<String, Room> rooms = this.serverController.getAllRooms();

        // Get user information
        String username = this.player.getUsername();
        int globalScore = player.getGlobalScore();

        // Main Panel
        this.setLayout( null );
        this.setBackground(BLUE_COLOR);

        // Anagram Title
        JLabel titleLabel = new JLabel("ANAGRAM");
        titleLabel.setSize(200, 30);
        titleLabel.setLocation(20, 10);
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 24));


        // Server Status Label
        JLabel serverStatus = new JLabel("Server Status");
        serverStatus.setSize(100, 30);
        serverStatus.setLocation(470, 10);
        buildLabel(serverStatus);

        // Room Panel
        JPanel roomPanel = new JPanel();
        roomPanel.setSize(350,200);
        roomPanel.setLocation(30,80);
        roomPanel.setBackground(BLUE_COLOR);
        roomPanel.setLayout(new GridLayout(0,2));

        // Room Button
        for (Map.Entry<String, Room> room : rooms.entrySet()) {
            JButton roomButton = new JButton(room.getKey());
            roomButton.addActionListener(this::connectRoomButtonListener);
            roomPanel.add(roomButton);
        }

        // Profile Panel
        JPanel profilePanel = new JPanel();
        profilePanel.setSize(150,100);
        profilePanel.setLocation(410,80);
        profilePanel.setBackground(DARK_BLUE_COLOR);
        profilePanel.setLayout(new FlowLayout(FlowLayout.LEFT));


        // Profile Label
        JLabel playerNameLabel = new JLabel(username);
        buildLabel(playerNameLabel);

        // Global Score Label
        JLabel globalScoreLabel = new JLabel("Score global: " + globalScore);
        buildLabel(globalScoreLabel);

        // Create Room Button
        JButton createRoomButton = buildButton("Create Room", this::createRoomButtonListener, 330, 310);

        // Disconnect Button
        JButton disconnectButton = buildButton("Disconnect", this::disconnectButtonListener, 450, 310);

        // Refresh Button
        JButton refreshButton = buildButton("Refresh", this::refreshButtonListener, 30, 290);

        // Adding items to ContentPane
        this.add(titleLabel);
        this.add(serverStatus);
        this.add(createRoomButton);
        this.add(disconnectButton);
        this.add(refreshButton);
        this.add(roomPanel);
        this.add(profilePanel);

        // Adding items to ProfilPane
        profilePanel.add(playerNameLabel);
        profilePanel.add(globalScoreLabel);
    }

    /**
     * Méthode qui crée et définit le style, les attributs des boutons de la classe.
     *
     * @param    string     label du bouton
     * @param    function   Méthode effectuée par le bouton
     * @param    x          Localisation verticale sur l'interface
     *
     */

    private JButton buildButton(String string, ActionListener function, int x, int y) {
        JButton button = new JButton(string);
        button.addActionListener(function);
        button.setSize(110, 30);
        button.setLocation(x, y);
        return button;
    }

    /**
     * Méthode qui crée et définit le style, les attributs des labels de la classe.
     *
     * @param    label       label ciblé
     *
     */

    private void buildLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        label.setBorder(BorderFactory.createEmptyBorder(5,10,5,0));
    }

    /**
     * Méthode de type ActionEvent qui permet de rejoindre un salon
     * Affiche un message d'erreur si le salon est déjà plein.
     * Lance la partie une fois le nombre maximum de joueur atteint.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void connectRoomButtonListener(ActionEvent event) {

        JButton button = (JButton) event.getSource();
        String roomName = button.getText();

        Room room = this.serverController.joinRoom(roomName, player);

        if(room == null) {
            JOptionPane.showMessageDialog(null, "Game full !", "Error", JOptionPane.ERROR_MESSAGE);
        } else {
            ClientFrame frame = ClientFrame.getClientFrame(serverController);
            RoomDetailsPanel roomDetails = new RoomDetailsPanel(player, serverController, room);
            frame.update(roomDetails);

            if(room.getConnectedPlayers().size() == room.getMaxPlayer())
                this.serverController.startGame(room);
        }
    }

    /**
     * Méthode de type ActionEvent qui appelle la fenêtre de création des salons de jeu privés.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void createRoomButtonListener(ActionEvent event) {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        CreateRoomPanel createRoomPanel = new CreateRoomPanel(player, serverController);
        frame.update(createRoomPanel);
    }

    /**
     * Méthode de type ActionEvent qui permet le reafraichissement des salons de jeu.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void refreshButtonListener(ActionEvent event) {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        LobbyPanel refreshedLobbyPanel = new LobbyPanel(player, serverController);
        frame.update(refreshedLobbyPanel);
    }

    /**
     * Méthode de type ActionEvent qui déconnecte le joueur, le renvoit à l'écran d'accueil et demande au serveur de mettre à jour ses données.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void disconnectButtonListener(ActionEvent event) {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        ConnectPanel connectPanel = new ConnectPanel(serverController);
        this.serverController.disconnect(this.player.getUsername(), this.serverController.getClient());
        this.serverController.updateUsersFile(player);
        frame.update(connectPanel);
    }
}

