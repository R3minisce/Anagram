import java.io.*;
import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;

/**
 * Classe qui permet la création d'un objet de type Server.
 *
 */

public class Server extends UnicastRemoteObject implements IServer {

    private static final int SRV_PORT = 4321;

    private HashMap<String, Player> allUsers;
    private ArrayList<IClient> connectedClients;
    private ArrayList<String> connectedPlayers;
    private HashMap<String, Room> allRooms;
    private Player player;
    private HashMap<String, Thread> threads;
    private ArrayList<AnagramDictionary> dictionaries;

    /**
     * Constructeur standard qui permet l'instanciation d'un object de type Server.
     *
     */

    public Server() throws RemoteException {
        super();
        this.setDictionaries(new ArrayList<AnagramDictionary>());
        this.initDictionaries();

        //this.initUsers(new File("server/resources/players.txt"));

        this.allUsers = new HashMap<>();
        this.setAllUsers(new File("server/resources/players.txt"));

        //this.initRooms(new File("rooms.txt"));

        this.allRooms = new HashMap<>();
        this.setAllRooms(new File("server/resources/rooms.txt"));

        this.setConnectedClients(new ArrayList<IClient>());
        this.setConnectedPlayers(new ArrayList<String>());

        this.setThreads(new HashMap<String, Thread>());
    }

    // All the methods the client can execute

    /**
     * Méthode permettant la connexion d'un utilisateur déjà existant et son association à un client
     * Si le joueur n'existe pas, il est créé.
     *
     * @param    username        nom de l'utilisateur
     * @param    client          client ciblé lors de l'affection de l'utilisateur
     *
     */

    public synchronized Player connect(String username, IClient client) {
        if(allUsers.containsKey(username)) {
            if(!connectedPlayers.contains(username)) {
                this.connectedClients.add(client);
                this.connectedPlayers.add(username);
                return allUsers.get(username);
            }
        } else {
            Player player = new Player(username, 0);
            this.allUsers.put(username, player);
            this.connectedClients.add(client);
            this.connectedPlayers.add(username);
            return player;
        }
        return null;
    }

    /**
     * Méthode permettant la déconnexion d'un utilisateur et la destruction de son client.
     *
     * @param    username        nom de l'utilisateur
     * @param    client          client lié à l'utilisateur
     *
     */

    public void disconnect(String username, IClient client) {
        this.connectedPlayers.remove(username);
        this.connectedClients.remove(client);
    }

    public HashMap<String, Room> getAllRooms() {
        return allRooms;
    }

    public HashMap<String, Player> getAllUsers() {
        return allUsers;
    }

    /**
     * Méthode qui permet à un joueur de rejoindre un salon de jeu.
     * Vérifie dans le cas d'un salon privé si l'utilisateur est bien dans la liste des joueurs autorisés.
     * Dans le cas contraire, refuse l'accès au salon de jeu.
     *
     * @param        roomName        nom du salon de jeu à rejoindre
     * @param        player          joueur ciblé
     *
     */

    public Room joinRoom(String roomName, Player player) {
        Room room = this.allRooms.get(roomName);

        if(room.getConnectedPlayers().size() == room.getMaxPlayer()) {
            return null;
        }

        if(room.getWhitelistedPlayers().size() != 0) {
            int i = 0;
            boolean found = false;
            while(i < room.getWhitelistedPlayers().size() && !found) {
                found = room.getWhitelistedPlayers().get(i).equals(player.getUsername());
                i++;
            }
            if(!found)
                return null;
        }

        room.addUsers(player);
        if(room.getConnectedPlayers().size() > 1) {
            for (Player pl : room.getConnectedPlayers()) {
                if(pl.getClient() != player.getClient()) {
                    try {
                        pl.getClient().updatePanel(room);
                    } catch (RemoteException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return room;
    }

    /**
     * Méthode qui permet à un joueur de quitter un salon de jeu.
     *
     * @param        roomName        nom du salon de jeu à rejoindre
     * @param        player          joueur ciblé
     *
     */

    public void leaveRoom(String roomName, Player player) {
        Room room = this.allRooms.get(roomName);
        room.removeUsers(player);

        if(room.getConnectedPlayers().size() != 0) {
            for (Player pl : room.getConnectedPlayers()) {
                try {
                    pl.getClient().updatePanel(room);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode qui lance une partie, une fois son thread démarré et la liste des anagrammes chargées depuis le dictionnaire du salon.
     *
     * @param    room        salon de jeu ciblé.
     *
     */

    public synchronized void startGame(Room room) {
        ArrayList<String> words = this.chooseWord(room);
        GameThread gameThread =  new GameThread(room, words);
        Thread game = new Thread(gameThread);
        this.threads.put(room.getRoomName(), game);
        game.start();
    }

    /**
     * Méthode qui vérifie la correspondance entre le mot reçu par le joueur et la réponse correcte se trouvant dans le dictionnaire du salon.
     * En cas de réponse correcte, le joueur se voit octroyer un point.
     *
     * @param    room        salon de jeu ciblé
     * @param    word        anagramme envoyé par le serveur
     * @param    answer      réponse à l'anagramme envoyé par l'utilisateur
     * @param    player      joueur ciblé
     *
     */

    public Player checkAnswer(Room room, String word, String answer, Player player) {

        if (room.getDictionary().getValues().containsKey(word)) {
            ArrayList<String> correctAnswers = new ArrayList<String>(Arrays.asList(room.getDictionary().getValues().get(word)));
            if (correctAnswers.contains(answer)) {
                player.setScore(player.getScore() + 1);
                player.setGlobalScore(player.getGlobalScore() + 1);
                this.player = player;

                Thread thread = searchThread(room);

                try {
                    thread.interrupt();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                return player;
            }
        }
        return player;
    }

    /**
     * Méthode permettant de créer une room privée et de l'ajouter à la liste des rooms du serveur.
     * @param roomName Nom de la room à créer.
     * @param maxPlayer Nombre de joueurs maximum dans la nouvelle room.
     * @param maxWord Nombre de mots maximum. Ceci correspond donc au nombre de manches.
     * @param dictionary Le dictionnaire d'anagrammes à utiliser pour générer les anagrammes.
     * @param selectedPlayers Nom des différents joueurs whitelisted (qui peuvent rejoindre la room).
     * @return La room nouvellement créée.
     */

    public Room createPrivateRoom(String roomName, int maxPlayer, int maxWord, AnagramDictionary dictionary, ArrayList<String> selectedPlayers) {
        if(this.getAllRooms().size() < 5) {
            Room room = new Room(roomName, maxPlayer, maxWord, dictionary);
            room.setWhitelistedPlayers(selectedPlayers);
            this.getAllRooms().put(room.getRoomName(), room);
            return room;
        }
        return null;
    }

    public ArrayList<AnagramDictionary> getDictionaries() {
        return dictionaries;
    }

    /**
     * Méthode permettant de trouver une room en fonction de son nom.
     * @param name Le nom de la room à trouver.
     * @return La room trouvée ou null si elle n'a pas été trouvée.
     */

    public Room findRoom(String name) {
        for (Map.Entry<String, Room> room : this.allRooms.entrySet()) {
            if(room.getKey().equals(name))
                return room.getValue();
        }
        return  null;
    }

    /**
     * Méthode permettant de mettre à jour les données d'un joueur dans les différentes structures du serveur.
     * @param player Le joueur dont il faut mettre à jour les informations.
     * @param roomName Le nom de la room (permet de mettre à jour les données du serveur relatives aux rooms).
     */

    public void updatePlayer(Player player, String roomName) {
        this.getAllUsers().replace(player.getUsername(), player);
        Room room = this.allRooms.get(roomName);

        int i = 0;
        while(i < room.getConnectedPlayers().size() && !room.getConnectedPlayers().get(i).getUsername().equals(player.getUsername())) {
            i++;
        }
        room.getConnectedPlayers().set(i, player);
    }

    /**
     * Méthode permettant de mettre à jour le fichier des joueurs.
     * @param player Le joueur à mettre à jour dans le fichier.
     */

    public void updateUsersFile(Player player) {
        ObjectOutputStream writer = null;
        try {
            writer = new ObjectOutputStream(new FileOutputStream("players.txt"));
            writer.writeObject(new Player(player.getUsername(), player.getGlobalScore()));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Setters

    public void setConnectedClients(ArrayList<IClient> connectedClients) {
        this.connectedClients = connectedClients;
    }

    public void setConnectedPlayers(ArrayList<String> connectedPlayers) {
        this.connectedPlayers = connectedPlayers;
    }

    public void setDictionaries(ArrayList<AnagramDictionary> dictionaries) {
        this.dictionaries = dictionaries;
    }

    public void setThreads(HashMap<String, Thread> threads) {
        this.threads = threads;
    }

    // Methods

    /**
     * Méthode qui sélectionne un nombre défini d'anagramme dans le dictionnaire lié au salon de jeu.
     *
     * @param     room     salon de jeu ciblé
     *
     */

    public synchronized ArrayList<String> chooseWord(Room room) {
        ArrayList<String> words = new ArrayList<>();
        AnagramDictionary dictionary = room.getDictionary();
        ArrayList<String> anagrams = new ArrayList<String>(dictionary.getValues().keySet());
        int dictSize = dictionary.getValues().size();

        int i = 0;
        while(i < room.getMaxWord()) {
            int ran = (int) (Math.random() * dictSize);
            String chosenWord = anagrams.get(ran);
            words.add(chosenWord);
            i++;
        }
        return words;
    }

    /**
     * Méthode permettant de chercher un thread dans la liste des Threads du serveur en fonction de la room.
     * @param room La room associée au Thread recherché.
     * @return Le Thread cherché.
     */

    public Thread searchThread(Room room) {
        if(this.threads.containsKey(room.getRoomName()))
            return this.threads.get(room.getRoomName());
        return null;
    }

    // Init methods

    /**
     * Méthode permettant de récupérer la liste de tous les salons connus du serveur et sauvegardés dans un fichier.
     * @param roomsFile Le fichier dans lequel les salons sont stockés.
     */

    private void setAllRooms(File roomsFile) {
        ObjectInputStream reader = null;
        try {
            reader = new ObjectInputStream(new FileInputStream(roomsFile));
            Room room;
            room = (Room) reader.readObject();
            while(room != null) {
                room.setDictionary(this.dictionaries.get(0));
                this.allRooms.put(room.getRoomName(), room);
                room = (Room) reader.readObject();
            }
        }
        catch(EOFException e) {
            //expected exception
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode permettant d'initialiser le fichier des joueurs du serveur.
     * @param allUsers Le fichier des joueurs dans lequel écrire.
     */

    private void initUsers(File allUsers) {
        ObjectOutputStream writer = null;
        try {
            writer = new ObjectOutputStream(new FileOutputStream(allUsers));
            writer.writeObject(new Player("Azhorr", 0));
            writer.writeObject(new Player("Romoule", 0));
        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode permettant d'initialiser le fichier des salons du serveur.
     * @param allRooms  Le fichier des salons dans lequel écrire.
     */

    private void initRooms(File allRooms) {
        ObjectOutputStream writer = null;
        try {
            writer = new ObjectOutputStream(new FileOutputStream(allRooms));
            writer.writeObject(new Room("room 1", 1, 5, this.dictionaries.get(0)));
            writer.writeObject(new Room("room 2", 2, 5, this.dictionaries.get(0)));
            writer.writeObject(new Room("room 3", 1, 5, this.dictionaries.get(0)));
            writer.writeObject(new Room("room 4", 2, 5, this.dictionaries.get(0)));

        }
        catch(IOException e) {
            e.printStackTrace();
        }
        finally {
            if(writer != null) {
                try {
                    writer.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Méthode permettant d'initialiser les dictionnaires du serveur.
     */

    public void initDictionaries() {
        AnagramDictionary dictionary1 = new AnagramDictionary("FR");
        dictionary1.initValues(new File("server/resources/anagrams"), 50);
        this.dictionaries.add(dictionary1);
    }

    /**
     * Méthode permettant de récupérer la liste de tous les joueurs connus du serveur et sauvegardés dans un fichier.
     * @param usersFile Le fichier dans lequel les utilisateurs sont stockés.
     */

    private void setAllUsers(File usersFile) {
        ObjectInputStream reader = null;
        try {
            reader = new ObjectInputStream(new FileInputStream(usersFile));
            Player player;
            player = (Player) reader.readObject();
            while(player != null) {
                this.allUsers.put(player.getUsername(), player);
                player = (Player) reader.readObject();
            }
        }
        catch(EOFException e) {
            //expected exception
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch(IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // Launch the server
    public static void main(String[] args) {
        Registry registry;

        try {
            registry = LocateRegistry.createRegistry(SRV_PORT);
            registry.rebind("server", new Server());
        }
        catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}