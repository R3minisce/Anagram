import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Classe permettant de séparer la couche "vue" et la couche "serveur". Elle permet la communication entre le serveur et ses différents clients.
 */

public class ServerController {

    private final IServer server;
    private Client client;

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public ServerController(IServer server, Client client) {
        this.server = server;
        this.client = client;
    }

    public synchronized Player connect(String username, IClient client) {
        try {
            return this.server.connect(username, client);
        }
        catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void disconnect(String username, IClient client) {
        try {
            this.server.disconnect(username, client);
        }
        catch(RemoteException e) {
            e.printStackTrace();
        }
    }

    public Room joinRoom(String roomName, Player player) {
        try {
            return this.server.joinRoom(roomName, player);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void leaveRoom(String roomName, Player player) {
        try {
            this.server.leaveRoom(roomName, player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void startGame(Room room) {
        try {
            this.server.startGame(room);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public HashMap<String, Room> getAllRooms() {
        try {
            return this.server.getAllRooms();
        }
        catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }

    }

    public HashMap<String, Player> getAllUsers() {
        try {
            return this.server.getAllUsers();
        }
        catch(RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Player checkAnswer(Room room, String word, String answer, Player player) {
        try {
            return this.server.checkAnswer(room, word, answer, player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return player;
    }

    public Room createPrivateRoom(String roomName, int maxPlayer, int maxWord, AnagramDictionary dictionary, ArrayList<String> selectedPlayers) {
        try {
            return this.server.createPrivateRoom(roomName, maxPlayer, maxWord, dictionary, selectedPlayers);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public ArrayList<AnagramDictionary> getDictionaries() {
        try {
            return this.server.getDictionaries();
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public Room findRoom(String name) {
        try {
            return this.server.findRoom(name);
        } catch (RemoteException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void updatePlayer(Player player, String roomName) {
        try {
            this.server.updatePlayer(player, roomName);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public void updateUsersFile(Player player) {
        try {
            this.server.updateUsersFile(player);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}