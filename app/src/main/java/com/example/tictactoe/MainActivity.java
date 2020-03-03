package com.example.tictactoe;

import androidx.appcompat.app.AppCompatActivity;

import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

/*
    Auteur: Patrick Lainesse    0740302

    Le constraint layout a été prévilégié car c'était plus simple ainsi de faire coordonner les cases avec la grille du fond d'écran

    Sources:
    algorithme du jeu: fichier sur Studium par Michel Reid
    fond d'écran: Pixabay - https://pixabay.com/illustrations/board-chalk-strokes-tic-tac-toe-2097446/
    police: Marta van Eck - https://www.fontspace.com/marta-van-eck-designs/chalkboard-by-marta-van-eck
 */

public class MainActivity extends AppCompatActivity {

    Button[] grille = new Button[9];
    private int tour = 0;
    private boolean termine = false;

    TextView msg;
    TextView titre;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tour = 0;

        msg = (TextView)findViewById(R.id.messages);
        titre = (TextView)findViewById(R.id.titre);

        // création de la grille de tic tac toe
        grille[0] = (Button)findViewById(R.id.bouton0);
        grille[1] = (Button)findViewById(R.id.bouton1);
        grille[2] = (Button)findViewById(R.id.bouton2);
        grille[3] = (Button)findViewById(R.id.bouton3);
        grille[4] = (Button)findViewById(R.id.bouton4);
        grille[5] = (Button)findViewById(R.id.bouton5);
        grille[6] = (Button)findViewById(R.id.bouton6);
        grille[7] = (Button)findViewById(R.id.bouton7);
        grille[8] = (Button)findViewById(R.id.bouton8);

        //application de la police d'écriture au X et O
        Typeface craie = Typeface.createFromAsset(getAssets(), "font/chalkboard.ttf");
        for(int i=0; i<9; i++) {
            grille[i].setTypeface(craie);
        }
        titre.setTypeface(craie);
    }

    // initialisation de la grille à l'ouverture de l'application
    public void initialise(View v) {
        for (int i = 0; i < 9; i++) {
            grille[i].setText("");
            grille[i].setTextColor(Color.WHITE);
        }
        tour = 0;
        termine = false;
        msg.setText("");
    }

    // méthode appelée au clic du bouton "Sources", qui affiche les sources utilisées avec un délai de 2 secondes entre chacune
    public void afficherSources(View v) {

        msg.setText(R.string.sCode);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msg.setText(R.string.sFond);
            }
        }, 2000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msg.setText(R.string.sPolice);
            }
        }, 4000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msg.setText(R.string.auteur);
            }
        }, 6000);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                msg.setText("");
            }
        }, 8000);
    }

    // méthode principale qui appelera les autres méthodes selon l'endroit qui est cliqué dans la grille
    public void clic(View v) {
        Button unBouton = (Button)v;

        if(termine) {
            msg.setText(R.string.gameover);
            return;
        }

        // vérifie si l'emplacement sélectionné est déjà occupé
        if(unBouton.getText().equals("O") || unBouton.getText().equals("X")) {
            msg.setText(R.string.dejaJoue);
            return;
        }

        // si la partie n'est pas terminée ou si ce n'est pas déjà occupé
        unBouton.setText("X");
        tour++;

        if(gagnant("X")) {
            termine = true;
            msg.setText(R.string.xgagne);
        }

        // si ce n'est pas nul, à O de jouer
        else if(!isPartieNulle())
        {
            getO();

            if(gagnant("O")) {
                termine = true;
                msg.setText(R.string.ogagne);
            }
        }

        else        //Partie nulle
        {
            termine = true;
            msg.setText(R.string.nulle);
        }
    }

    // Fonction qui retourne le poids numérique associé à une valeur dans une case.
    // Valeur = X, poids = 1; valeur = O, poids = -1; et la chaîne vide a un poids = 0.
    private int lePoids(int cellule) {
        if(grille[cellule].getText() == "X") {
            return 1;
        }
        else
            if(grille[cellule].getText().equals("O")) {
                return -1;
            }
            else return 0;
    }

    /* Fonction qui remplit un tableau d'entiers représentant chacunes des lignes,
   colonnes et diagonales possibles.  La valeur sera la somme des poids des cases de cette
   ligne, colonne ou diagonale.

   Les 3 premiers éléments du tableau représentent les lignes, les 3 suivants, les
   colonnes et les 2 derniers, les diagonales.
    */
    private void lesPoids(int[] solutions){
        int n;

        for(int j = 0; j<3; j++)
        {	n = 0;
            for(int i=0; i<3; i++)
                n += lePoids(i+ j*3);
            solutions[j] = n;
        }

        for(int j = 0; j<3; j++)
        {	n = 0;
            for(int i=0; i<3; i++)
                n += lePoids(i*3+ j);
            solutions[j+3] = n;
        }

        n=0;
        for(int i = 0; i <3 ; i++)
            n+=lePoids(i*4);
        solutions[6] = n;

        n = 0;
        for(int i = 0; i <3 ; i++)
            n+=lePoids((i+1)*2);
        solutions[7] = n;
    }

    /*  Fonction qui détermine la case où jouer le prochain O.
     *
     *  La fonction privilégie une case vide permettant une victoire
     *  immédiate de O. Si un tel coup n'est pas disponible, la fonction
     *  cherche un coup qui empêcherait X de gagner au prochain coup.
     *
     *  Valeur retournée : l'indice, dans la grille, où jouer le prochain O.
     */
    public int getO() {

        int[] poids = new int[8];
        int ch = -1;

        if(tour==1){
            if(grille[4].getText().equals("X"))
            {
                grille[0].setText("O");
                return 0;
            }
            else{
                grille[4].setText("O");
                return 4;
            }
        }

        if(tour==2){                     // X a joué 2 fois et O une seule.
            lesPoids(poids);
            for(int i = 0; i<8; i++){
                if(poids[i] == 2){       // il y a 2 X et une vide
                    int n = lIndice(i);  // l'indice de la case vide
                    grille[n].setText("O"); // on y place O pour empêcher la
                    return n;            // victoire de X.
                }
            }

            if(!grille[0].getText().equals("") && !grille[4].getText().equals("") && !grille[8].getText().equals(""))
                if(grille[4].getText().equals("O"))
                {
                    grille[1].setText("O");
                    return 1;
                }
                else
                {
                    grille[2].setText("O");
                    return 2;
                }
            else
            if(!grille[6].getText().equals("") && !grille[4].getText().equals("") && !grille[2].getText().equals(""))
                if(grille[4].getText().equals("O"))
                {
                    grille[1].setText("O");
                    return 1;
                }
                else
                {
                    grille[2].setText("O");
                    return 2;
                }
            else
            if(grille[4].getText().equals("O")){
                if(grille[0].getText().equals("X"))
                {	if( grille[7].getText().equals("X")){
                    grille[6].setText("O");
                    return 6;
                }
                else
                if( grille[5].getText().equals("X")){
                    grille[2].setText("O");
                    return 2;
                }
                }
                else
                if(grille[8].getText().equals("X"))
                {	if (grille[1].getText().equals("X")){
                    grille[2].setText("O");
                    return 2;
                }
                else
                if (grille[3].getText().equals("X")){
                    grille[6].setText("O");
                    return 6;
                }
                }
                else
                if(grille[2].getText().equals("X"))
                {	if (grille[7].getText().equals("X")){
                    grille[8].setText("O");
                    return 8;
                }
                else
                if (grille[3].getText().equals("X")){
                    grille[0].setText("O");
                    return 0;
                }
                }

                else
                if(grille[6].getText().equals("X"))
                {	if (grille[1].getText().equals("X")){
                    grille[0].setText("O");
                    return 0;
                }
                else
                if (grille[5].getText().equals("X")){
                    grille[8].setText("O");
                    return 8;
                }
                }
                else
                if(grille[1].getText().equals("X"))
                {
                    if(grille[5].getText().equals("X"))
                    {
                        grille[2].setText("O");
                        return 2;
                    }
                    else
                    if(grille[3].getText().equals("X"))
                    {
                        grille[0].setText("O");
                        return 0;
                    }
                }
                else
                if(grille[7].getText().equals("X"))
                {
                    if(grille[5].getText().equals("X"))
                    {
                        grille[8].setText("O");
                        return 8;
                    }
                    else
                    if(grille[3].getText().equals("X"))
                    {
                        grille[6].setText("O");
                        return 6;
                    }
                }

            }

        }

        // troisième tour ou plus.
        lesPoids(poids);

        for(int i = 0; i<8; i++){
            if(poids[i] == -2){      // il ne manque qu'un O pour gagner
                int n = lIndice(i);  // indice de la case vide
                grille[n].setText("O"); // on y place O
                return n;
            }
            if(poids[i]==2) // Il ne manque qu'un X pour gagner, avant d'y
                ch = i;     // jouer un O, on va regarder s'il y a possibilité
        }                   // d'une victoire immédiate de O ailleurs.

        if(ch >=0){			// Aucune victoire immédiate, mais défaite potentielle.
            int n = lIndice(ch);  // l'indice de la case vide à bloquer.
            grille[n].setText("O");
            return n;
        }
        ch = -1;

        // Pas de cas de victoire immédiate ou défaite au prochain tour.
        for(int i =0; i<8; i++){
            if(poids[i]==-1)     // Est-ce qu'il y a plus de O que de X?
                if(lIndice(i)>=0) // Est-ce qu'il y a une case vide (donc
                    ch=i;		  // 1 O et pas de X)?
        }

        if(ch>=0){   // s'il y a une ligne, colonne ou diagonale avec 1 O et
            int n = lIndice(ch);  // pas de X, alors on place le O dans une des
            grille[n].setText("O");    // cases vides.  Remarque, voici l'endroit où
            return n;             // cet algo n'est pas optimal, car il ne vérifie
        }                         // pas quelle case augmenterais la probabilité de
        // victoire.

        for(int i=0; i<8; i++)    // Aucune ligne, colonne ou diagonale où il est encore
            if(lIndice(i)>=0){    // possible de gagner, on cherche une case vide.
                int n = lIndice(i);
                grille[n].setText("O");
                return n;
            }
        return -1;
    }

    // retourne la première case vide rencontrée selon l'indice de la ligne, colonne ou
    // diagonale.  Les indices 0 à 2 représentent les trois lignes, 3 à 5, les trois
    // colonnes, 7 et 8, les deux diagonales.
    private int lIndice(int pos){
        if(pos <3)
        {
            for(int i = 0; i<3; i++)
                if(grille[pos *3 + i].getText().equals(""))
                    return pos*3 +i;
        }
        else
        if(pos < 6)
        {	for(int i = 0; i<3; i++)
            if(grille[3 * i+ pos-3].getText().equals(""))
                return 3*i + pos -3;
        }
        else
        if(pos==6){
            for(int i = 0; i<3; i++)
                if(grille[i*4].getText().equals(""))
                    return i *4;

        }
        else
        if(pos==7){
            for(int i = 0; i<3; i++)
                if(grille[(i+1)*2].getText().equals(""))
                    return (i+1)*2;
        }
        return -1;

    }

    // Est-ce que la partie est nulle?
    // Oui, si X a joué dans 5 cases et O dans 4 cases sans qu'il y ait de gagnant.
    public boolean isPartieNulle(){
        return tour==5;
    }

    /* Il y a 8 combinaisons gagnantes :
     *La fonction va vérifier si la chaîne de caractères représentant
     *le joueur, qui est reçue en paramètre, est présente dans toutes
     *les cases de l'une de ces combinaisons.
     *
     *Si oui, alors le tableau pos, reçu en paramètre, sera modifié de
     *façon à contenir les 3 cases représentant la combinaison gagnante
     *du joueur.
     */

    public boolean gagnant(String joueur) {

        if(grille[0].getText().equals(joueur) && grille[1].getText().equals(joueur)&& grille[2].getText().equals(joueur))
        {
            grille[0].setTextColor(Color.RED);
            grille[1].setTextColor(Color.RED);
            grille[2].setTextColor(Color.RED);
            return true;
        }
        if(grille[0].getText().equals(joueur) && grille[4].getText().equals(joueur)&& grille[8].getText().equals(joueur))
        {
            grille[0].setTextColor(Color.RED);
            grille[4].setTextColor(Color.RED);
            grille[8].setTextColor(Color.RED);
            return true;
        }
        if(grille[0].getText().equals(joueur) && grille[3].getText().equals(joueur)&& grille[6].getText().equals(joueur))
        {
            grille[0].setTextColor(Color.RED);
            grille[3].setTextColor(Color.RED);
            grille[6].setTextColor(Color.RED);
            return true;
        }

        if(grille[6].getText().equals(joueur) && grille[7].getText().equals(joueur)&& grille[8].getText().equals(joueur))
        {
            grille[6].setTextColor(Color.RED);
            grille[7].setTextColor(Color.RED);
            grille[8].setTextColor(Color.RED);
            return true;
        }
        if(grille[6].getText().equals(joueur) && grille[4].getText().equals(joueur)&& grille[2].getText().equals(joueur))
        {
            grille[6].setTextColor(Color.RED);
            grille[4].setTextColor(Color.RED);
            grille[2].setTextColor(Color.RED);
            return true;
        }

        if(grille[3].getText().equals(joueur)&& grille[4].getText().equals(joueur)&& grille[5].getText().equals(joueur))
        {
            grille[3].setTextColor(Color.RED);
            grille[4].setTextColor(Color.RED);
            grille[5].setTextColor(Color.RED);
            return true;
        }
        if(grille[1].getText().equals(joueur)&& grille[4].getText().equals(joueur)&& grille[7].getText().equals(joueur))
        {
            grille[1].setTextColor(Color.RED);
            grille[4].setTextColor(Color.RED);
            grille[7].setTextColor(Color.RED);
            return true;
        }
        if(grille[2].getText().equals(joueur)&& grille[8].getText().equals(joueur)&& grille[5].getText().equals(joueur))
        {
            grille[2].setTextColor(Color.RED);
            grille[8].setTextColor(Color.RED);
            grille[5].setTextColor(Color.RED);
            return true;
        }

        return false;
    }

    @Override
    public void onRestoreInstanceState(Bundle saveState) {
        super.onRestoreInstanceState(saveState);

        for (int i = 0; i < 9; i++) {
            String cle = "jeu" + i;
            grille[i].setText(saveState.getString(cle));
        }

        tour = saveState.getInt("noTour");
        termine = saveState.getBoolean("fini");
        msg.setText(R.string.welcome);
    }

    @Override
    public void onSaveInstanceState(Bundle outstate) {
        super.onSaveInstanceState(outstate);

        for (int i = 0; i < 9; i++) {
            String cle = "jeu" + i;
            outstate.putString(cle, grille[i].getText().toString());
        }

        outstate.putInt("noTour", tour);
        outstate.putBoolean("fini", termine);
    }
}
