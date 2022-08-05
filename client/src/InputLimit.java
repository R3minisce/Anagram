import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * Classe qui permet d'appliquer une limite de caractères entrés par l'utilisateur sur les champs de textes ciblés
 *
 * Source : https://askcodez.com/la-limitation-du-nombre-de-caracteres-dans-un-component-swing-jtextfield.html
 */

public class InputLimit extends PlainDocument {
    private int limit;

    /**
     * Méthode qui permet d'appliquer la limite
     *
     * @param limit     nombre de caractères autorisés.
     */

    InputLimit(int limit) {
        super();
        this.limit = limit;
    }

    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null)
            return;

        if ((getLength() + str.length()) <= limit) {
            super.insertString(offset, str, attr);
        }
    }
}