/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.util.Comparator;

/**
 * The Comparator of two Question Databases. It compares their quantities of questions stored.
 */
public class DBComparator implements Comparator<QuestionsDB> {

    @Override
    public int compare(QuestionsDB t1, QuestionsDB t2) {
        return Integer.compare(t2.getQuestionVector().size(), t1.getQuestionVector().size()); //Indices are swapped to provide reversed (descending) order.
    }
}
