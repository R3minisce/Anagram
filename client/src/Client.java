import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

/**
 * Classe permettant de représenter un client.
 */

public class Client extends UnicastRemoteObject implements IClient, Runnable {

    private static final int SRV_PORT = 4321;

    private String SRV_IP;
    private ClientFrame frame;

    /**
     * Constructeur standard permettant de créer un objet de type Client.
     *
     */

    public Client() throws RemoteException {
        this.setSRV_IP("127.0.0.1");
    }

    /**
     * Mets à jour le panneau propre aux salons de jeu ( rooms ) en y affichant les nouveaux joueurs qui s'y connectent.
     *
     * @param	room	salon de jeu devant être mis à jour.
     */

    public void updatePanel(Room room) {

        RoomDetailsPanel panel = (RoomDetailsPanel) this.frame.getContentPane().getComponent(0);
        panel.updatePlayerList(room);
    }

    /**
     * Crée et lance une partie en y envoyant le premier anagramme.
     * Si la partie est déjà en cours, rafraichis uniquement l' anagramme en cours.
     *
     * @param    room        salon de jeu devant être mis à jour.
     * @param    word        anagramme reçu par le serveur.
     */

    public void createGamePanel(Room room, String word) {

        if((this.frame.getContentPane().getComponent(0)).getClass() == RoomDetailsPanel.class) {
            RoomDetailsPanel panel = (RoomDetailsPanel) this.frame.getContentPane().getComponent(0);
            panel.connectRoomButtonListener(room, word);
        } else {
            GamePanel panel = (GamePanel) this.frame.getContentPane().getComponent(0);
            panel.refresh(word);
        }
    }

    /**
     * Mets à jour l'interface en faisant appel au panneau de classement.
     * Effectué en fin de partie.
     *
     * @param    room        salon de jeu devant être mis à jour.
     */

    public void createRankingPanel(Room room) {

        GamePanel game = (GamePanel) this.frame.getContentPane().getComponent(0);
        game.rank();
    }

    public String getSRV_IP() {
        return SRV_IP;
    }

    public void setSRV_IP(String SRV_IP) {
        this.SRV_IP = SRV_IP;
    }

    public static void main(String[] args) {
        try {
            Client client = new Client();
            if(args.length > 0)
                client.setSRV_IP(args[0]);
            new Thread(client).start();
        }
        catch(Exception ignored) {
            //
        }
    }

    /**
     * Initialise la connexion avec le serveur et apelle la classe qui intègre l'interface.
     *
     */

    public void run() {
        final String serv = SRV_IP;

        ServerController serverController;
        Registry registry;

        try {
            registry = LocateRegistry.getRegistry(serv, SRV_PORT);
            IServer server = (IServer) registry.lookup("server");
            serverController = new ServerController(server, this);
            this.frame = ClientFrame.getClientFrame(serverController);
            this.frame.setVisible(true);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
