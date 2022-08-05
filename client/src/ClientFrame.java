import javax.swing.*;
import java.awt.*;

/**
 * Classe qui permet de créer le fenêtre de l'interface utilisateur. C'est grâce à elle que les différentes données peuvent être affichées à l'utilisateur.
 * Est une implémentation du design-pattern Singleton.
 */

public class ClientFrame extends JFrame {

    private static ClientFrame clientFrame;

    /**
     * Constructeur standard permettant de créer un objet de type ClientFrame.
     * @param serverController Permet de lier la fenêtre créée à un ServerController. C'est lui qui effectuera les différentes interactions avec le serveur.
     */

    private ClientFrame(ServerController serverController) {

        this.setDefaultCloseOperation(EXIT_ON_CLOSE);
        this.setSize(600, 400);
        this.setLocationRelativeTo(null);
        this.setResizable(false);
        this.setTitle("Anagram Multiplayer Game");

        ConnectPanel panel = new ConnectPanel(serverController);
        this.update(panel);
    }

    /**
     * Méthode permettant de rafraichir le contenu de la fenêtre de l'utilisateur.
     * @param panel Le nouveau contenu (panneau) à afficher à l'utilisateur.
     */

    public void update(JPanel panel) {
        Container container = this.getContentPane();
        container.removeAll();
        container.add(panel);
        this.revalidate();
    }

    /**
     * Méthode permettant de récupérer l'unique référence ClientFrame.
     * @param serverController Paramètre nécéssaire pour créer l'objet s'il n'est pas encore créé.
     * @return L'unique instance de ClientFrame possible.
     */

    public static ClientFrame getClientFrame(ServerController serverController) {
        if(clientFrame == null) {
            clientFrame = new ClientFrame(serverController);
        }
        return clientFrame;
    }
}