/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.channels.OverlappingFileLockException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Collections;
import java.util.Vector;

/**
 * The class QuestionsDB (Questions Database). It stores the set of questions possible to be asked during quiz.
 */
public class QuestionsDB implements Cloneable{


    private String questionsPath; //ścieżka do pliku zawierającego bazę pytań
    private Vector<Question> questionVector; //wektor zawierający pytania wraz z odpowiedziami
    private Vector<Category> categoryVector; //wektor zawierający kategorie pytań

    /**
     * Sets questions path.
     *
     * @param questionsPath the questions path
     */
    public void setQuestionsPath(String questionsPath) {
        this.questionsPath = questionsPath;
    }

    /**
     * Gets questions path.
     *
     * @return the questions path
     */
    public String getQuestionsPath() {
        return questionsPath;
    }

    /**
     * Sets question vector.
     *
     * @param questionVector the existing question vector
     */
    public void setQuestionVector(Vector<Question> questionVector) {
        this.questionVector = questionVector;
    }

    /**
     * Gets the full question vector.
     *
     * @return the vector of questions
     */
    public Vector<Question> getQuestionVector() {
        return questionVector;
    }

    /**
     * Gets question vector, but only consisting of questions from selected category.
     *
     * @param category the category of the question
     * @return the vector of questions from chosen category
     */
    public Vector<Question> getQuestionVector(Category category) { //zwraca pytania tylko z wybranej kategorii
        Vector<Question> tempVec = new Vector<Question>(); //utworzenie tymczasowego wektora
        for (int i = 0; i < this.questionVector.size(); i++) {
            if (this.questionVector.get(i).getCategory() == category){
                tempVec.add(this.questionVector.get(i));
            }
        }
        return tempVec;
    }

    /**
     * Gets vector of categories available in this set of questions.
     *
     * @return the category vector
     */
    public Vector<Category> getCategoryVector() {
        return categoryVector;
    }


    /**
     * Sets category vector.
     *
     * @param categoryVector the existing category vector
     */
    public void setCategoryVector(Vector<Category> categoryVector) {
        this.categoryVector = categoryVector;
    }

    /**
     * Instantiates a new QuestionsDB consisting of questions stored in file.
     *
     * @param questionsPath the path to text file with questions
     * @param type          type of file (true - text, false - binary)
     */
    public QuestionsDB(String questionsPath, boolean type) {
        this.questionsPath = questionsPath;
        if (type) //reading text file
            this.questionVector = ReadTextFile();
        else //reading binary file
            this.questionVector = ReadBinaryFile();
        this.categoryVector = ReadCategories();
    }

    /**
     * Instantiates a new QuestionsDB consisting of default set of questions.
     */
    public QuestionsDB(){
        this("questions", true);
    }

    /**
     * Method reading set of available categories from the questions vector to the vector
     * @return Vector of categories
     */
    private Vector<Category> ReadCategories(){
        Vector<Category> tempVec = new Vector<>();
        for (Question i: this.questionVector) { //iteracja po kolejnych pytaniach, jeśli dana kategoria jeszcze nie znajduje się w wektorze, zostanie dodana
            if (!tempVec.contains(i.getCategory()))
                tempVec.add(i.getCategory());
        }
        return tempVec; //zwrócenie tymczasowego wektora
    }

    /**
     * Prints sorted questions with categories stored in database. They are sorted by category and the content.
     */
    public void printQuestions(){
        Collections.sort(this.questionVector); //it won't be possible without implementing Comparabale interface to Question
        for (Question q: this.questionVector) {
            System.out.print(q.getCategory() + ": " + q.getContent() + "\n");
        }
    }


    /**
     * Implementation of clone() method to fulfill the requirements of Cloneable interface and to provide option of deep cloning.
     * @return Deep copy of questions database
     */
    @Override
    public QuestionsDB clone() throws CloneNotSupportedException {

        QuestionsDB copy = new QuestionsDB();
        copy.setQuestionsPath(this.questionsPath);
        copy.setQuestionVector(new Vector<Question>());
        for (Question q: this.questionVector) {
            copy.getQuestionVector().add((Question)(q.clone()));
        }
        copy.setCategoryVector(new Vector<Category>());
        for (Category c: this.categoryVector) {
            copy.getCategoryVector().add(c);
        }
        System.out.println("Skopiowano!\n");
        return copy;
    }

    /**
     * Write text file.
     *
     * @param path the path of the output
     */
    public void WriteTextFile(String path){
        File output = new File(path);
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output.getAbsoluteFile(), false));
             FileChannel channel= FileChannel.open(Paths.get(path), StandardOpenOption.WRITE);
             FileLock lock = channel.lock()
        ){
            for (Question q: questionVector) {
                bufferedWriter.write(q.toString() + "\n");
            }
        } catch(OverlappingFileLockException e){
            System.out.print("Ktos inny korzysta obecnie z tego pliku!"); //catching lock exception
        } catch (IOException e){
            System.out.print("ERROR!");
        }
    }

    /**
     * Method reading set of questions from text file to vector
     * @return vector of questions
     */
    private Vector<Question> ReadTextFile() {
        Vector<Question> tempVec = new Vector<Question>(); //utworzenie tymczasowego wektora

        try (BufferedReader br = Files.newBufferedReader(Paths.get(this.questionsPath))) //próba utworzenia bufora iterującego po pliku z pytaniami
        {
            String line; //łańcuch znaków iterujący po kolejnych liniach pliku
            while ((line = br.readLine()) != null) { //iteracja do końca pliku
                String[] parts = line.split(";"); //rozdział linii na fragmenty (kategoria, treść, odpowiedzi)
                tempVec.add(new Question(parts[0],parts[1],parts[2],parts[3],parts[4],parts[5])); //dodanie pytania do wektora
            }
            return tempVec; //zwrócenie tymczasowego wektora
        } catch (IOException e) {
            System.out.print("Taki plik nie istnieje! Anulowano.\n");
            return null;
        }

    }

    /**
     * Method reading set of questions from binary file to vector
     * @return vector of questions
     */
    private Vector<Question> ReadBinaryFile() {
        Vector<Question> tempVec = new Vector<Question>(); //utworzenie tymczasowego wektora

        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(this.questionsPath))) //próba utworzenia bufora iterującego po pliku z pytaniami
        {
            while(true){
                tempVec.add((Question)in.readObject()); //Deserialization
            }
        } catch(EOFException e) { //we want to catch this, it means that we have read everything
            return tempVec;
        } catch (IOException e) {
            System.out.print("Taki plik nie istnieje! Anulowano.\n");
            return null;
        } catch (ClassNotFoundException e) {
            System.out.print("Blad! Sprobuj z innym plikiem.\n");
            return null;
        }

    }

    /**
     * Write binary file.
     *
     * @param path the path of the output
     */
    public void WriteBinaryFile(String path){
        File output = new File(path);
        try (
             ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(path))
        ){
            for (Question q: questionVector) {
                out.writeObject(q); //Serialization
            }
        } catch (IOException e) {
            System.out.print("ERROR!");
        }
    }
}