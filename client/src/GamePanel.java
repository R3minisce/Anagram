import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.ArrayList;

/**
 * Classe représentant l'interface de l'utilisateur pendant une partie.
 */
public class GamePanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");
    private static final Color DARK_BLUE_COLOR = Color.decode("#123c5d");

    private static final Integer MAX_CHAR = 20;

    private JTextField answer;
    private ServerController serverController;
    private Room room;
    private Player player;
    private String word;
    private JLabel wordLabel;

    /**
     * Constructeur standard permettant de créer un objet de type GamePanel.
     * @param player Le joueur courant connecté.
     * @param room La room dans laquelle l'utilisateur courant est connecté.
     * @param serverController
     * @param word Le mot à afficher à l'utilisateur courant.
     */

    public GamePanel(Player player, Room room, ServerController serverController, String word) {

        this.room = room;
        this.serverController = serverController;
        this.player = player;
        this.word = word;

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

        // Game Panel
        JPanel gamePanel = new JPanel();
        gamePanel.setSize(400, 200);
        gamePanel.setLocation(100, 80);
        gamePanel.setBackground(BLUE_COLOR);
        gamePanel.setLayout(new BoxLayout(gamePanel, BoxLayout.Y_AXIS));

        // Given Word label
        this.wordLabel = new JLabel(word.toUpperCase());
        wordLabel.setForeground(Color.WHITE);
        wordLabel.setFont(new Font("Times New Roman", Font.PLAIN, 24));
        wordLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        wordLabel.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));

        // Answer word InputField
        answer = new JTextField(MAX_CHAR);
        answer.setAlignmentX(Component.CENTER_ALIGNMENT);
        answer.setMaximumSize( new Dimension(150,25) );
        answer.addActionListener(this::getGivenAnswer);

        // return button
        JButton returnButton = new JButton("Return");
        returnButton.addActionListener(this::returnButtonListener);
        returnButton.setSize(110, 30);
        returnButton.setLocation(450, 310);

        // adding items to main panel
        this.add(titleLabel);
        this.add(serverStatus);
        this.add(returnButton);
        this.add(gamePanel);

        // adding items to Game Panel
        gamePanel.add(wordLabel);
        gamePanel.add(answer);
    }

    /**
     * Méthode permettant de récupérer la proposition de solution de l'utilisateur et de la comparer aux réponses valides.
     * Elle permet également de mettre à jour l'utilisateur.
     * @param event L'event de réaction de l'envoi d'un mot au serveur.
     */
    private void getGivenAnswer(ActionEvent event) {

        String answerGiven = answer.getText();
        Player player2 = this.serverController.checkAnswer(room, word, answerGiven, this.player);
        ArrayList<Player> players = this.room.getConnectedPlayers();
        this.player = player2;
        int i = 0;
        while(i < players.size() && !players.get(i).getUsername().equals(this.player.getUsername())) {
            i++;
        }
        if(players.get(i).getUsername().equals(this.player.getUsername()))
            this.room.getConnectedPlayers().set(i, player2);
        answer.setText("");
    }

    /**
     * Méthode permettant de rafraichir l'interface de l'utilisateur pour lui afficher un nouveau mot.
     * @param word Le nouveau mot à afficher à l'utilisateur.
     */
    public void refresh(String word) {
        this.word = word;
        this.wordLabel.setText(word.toUpperCase());
        this.revalidate();
        this.repaint();
    }

    /**
     * Méthode permettant de créer l'interface de ranking.
     */
    public void rank() {

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        this.serverController.updatePlayer(this.player, this.room.getRoomName());
        RankingPanel rank = new RankingPanel(player, serverController, this.room);
        frame.update(rank);
    }

    /**
     * Méthode permettant de quitter la partie en cours. Elle renvoie à la liste des lobbys.
     * @param event L'event de récation au clic du bouton permettant de quitter la partie en cours.
     */
    private void returnButtonListener(ActionEvent event) {

        this.serverController.leaveRoom(this.room.getRoomName(), this.player);

        ClientFrame frame = ClientFrame.getClientFrame(serverController);
        LobbyPanel lobbyPanel = new LobbyPanel(player, serverController);
        frame.update(lobbyPanel);
    }
}