import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

/**
* Classe dont le rôle est la création de l'interface propre à la création des salons de jeu privé.
*/

public class CreateRoomPanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");
    private static final Color DARK_BLUE_COLOR = Color.decode("#123c5d");

    private ServerController serverController;
    private Player player;
    private JComboBox<Object> roomDictCombo;
    private JList<Object> authPlayerList;
    private JTextField roomName;
    private JTextField roomMaxWord;

    /**
     * Constructeur standard permettant de créer un objet de type CreateRoomPanel.
     * @param serverController  Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     */

    public CreateRoomPanel(Player player, ServerController serverController) {

        this.serverController = serverController;
        this.player = player;

        ArrayList<AnagramDictionary> dictionaries = this.serverController.getDictionaries();

        // Main Panel
        this.setLayout( null );
        this.setBackground(BLUE_COLOR);

        // Anagram Title
        JLabel titleLabel = new JLabel("ANAGRAM");
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        titleLabel.setSize(200, 30);
        titleLabel.setLocation(20, 10);

        // Server Status Label
        JLabel serverStatus = new JLabel("Server Status");
        buildLabel(serverStatus);
        serverStatus.setLocation(470, 10);

        // Room Creation Panel left
        JPanel roomPanelLeft = buildPanel(250, 30, 0);

        // Room Name Label
        JLabel roomNameLabel = new JLabel("Enter room name :");
        buildLabel(roomNameLabel);

        // Room Dict Label
        JLabel dictLabel = new JLabel("Select dictionary :");
        buildLabel(dictLabel);

        // Authorized Player label
        JLabel authPlayerLabel = new JLabel("Select authorized players :");
        buildLabel(authPlayerLabel);

        // MaxWord Label
        JLabel maxWordLabel = new JLabel("Enter number of word in a game :");
        buildLabel(maxWordLabel);

        // Room Creation Panel right
        JPanel roomPanelRight = buildPanel(290, 270, 1);


        // RoomName Textfield
        this.roomName = new JTextField();
        JPanel roomNamePanel = buildTextField(roomName);

        // RoomDict Combobox
        this.roomDictCombo = new JComboBox<>(dictionaries.toArray());
        roomDictCombo.setSelectedItem(null);
        roomDictCombo.setMaximumRowCount(5);
        roomDictCombo.setAlignmentX(Component.CENTER_ALIGNMENT);
        roomDictCombo.setPreferredSize(new Dimension(200,20));

        // Authorized Players Jlist
        HashMap<String, Player> playersHashmap = this.serverController.getAllUsers();
        ArrayList<String> playerList = new ArrayList<>();
        for (Map.Entry<String, Player> iPlayer : playersHashmap.entrySet()) {
            playerList.add(iPlayer.getKey());
        }

        JPanel authPlayerPanel = new JPanel();
        authPlayerPanel.setSize(200,100);

        this.authPlayerList = new JList<>(playerList.toArray());
        authPlayerList.setVisibleRowCount(4);
        authPlayerList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        authPlayerList.setAlignmentX(Component.CENTER_ALIGNMENT);
        authPlayerList.setFixedCellHeight(20);
        authPlayerList.setFixedCellWidth(170);
        authPlayerPanel.add(authPlayerList);
        authPlayerPanel.add(new JScrollPane(authPlayerList));

        // RoomMaxWord Textfield
        this.roomMaxWord = new JTextField();
        JPanel roomMaxWordPanel = buildTextField(roomMaxWord);

        // Create Room Button
        JButton createRoomButton = buildButton("Create Room", this::createRoomButtonListener, 30);

        // Disconnect Button
        JButton disconnectButton = buildButton("Go back", this::returnButtonListener, 450);

        // Adding items to ContentPane
        this.add(titleLabel);
        this.add(serverStatus);
        this.add(createRoomButton);
        this.add(disconnectButton);
        this.add(roomPanelRight);
        this.add(roomPanelLeft);

        // Right Panel Items
        roomPanelRight.add(roomNamePanel);
        roomPanelRight.add(roomDictCombo);
        roomPanelRight.add(roomMaxWordPanel);
        roomPanelRight.add(authPlayerPanel);

        // Left Panel Items
        roomPanelLeft.add(roomNameLabel);
        roomPanelLeft.add(dictLabel);
        roomPanelLeft.add(maxWordLabel);
        roomPanelLeft.add(authPlayerLabel);

    }

    /**
     * Méthode qui crée et définit le style, les attributs des panneaux de la classe.
     *
     * @param    width       Taille du panneau
     * @param    x           Localisation verticale sur l'interface
     * @param    layout      Type d'alignement dans le panneau
     *
     */

    private JPanel buildPanel(int width, int x, int layout) {
        JPanel roomPanel = new JPanel();
        roomPanel.setSize(width, 210);
        roomPanel.setLocation(x, 80);
        roomPanel.setBackground(DARK_BLUE_COLOR);
        roomPanel.setLayout(new FlowLayout(layout));

        return roomPanel;
    }

     /**
     * Méthode qui crée et définit le style, les attributs des champs de texte de la classe.
     *
     * @param    textField       champs de texte ciblé
     *
     */

    private JPanel buildTextField(JTextField textField) {
        JPanel panel = new JPanel();
        panel.setBackground(DARK_BLUE_COLOR);
        panel.setMaximumSize(new Dimension(100,30));

        textField.setDocument(new InputLimit(25));
        textField.setAlignmentX(Component.CENTER_ALIGNMENT);
        textField.setPreferredSize(new Dimension(200,20));
        panel.add(textField);

        return panel;
    }

     /**
     * Méthode qui crée et définit le style, les attributs des boutons de la classe.
     *
     * @param    string     label du bouton
     * @param    function   Méthode effectuée par le bouton
     * @param    x          Localisation verticale sur l'interface
     *
     */

    private JButton buildButton(String string, ActionListener function, int x) {
        JButton button = new JButton(string);
        button.addActionListener(function);
        button.setSize(110, 30);
        button.setLocation(x, 310);
        return button;
    }

     /**
     * Méthode qui crée et définit le style, les attributs des labels de la classe.
     *
     * @param    label      label ciblé
     *
     */

    private void buildLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        label.setSize(100, 30);
        label.setBorder(BorderFactory.createEmptyBorder(7,5,7,5));
    }

     /**
     * Méthode de type ActionEvent qui, une fois les informations rentrées par l'utilisateur, crée le salon de jeu.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void createRoomButtonListener(ActionEvent event) {

        ArrayList<String> selectedPlayers = new ArrayList<>();
        for (Object selected : this.authPlayerList.getSelectedValuesList()) {
            selectedPlayers.add(selected.toString());
        }
        int roomMaxPlayerInt = selectedPlayers.size();

        int roomMaxWordInt;

        try {
            roomMaxWordInt =  Integer.parseInt(roomMaxWord.getText());
        } catch(NumberFormatException nfe) {
            roomMaxWord.setText("");
            roomMaxWordInt = 0;

        }

        if (roomMaxWordInt > 0 && !this.roomName.getText().equals("")) {
            Room newRoom = this.serverController.createPrivateRoom(this.roomName.getText(), roomMaxPlayerInt,
                    roomMaxWordInt, (AnagramDictionary) roomDictCombo.getSelectedItem(), selectedPlayers);
            returnButtonListener(event);
        } else {
            JOptionPane.showMessageDialog(null, "Invalid input", "Error !", JOptionPane.ERROR_MESSAGE);
        }
    }

     /**
     * Méthode de type ActionEvent qui permet le retour vers la page ( panneau ) précédente.
     *
     * @param    event       ActionEvent qui répond au clic sur le bouton
     *
     */

    private void returnButtonListener(ActionEvent event) {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        LobbyPanel lobby = new LobbyPanel(player, serverController);
        frame.update(lobby);
    }
}
