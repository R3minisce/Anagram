import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * Interface qui reprends les méthodes pouvant être exécutées par le client afin d'en avertir le serveur.
 * Nécessaire à l'utilisation du RMI bidirectionnel.
 *
 */

public interface IClient extends Remote {

    public void updatePanel(Room room) throws  RemoteException;
    public void createGamePanel(Room room, String word) throws RemoteException;
    public void createRankingPanel(Room room) throws RemoteException;
}