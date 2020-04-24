/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;

import java.io.IOException;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Scanner;
import java.util.concurrent.ExecutionException;

/**
 * The class Admin. It allows Admin to manage the quiz from his perspective.
 * Multithread operation is being ran from this main method. Test of lock is ran from here too.
 */
public class Admin extends Profile{
    private static QuestionsDBGenerator generator = new QuestionsDBGenerator();

    /**
     * The entry point of application from the perspective of the Admin.
     *
     * @param args the input arguments
     * @throws ExecutionException   the execution exception
     * @throws InterruptedException the interrupted exception
     */
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        Scanner scan = new Scanner(System.in);
        int control = 0;
        while (control != 3){
            System.out.print("Witaj w widoku administratora! Co chcialbys zrobic?\n1. Uruchom test wielowatkowosci (uzywane do lab 3).\n2. Uruchom test blokady plikow (uzywane do lab 4).\n3. Wyjdz.\n");
            control = scan.nextInt();
            switch(control){
                case 1:{
                    generator.test(8,50000,5); //executing this may take some time
                    break;
                }
                case 2:{
                    //To try locking in this app you should run mains from this class and from User class simultaneously. It is not working fully proper.
                    //While the code below will be locking file "locktest", you should run writing to this text file from User class.
                    //It will wait for Admin's job completion and then do its job.
                    try (
                            FileChannel channel= FileChannel.open(Paths.get("locktest"), StandardOpenOption.WRITE);
                            FileLock lock = channel.lock()
                    ){
                        generator.generate(2, 100000, "locktest");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    break;
                }
            }
        }
        scan.close();
    }
}