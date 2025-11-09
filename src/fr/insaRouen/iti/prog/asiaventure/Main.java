package fr.insaRouen.iti.prog.asiaventure;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) throws ASIAventureException, Exception, Throwable {
        Simulateur s = null;

        Scanner stdin = new Scanner(System.in);
        while (true) {
            System.out.println("--- Menu ---\n\t1/ jouer\n\t2/ charger un fichier de description\n\t3/ sauver la partie actuelle\n\t4/ charger une partie\n\t5/ quitter");
            switch (stdin.nextInt()) {
                case 1:
                    if (s == null) {
                        System.out.println("Création d'un monde par défaut");
                        //s = new Simulateur(new Monde("Monde"));
                        s = new Simulateur(new FileReader(new File("/home/mgevia/Documents/ITI3/2/java_avancee/TP9/asiaventure/world.txt")));
                    }
                    while (true) {
                        s.executerUnTour();
                        System.out.println("Souhaitez vous rejouer?");
                        String ordre = stdin.next();
                        if (!ordre.trim().equals("oui")) {
                            break;
                        }
                    }
                    break;
                case 2:
                    System.out.println("Entrez le chemin vers le fichier de description");
                    String filename = stdin.next();
                    s = new Simulateur(new FileReader(new File(filename)));
                    break;
                case 3:
                    System.out.println("Entrez le chemin vers le fichier de sauvegarde");
                    String out_filename = stdin.next();
                    ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(out_filename));
                    s.enregister(oos);
                    break;
                case 4:
                    System.out.println("Entrez le chemin vers le fichier de chargement");
                    String in_filename = stdin.next();
                    ObjectInputStream ois = new ObjectInputStream(new FileInputStream(in_filename));
                    s = new Simulateur(ois);
                    break;
                case 5:
                    stdin.close();
                    System.exit(0);
                    break;
                default:
                    break;
            }
        }
    }
}
