import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *  Interface qui reprend les méthodes pouvant être exécutées par le serveur afin d'en avertir le client.
 *  Nécessaire à l'utilisation du RMI bidirectionnel.
 */

public interface IServer extends Remote {

    public Player connect(String username, IClient client) throws RemoteException;
    public void disconnect(String username, IClient client) throws  RemoteException;
    public HashMap<String, Room> getAllRooms() throws  RemoteException;
    public HashMap<String, Player> getAllUsers() throws  RemoteException;
    public Room joinRoom(String roomName, Player player) throws RemoteException;
    public void leaveRoom(String roomName, Player player) throws RemoteException;
    public void startGame(Room room) throws RemoteException;
    public Player checkAnswer(Room room, String word, String answer, Player player) throws RemoteException;
    public Room createPrivateRoom(String roomName, int maxPlayer, int maxWord, AnagramDictionary dictionary, ArrayList<String> selectedPlayers) throws  RemoteException;
    public ArrayList<AnagramDictionary> getDictionaries()throws RemoteException;
    public Room findRoom(String name) throws RemoteException;
    public void updatePlayer(Player player, String roomName) throws RemoteException;
    public void updateUsersFile(Player player)throws RemoteException;
}