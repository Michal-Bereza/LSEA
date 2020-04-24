/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.io.Serializable;
import java.util.Random;
import java.util.Vector;


/**
 * The class Question. It defines the content of each question available in the quiz.
 */
public class Question implements Comparable<Question>, Cloneable, Serializable {
    //atrybuty tworzące każde pytanie, odpowiednio:
    private Category category; //the category of the question
    private String content; //the content of the question
    private String correct; //the correct answer
    private String incorrect1; //the first incorrect answer
    private String incorrect2; //the second incorrect answer
    private String incorrect3; //the third incorrect answer

    /**
     * Gets category.
     *
     * @return the category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Gets content.
     *
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * Gets correct answer.
     *
     * @return the correct
     */
    public String getCorrect() {
        return correct;
    }

    /**
     * Gets first incorrect answer.
     *
     * @return the incorrect 1
     */
    public String getIncorrect1() {
        return incorrect1;
    }

    /**
     * Gets second incorrect answer.
     *
     * @return the incorrect 2
     */
    public String getIncorrect2() {
        return incorrect2;
    }

    /**
     * Gets third incorrect answer.
     *
     * @return the incorrect 3
     */
    public String getIncorrect3() {
        return incorrect3;
    }

    /**
     * Instantiates a new Question.
     *
     * @param category   the category of the question
     * @param content    the content of the question
     * @param correct    the correct answer
     * @param incorrect1 the first incorrect answer
     * @param incorrect2 the second incorrect answer
     * @param incorrect3 the third incorrect answer
     */
    public Question(String category, String content, String correct, String incorrect1, String incorrect2, String incorrect3) {
        this.category = Category.valueOf(category.toUpperCase());
        this.content = content;
        this.correct = correct;
        this.incorrect1 = incorrect1;
        this.incorrect2 = incorrect2;
        this.incorrect3 = incorrect3;
    }

    /**
     * Print the question and answers in random order.
     *
     * @return the index of correct answer (integer), range 1-4
     */
    public Integer PrintQuestion(){
        Random generator = new Random();
        Vector<String> tempVec = new Vector<String>(); //wektor odpowiedzi
        tempVec.add(this.incorrect1);
        tempVec.add(this.incorrect2);
        tempVec.add(this.incorrect3);
        int m = generator.nextInt(4); //losowanie, która w kolejnosci odpowiedz bedzie prawidlowa
        tempVec.add(m,this.correct); //dodanie prawidlowej odpowiedzi na odpowiednim miejscu
        System.out.print("Kategoria - " + this.category + ": " + this.content + "\n"); //wypisanie pytania
        for (int i = 0; i < 4; i++){ //wypisanie kolejnych odpowiedzi
            System.out.print((i+1) + ". " + tempVec.get(i) + ".\n");
        }
        return m+1; //zostaje zwrócony identyfikator prawidlowej odpowiedzi powiekszony o 1, aby pasowal do numerow odpowiedzi
    }

    /**
     * Implementation of compareTo() method from Comparable interface.
     * @param o question to which this question will be compared
     * @return -1, 0 or 1, for being smaller, equal or greater than o question.
     */
    @Override
    public int compareTo(Question o) { //implementing Comparable interface
        int compCat = this.category.name().compareTo(o.getCategory().name());
        int compCont = this.content.compareTo(o.getContent());
        if (compCat < 0)
            return -1;
        else if (compCat > 0)
            return 1;
        else
            return Integer.compare(compCont, 0);
    }

    /**
     * Implementation of clone() method to fulfill the requirements of Cloneable interface and to provide option of deep cloning.
     * @return deep copy of the question
     * @throws CloneNotSupportedException
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
        return new Question(this.category.name(), this.content, this.correct, this.incorrect1, this.incorrect2, this.incorrect3);
    }

    @Override
    public String toString(){
        return this.category + ";" + this.content + ";" + this.correct + ";" + this.incorrect1 + ";" + this.incorrect2 + ";" + this.incorrect3;
    }
}
