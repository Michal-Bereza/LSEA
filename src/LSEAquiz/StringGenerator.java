/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.*;

/**
 * The class StringGenerator. It implements the Callable interface and allows to generate random questions (without meaning,
 * but with correct syntax) and then write them to the file.
 */
public class StringGenerator implements Callable<String> {
    private String path; //path to the output file
    @Override
    public String call() {
        try  {
            File output = new File(this.path);
            output.createNewFile();
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(output.getAbsoluteFile(), true)); //creating buffer to write to file
            String question = genQuestion();
            synchronized (this){ //allows only one thread at the time to write to the file, to avoid collision
                bufferedWriter.write(genQuestion() + "\n"); //write generated question to file
            }
            bufferedWriter.close(); //closing buffer
            return "Udalo sie";
        }
        catch (IOException e){
            return "Nie udalo sie";
        }
    }

    /**
     * Instantiates a new StringGenerator.
     *
     * @param path the path of output file
     */
    public StringGenerator(String path) {
        this.path = path;
    }

    /**
     * Generates random question.
     * @return String of category, question and answers divided by semicolons
     */
    private String genQuestion(){
        int leftLimit = 97; // letter 'a'
        int rightLimit = 122; // letter 'z'
        int[] wordLengths = new int[5]; //array of words' lengths
        String[] words = new String[5]; //array of words which will make the question
        Category cat = Category.values()[ThreadLocalRandom.current().nextInt(Category.values().length)]; //Getting random category from enum
        for (int i = 0; i < 5; i++) {
            wordLengths[i] = ThreadLocalRandom.current().nextInt(10, 24); //Randomizing lengths of question and answers
            words[i] = "";
            for (int j = 0; j < wordLengths[i]; j++)
                words[i] += (char)ThreadLocalRandom.current().nextInt(leftLimit, rightLimit); //Adding next letters to complete words
        }
        return cat + ";" + words[0] + "?;" + words[1] + ";" + words[2] + ";" + words[3] + ";" + words[4]; //Returning finished string of category, question and answers
    }
}
