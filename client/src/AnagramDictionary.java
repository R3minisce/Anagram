import java.io.*;
import java.util.HashMap;

/**
 * Classe représentant un dictionnaire d'anagrammes.
 */
public class AnagramDictionary implements Serializable {

    private HashMap<String, String[]> values;
    private String language;

    /**
     * Constructeur standard permettant de créer un objet de type AnagramDictionary.
     * @param language Permet de spécifier la langue du dictionnaire d'anagrammes.
     */

    public AnagramDictionary(String language) {
        this.setLanguage(language);
        this.values = new HashMap<String, String[]>();
    }

    // Getters

    public HashMap<String, String[]> getValues() {
        return values;
    }

    public String getLanguage() {
        return language;
    }

    // Setters

    public void setValues(HashMap<String, String[]> values) {
        this.values = values;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    // Methods

    /**
     * Méthode permettant d'initialiser les valeurs (values) d'un dictionnaire.
     * @param dictionaryFile Le fichier contenant les anagrammes.
     * @param numberOfItemsToLoad Le nombre de données à charger du fichier spécifié. Sera donc le nombre de mots dans le dictionnaire.
     */

    public void initValues(File dictionaryFile, int numberOfItemsToLoad) {
        BufferedReader reader = null;

        try {
            reader = new BufferedReader(new FileReader(dictionaryFile));
            String line;
            line = reader.readLine();

            int i = 1;
            while(line != null && i <= numberOfItemsToLoad) {
                String[] allWords = line.split(" ");
                String[] words = new String[allWords.length - 1];
                System.arraycopy(allWords, 1, words, 0, words.length);
                this.values.put(allWords[0], words);
                line = reader.readLine();
                i++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            if(reader != null) {
                try {
                    reader.close();
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     *
     * @return La langue du dictionnaire.
     */

    @Override
    public String toString() {
        return this.language;
    }
}