/**
 * @author Michał Bereza
 * @version 0.3
 * @since 09.04.2020
 */

package LSEAquiz;
import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.*;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Vector;

/**
 * The class User. It stores information about current player
 */
public class User extends Profile {
    private static Integer maxPoints; //liczba punktów, które użytkownik mógł zdobyć
    private static QuestionsDB questionBase;//baza pytań
    private static Vector<QuestionsDB> questionsDBVector; //vector storing loaded sets of questions

    /**
     * The entry point of application from the perspective of the player.
     *
     * @param args the input arguments
     * @throws CloneNotSupportedException the clone not supported exception
     */
    public static void main(String[] args) throws CloneNotSupportedException {
        Scanner scan = new Scanner(System.in);
        Scanner scan1 = new Scanner(System.in);
        Scanner scan2 = new Scanner(System.in);

        Integer points = 0; //liczba zdobytych punktów
        maxPoints = 0;
        int control = 0; //służy do wyboru opcji
        System.out.print("Dzien dobry! Jak masz na imie?\n");
        String name = scan.nextLine(); //nazwa użytkownika
        questionsDBVector = new Vector<QuestionsDB>(); //vector of all question databases loaded
        questionsDBVector.add(new QuestionsDB()); //adding to the vector default question base
        questionsDBVector.add(new QuestionsDB("questions2", true)); //adding to the vector second question base
        questionBase = (QuestionsDB) questionsDBVector.get(0).clone();
        while(control != 7){
            System.out.print("Dobrze, " + name + ", co chcialbys zrobic? (wpisz odpowiedni numer)\n1. Rozpocznij quiz!\n2. Zaproponuj pytanie. (juz wkrotce)\n3. Pokaz moj wynik.\n4. Wypisz mozliwe pytania.\n5. Wybierz zestaw pytan.\n6. Zapisz zestaw pytan do pliku.\n7. Opusc gre.\n");
            control = scan.nextInt();
            switch (control){
                case 1:{
                    System.out.print("Wybierz kategorie:\n");//rusza procedura wyboru kategorii
                    int i;
                    for (i = 0; i<questionBase.getCategoryVector().size(); i++){
                        System.out.print((i+1)+". "+questionBase.getCategoryVector().get(i)+".\n");
                    }
                    System.out.print((i+1)+"+. Wszystkie.\n");
                    Integer controlCat = scan.nextInt(); //sluzy do wyboru kategorii
                    if (controlCat < i+1){
                        points += Quiz(questionBase.getQuestionVector(questionBase.getCategoryVector().get(controlCat -1))); //wybór zestawu pytań z konkretnej kategorii, przeprowadzenie quizu i dodanie ewentualnych punktów
                    }
                    else{ //jeśli nie zostanie wybrana kategoria
                        points += Quiz(questionBase.getQuestionVector());//wzięcie pełnego zestawu pytań, przeprowadzenie quizu i dodanie ewentualnych punktów
                    }
                    break;
                }
                case 3:{
                    System.out.print(name + ", dotychczas zdobyles " + points + " na " + maxPoints + " mozliwych punktow.\n");
                    break;
                }
                case 4:{
                    questionBase.printQuestions();
                    break;
                }
                case 5:{
                    System.out.print("Wybierz jedna z dostepnych baz lub zaimportuj nowa:\n");
                    questionsDBVector.sort(new DBComparator()); //using Comparator to sort objects
                    int numberOfQuestionBases = questionsDBVector.size();
                    for(int i = 1; i <= numberOfQuestionBases; i++)
                        System.out.print(i + ". " + questionsDBVector.get(i-1).getQuestionsPath() + " (" +questionsDBVector.get(i-1).getQuestionVector().size()+ " pytan)\n");
                    System.out.print((numberOfQuestionBases + 1) + ". Dodaj nowy zestaw.\n" + (numberOfQuestionBases + 2) + ". Anuluj.\n");
                    int controlDB = scan.nextInt(); //to select set of questions
                    if(controlDB <= numberOfQuestionBases && controlDB > 0){
                        questionBase = (QuestionsDB) questionsDBVector.get(controlDB - 1).clone(); //deep copying
                        System.out.print("Pomyslnie wybrano zestaw!\n");
                    }
                    else if (controlDB == numberOfQuestionBases + 1){
                        System.out.print("Jakiego typu jest to plik?\n1. Tekstowy.\n2. Binarny.\n");
                        int controlType = scan1.nextInt();
                        if (controlType != 1 && controlType != 2){
                            System.out.print("Anulowano.\n");
                        }
                        else {
                            boolean cont = controlType == 1; //true - text, false - binary
                            if (cont)
                                System.out.print("Podaj sciezke do testowego pliku tekstowego (jako testowy przygotowany zostal plik questions3txt):\n");
                            else
                                System.out.print("Podaj sciezke do testowego pliku binarnego (jako testowy przygotowany zostal plik questions3bin):\n");
                            String path = scan2.nextLine(); //to read the path to the file
                            try (BufferedReader ignored = Files.newBufferedReader(Paths.get(path))) { //BufferedReader is not used here, it is only used to check if file exists (passed as resource for "try", so it is closed automatically)
                                questionsDBVector.add(new QuestionsDB(path, cont)); //if "cont" == true, it reads text file, if false - binary
                                questionBase = (QuestionsDB) questionsDBVector.lastElement().clone(); //selecting new database as the current one
                            } catch(NullPointerException e) { //it may be triggered when trying to open existing text file as binary file
                                System.out.print("Nieprawidlowy typ pliku! Wybrano domyslna baze.\n");
                                questionBase = (QuestionsDB) questionsDBVector.firstElement().clone();
                            } catch (InvalidPathException e){
                                System.out.print("Nieprawidlowa sciezka! Anulowano.\n");
                            } catch (IOException e) {
                                System.out.print("Taki plik nie istnieje! Anulowano.\n");
                            }
                        }
                    }
                    else
                        System.out.print("Anulowano.\n");
                    break;
                }
                case 6:{
                    System.out.print("Ktora sposrod dostepnych baz chcesz zapisac do pliku?\n");
                    questionsDBVector.sort(new DBComparator());
                    int numberOfQuestionBases = questionsDBVector.size();
                    for(int i = 1; i <= numberOfQuestionBases; i++)
                        System.out.print(i + ". " + questionsDBVector.get(i-1).getQuestionsPath() + " (" +questionsDBVector.get(i-1).getQuestionVector().size()+ " pytan)\n");
                    System.out.print((numberOfQuestionBases + 1) + ". Anuluj.\n");
                    int controlDB = scan.nextInt();
                    if(controlDB <= numberOfQuestionBases && controlDB > 0){
                        System.out.print("Wybierz sposob zapisu:\n1. Tekstowo.\n2. Binarnie.\n");
                        int controlType = scan1.nextInt();
                        if (controlType == 1){
                            System.out.print("Podaj sciezke, pod ktora ma zostac zapisany plik (tekstowo):\n");
                            String path = scan2.nextLine();
                            try{
                                Paths.get(path); //to check if name of path is correct - it will throw exception if not
                                questionsDBVector.get(controlDB - 1).WriteTextFile(path);
                                System.out.print("Zapisano!\n");
                            } catch (InvalidPathException e){
                                System.out.print("Nieprawidlowa sciezka! Anulowano.\n");
                            }
                        }
                        else if (controlType == 2){
                            System.out.print("Podaj sciezke, pod ktora ma zostac zapisany plik (binarnie):\n");
                            String path = scan2.nextLine();
                            try{
                                Paths.get(path); //to check if name of path is correct
                                questionsDBVector.get(controlDB - 1).WriteBinaryFile(path);
                                System.out.print("Zapisano!\n");
                            } catch (InvalidPathException e){
                                System.out.print("Nieprawidlowa sciezka! Anulowano.\n");
                            }
                        }
                        else {
                            System.out.print("Anulowano.\n");
                        }
                    }
                    else
                        System.out.print("Anulowano.\n");
                    break;
                }
            }
        }
        scan.close();
        scan1.close();
        scan2.close();
    }
    /**
     * Method of quiz executing. Running it will allow user to answer three questions and earn max. 3 points.
     * @param questionSet vector of questions possible to be asked
     * @return number of earned points, range 0-3
     */
    private static Integer Quiz(Vector<Question> questionSet){
        Vector<Question> tempVec = new Vector<Question>(); //wektor trzech losowych pytań
        int n = 0; //licznik zdobytych punktow
        int m; //służy do iterowania po wektorze pytań
        int o; //odpowiedz wskazana przez gracza
        int p; //identyfikator prawidlowej odpowiedzi
        Scanner scan = new Scanner(System.in); //skaner odpowiedzi gracza
        Random generator = new Random(); //inicjalizacja generatora
        while(tempVec.size()<3){ //wylosowanie trzech pytań z dostępnej puli
            m = generator.nextInt(questionSet.size());
            if(!tempVec.contains(questionSet.get(m)))
                tempVec.add(questionSet.get(m));
        }
        for(int i = 0; i < 3; i++){
            p = tempVec.get(i).PrintQuestion(); //drukowanie pytania wraz z odpowiedziami
            o = scan.nextInt();
            if (o == p){ //jeśli odpowiedź prawidłowa
                System.out.print("Brawo, zdobywasz punkt!\n");
                n++;
            }
            else{
                System.out.print("Niestety, prawidlowa odpowiedz to: " + tempVec.get(i).getCorrect() + ".\n");
            }
        }
        System.out.print("Zdobyles " + n + " na 3 mozliwych punktow. Co dalej?\n");
        maxPoints += 3;
        scan.close();
        return n;
    }
}
