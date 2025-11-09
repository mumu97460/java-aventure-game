package fr.insaRouen.iti.prog.asiaventure;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Scanner;

import fr.insaRouen.iti.prog.asiaventure.elements.Entite;
import fr.insaRouen.iti.prog.asiaventure.elements.Executable;
import fr.insaRouen.iti.prog.asiaventure.elements.objets.serrurerie.Serrure;
import fr.insaRouen.iti.prog.asiaventure.elements.objets.serrurerie.Clef;
import fr.insaRouen.iti.prog.asiaventure.elements.structure.Piece;
import fr.insaRouen.iti.prog.asiaventure.elements.structure.Porte;
import fr.insaRouen.iti.prog.asiaventure.elements.vivants.JoueurHumain;
import fr.insaRouen.iti.prog.asiaventure.elements.vivants.Vivant;

public class Simulateur implements java.io.Serializable {
    private Monde monde;
    private ArrayList<ConditionDeFin> conditionsDeFin;
    private EtatDuJeu etatDuJeu;
    Scanner stdin = new Scanner(System.in);

    public Simulateur(Monde monde, ConditionDeFin... conditionsDeFin) {
        this.monde = monde;
        this.conditionsDeFin = new ArrayList<ConditionDeFin>();
        for (ConditionDeFin cdf : conditionsDeFin) {
            this.conditionsDeFin.add(cdf);
        }
    }

    public Simulateur(ObjectInputStream ois) throws IOException, ClassNotFoundException {
        this.monde = (Monde) ois.readObject();
        this.conditionsDeFin = (ArrayList<ConditionDeFin>) ois.readObject();
    }

    public Simulateur(Reader reader) throws NomDEntiteDejaUtiliseDansLeMondeException {
        Scanner s = new Scanner(reader);
        this.conditionsDeFin = new ArrayList<ConditionDeFin>();
        while (s.hasNext()) {
            switch (s.next()) {
                case "Monde":
                    this.monde = construitMonde(s);
                    break;
                case "Piece":
                    construitPiece(s, this.monde);
                    break;
                case "Porte":
                    construitPorte(s, this.monde);
                    break;
                case "PorteSerrure":
                    construitPorteSerrure(s, this.monde);
                    break;
                case "Clef":
                    construitClef(s, this.monde);
                    break;
                case "JoueurHumain":
                    construitJoueurHumain(s, this.monde);
                    break;
                case "ConditionDeFinVivantDansPiece":
                    construitConditionDeFinVivantDansPiece(s, this.monde);
                    break;
                default:
                    break;
            }
        }
    }

    private Monde construitMonde(Scanner s) {
        String nom = s.next().replaceAll("\"", "");
        return new Monde(nom);
    }

    private void construitPiece(Scanner s, Monde monde) throws NomDEntiteDejaUtiliseDansLeMondeException {
        String nom = s.next().replaceAll("\"", "");
        new Piece(nom, monde);
    }

    private void construitPorte(Scanner s, Monde monde) throws NomDEntiteDejaUtiliseDansLeMondeException {
        String nom = s.next().replaceAll("\"", "");
        Piece piece1 = monde.getPiece(s.next().replaceAll("\"", ""));
        Piece piece2 = monde.getPiece(s.next().replaceAll("\"", ""));
        new Porte(nom, monde, piece1, piece2, true);
    }

    private void construitPorteSerrure(Scanner s, Monde monde) throws NomDEntiteDejaUtiliseDansLeMondeException {
        String nom = s.next().replaceAll("\"", "");
        Piece piece1 = monde.getPiece(s.next().replaceAll("\"", ""));
        Piece piece2 = monde.getPiece(s.next().replaceAll("\"", ""));
        Serrure serrure = new Serrure(monde);
        new Porte(nom, monde, serrure, piece1, piece2);
    }

    private void construitClef(Scanner s, Monde monde) {
        Porte porte = monde.getPorte(s.next().replaceAll("\"", ""));
        Serrure serrure = porte.getSerrure();
        Clef clef = serrure.creerClef();
        Piece piece = monde.getPiece(s.next().replaceAll("\"", ""));
        piece.deposer(clef);
    }

    private void construitJoueurHumain(Scanner s, Monde monde) throws NomDEntiteDejaUtiliseDansLeMondeException {
        String nom = s.next().replaceAll("\"", "");
        int pointVie = s.nextInt();
        int pointForce = s.nextInt();
        Piece piece = monde.getPiece(s.next().replaceAll("\"", ""));
        new JoueurHumain(nom, monde, pointVie, pointForce, piece);
    }

    private void construitConditionDeFinVivantDansPiece(Scanner s, Monde monde) throws NomDEntiteDejaUtiliseDansLeMondeException {
        String etat_str = s.next();
        EtatDuJeu etat;
        if (etat_str.equals("SUCCES")) {
            etat = EtatDuJeu.SUCCES;
        } else {
            etat = EtatDuJeu.ECHEC;
        }
        Vivant vivant = (Vivant) monde.getEntite(s.next().replaceAll("\"", ""));
        Piece piece = (Piece) monde.getEntite(s.next().replaceAll("\"", ""));
        this.conditionsDeFin.add(new ConditionDeFinVivantDansPiece(etat, vivant, piece));
    }

    public void enregister(ObjectOutputStream oos) throws IOException {
        oos.writeObject(this.monde);
        oos.writeObject(this.conditionsDeFin);
    }

    public EtatDuJeu executerUnTour() throws Throwable {
        // pour chaque joueur humain, afficher sa situation et lui demander de saisir un ordre.
        for (Entite entite : monde.getEntites()) {
            if (entite instanceof JoueurHumain) {
                JoueurHumain joueur = (JoueurHumain)entite;
                System.out.println(String.format("%s\nVeuillez saisir un ordre", joueur.toString()));
                String ordre = this.stdin.nextLine();
                joueur.setOrdre(ordre);
            }
        }

        // pour chaque Executable appeler la méthode executer.
        for (Executable executable : monde.getExecutables()) {
            try {
                executable.executer();
            } catch(ASIAventureException e) {
                System.out.println(e.toString());
            }
        }

        // vérifier chaque condition de fin et retourner ENCOURS si aucune n’est vérifiée, sinon retourner l’état.
        for (ConditionDeFin cdf : this.conditionsDeFin) {
            this.etatDuJeu = cdf.verifierCondition();
            if (this.etatDuJeu != EtatDuJeu.ENCOURS) {
                System.out.println(this.etatDuJeu);
                return this.etatDuJeu;
            }
        }
        return this.etatDuJeu;
    }

    public String toString() {
        return String.format("Simulateur(monde: %s,  conditionsDeFin: [])", this.monde);
    }
}
