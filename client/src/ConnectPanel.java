import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.rmi.RemoteException;

/**
 * Classe représentant l'interface de l'utilisateur avant et pendant sa connexion.
 */

public class ConnectPanel extends JPanel {

    private static final Color BLUE_COLOR = Color.decode("#15476E");

    private JTextField pseudo;
    private ServerController serverController;

    // GUI Frame Method

    /**
     * Constructeur standard permettant de créer un objet de type ConnectPanel.
     * @param serverController  Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     */

    public ConnectPanel(ServerController serverController) {

        this.serverController = serverController;

        // Panel attributes
        this.setPreferredSize(new Dimension(600, 400));
        this.setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        this.setBackground(BLUE_COLOR);
        this.setBorder(BorderFactory.createEmptyBorder(25,25,25,25));

        // Server label
        JLabel serverStatus = new JLabel("Server Status", SwingConstants.CENTER);
        serverStatus.setFont(new Font("Lucida Console", Font.PLAIN, 12));
        buildLabel(serverStatus);

        // Title label
        JLabel anagram = new JLabel("ANAGRAM");
        anagram.setFont(new Font("Times New Roman", Font.PLAIN, 40));
        anagram.setBorder(BorderFactory.createEmptyBorder(75,0,10,0));
        buildLabel(anagram);

        // Pseudo field
        JPanel pseudoPanel = new JPanel();
        pseudoPanel.setBackground(BLUE_COLOR);
        pseudoPanel.setMaximumSize(new Dimension(200,30));

        pseudo = new JTextField();
        pseudo.setDocument(new InputLimit(25));
        pseudo.setAlignmentX(Component.CENTER_ALIGNMENT);
        pseudo.setPreferredSize(new Dimension(200,20));
        pseudo.addActionListener(this::connectButtonListener);
        pseudoPanel.add(pseudo);

        // ConnectStatus Label
        JLabel connectStatus = new JLabel("");
        connectStatus.setBorder(BorderFactory.createEmptyBorder(25,0,35,0));
        buildLabel(connectStatus);

        // Quit button
        JButton quit = new JButton("Quit");
        quit.addActionListener(this::quitButtonListener);
        quit.setAlignmentX(Component.CENTER_ALIGNMENT);
        quit.setMinimumSize(new Dimension(100,20));

        // Adding items to panel
        this.add(serverStatus);
        this.add(anagram);
        this.add(pseudoPanel);
        this.add(connectStatus);
        this.add(quit);

    }

    /**
     * Méthode permettant de définir le style d'un label.
     * @param label Le label dont le style doit être défini.
     */

    private void buildLabel(JLabel label) {
        label.setForeground(Color.WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
    }

    /**
     * Méthode permettant de gérer l'event de connexion du joueur. Elle permet notamment de :
     * - vérifier si un autre utilisateur utilise déjà le nom entré par l'utilisateur courant
     * - connecter l'utilisateur à son compte (si trouvé) ou
     * - créer un compte avec le nom entré
     * - déplacer l'utilisateur sur la page listant les lobbys.
     * @param event L'event de réaction au clic du bouton de connexion.
     */

    private void connectButtonListener(ActionEvent event) {
        String username = this.pseudo.getText();
        ClientFrame frame = ClientFrame.getClientFrame(this.serverController);

        Player player = null;
        if(username != null) {
            player = this.serverController.connect(username, this.serverController.getClient());
            if(player == null) {
                JOptionPane.showMessageDialog(null, "Player already connected", "Error !", JOptionPane.ERROR_MESSAGE);
            }
            else {
                player.setClient(this.serverController.getClient());
                LobbyPanel lobby = new LobbyPanel(player, serverController);
                frame.update(lobby);
            }
        }

    }

    /**
     * Méthode permettant de fermer l'application cliente.
     * @param event L'event de réaction au clic du bouton permettant de fermer l'application. (Quit)
     */

    private void quitButtonListener(ActionEvent event) {
        System.exit(0);
    }
}