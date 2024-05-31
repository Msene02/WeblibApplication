package com.example.weblibapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Layout;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Button;
import com.example.weblibapplication.Chord;

import java.util.Arrays;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class MainActivity extends AppCompatActivity {
    private Handler handler = new Handler(Looper.getMainLooper());
    private TextView textView;
    private Button startButton;
    private Button resetButton;
    private RadioButton rb5;
    private RadioButton rb10;
    private RadioButton rb15;
    private Switch minors;
    private Switch sharp_flat;
    private Runnable defileRunnable;
    private int displayDuration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rb5 = findViewById(R.id.five_sec);
        rb10 = findViewById(R.id.ten_sec);
        rb15 = findViewById(R.id.fifteen_sec);
        textView = findViewById(R.id.notes);
        startButton = findViewById(R.id.Start);
        resetButton = findViewById(R.id.Reset);
        minors = findViewById(R.id.Minors);
        sharp_flat = findViewById(R.id.Sharp_flat);
        resetButton.setEnabled(false);


        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startDefiling();
                disableOtherButtons();
            }
        });

        resetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enableOtherButtons();
                stopDefiling();
            }
        });

        rb5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDuration = 500;
            }
        });

        rb10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDuration = 10000;
            }
        });

        rb15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayDuration = 15000;
            }
        });
        // Affichez l'élément actuel
        displayCurrentElement(Chord.getRandomOption());
    }

    private void startDefiling() {
        defileRunnable = new Runnable() {
            @Override
            public void run() {
                List<Chord> chordsToDisplay;

                // Vérifie si l'un des switches est coché
                if (minors.isChecked() && !sharp_flat.isChecked()) {
                    // Filtrage spécifique pour les éléments mineurs
                    chordsToDisplay = Arrays.stream(Chord.values())
                            .filter(chord -> !chord.isSharp() && !chord.isFlat())
                            .collect(Collectors.toList());
                } else if (sharp_flat.isChecked() && !minors.isChecked()) {
                    // Filtrage spécifique pour les éléments sharp ou flat
                    chordsToDisplay = Arrays.stream(Chord.values())
                            .filter(chord -> !chord.isMinor())
                            .collect(Collectors.toList());
                } else if (sharp_flat.isChecked() && minors.isChecked()) {
                    chordsToDisplay =Arrays.stream(Chord.values())
                            .filter(chord -> chord.isMinor() || chord.isFlat() || chord.isSharp())
                            .collect(Collectors.toList());
                } else {
                    // Si aucun switch n'est coché, exclure les éléments mineurs, sharp, et flat
                    chordsToDisplay = Arrays.stream(Chord.values())
                            .filter(chord ->!chord.isMinor() &&!chord.isSharp() &&!chord.isFlat())
                            .collect(Collectors.toList());
                }

                Chord randomOption = chordsToDisplay.isEmpty()? null : chordsToDisplay.get(new Random().nextInt(chordsToDisplay.size()));

                // Mettez à jour l'affichage
                displayCurrentElement(randomOption);

                // Continuez le défilement
                startDefiling();
            }
        };
        handler.postDelayed(defileRunnable, displayDuration);
    }


    private void stopDefiling() {
        handler.removeCallbacks(defileRunnable);
        displayCurrentElement(null);
    }

    /*
    @SuppressLint("Range")
    private void displayCurrentElement(Chord option) {
        if (option == null) {
            textView.setText("");
        } else {
            SpannableStringBuilder builder = new SpannableStringBuilder(option.getValue() + " ");

            // Appliquer une taille spécifique au texte "value"
            builder.setSpan(new RelativeSizeSpan(1.5f), 0, option.getValue().length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

            // Ajout du symbole "m" en bas à droite de la valeur
            int lastIndexOfValue = builder.length() - 1; // Index de la dernière lettre de la valeur
            builder.append("m"); // Ajout du symbole "m"

            // Appliquer une taille spécifique au symbole "m"
            builder.setSpan(new RelativeSizeSpan(-1.5f), lastIndexOfValue + 1, builder.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); // +1 pour inclure le symbole "m"

            textView.setText(builder);
        }
    }*/

    @SuppressLint("Range")
    private String buildChordString(Chord chord) {
        StringBuilder chordBuilder = new StringBuilder();

        // Construire la base de la chaîne sans les symboles spéciaux
        chordBuilder.append(chord.getValue());

        // Calculer la longueur de la valeur pour déterminer où placer les symboles
        int lengthOfValue = chord.getValue().length();

        // Ajouter le symbole # en bas à droite si isSharp est true
        if (chord.isSharp()) {
            chordBuilder.append("#");
        }

        // Ajouter le symbole b en bas à droite si isFlat est true
        if (chord.isFlat()) {
            chordBuilder.append("b");
        }

        // Ajouter le symbole m en bas à droite si isMinor est true
        if (chord.isMinor()) {
            chordBuilder.append("m");
        }

        // Créer un SpannableStringBuilder pour appliquer des styles spécifiques
        SpannableStringBuilder spannableBuilder = new SpannableStringBuilder(chordBuilder.toString());

        // Définir la taille relative de la valeur pour qu'elle soit plus grande que les symboles
        int valueStartIndex = 0; // Le début de la valeur
        int valueEndIndex = chord.getValue().length(); // La fin de la valeur
        spannableBuilder.setSpan(new RelativeSizeSpan(1.5f), valueStartIndex, valueEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // Définir la taille relative des symboles pour qu'ils soient plus petits que la valeur
        int symbolStartIndex = chord.getValue().length(); // Le début du premier symbole après la valeur
        int symbolEndIndex = chordBuilder.length(); // La fin de la chaîne
        spannableBuilder.setSpan(new RelativeSizeSpan(-1.5f), symbolStartIndex, symbolEndIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        return spannableBuilder.toString();
    }


    private void displayCurrentElement(Chord option) {
        if (option == null) {
            textView.setText("");
        } else {
            String chordString = buildChordString(option);
            textView.setText(chordString);
        }
    }





    public void disableOtherButtons(){
        rb5.setEnabled(false);
        rb10.setEnabled(false);
        rb15.setEnabled(false);
        resetButton.setEnabled(true);
        startButton.setEnabled(false);
        minors.setEnabled(false);
        sharp_flat.setEnabled(false);

    }

    public void enableOtherButtons(){
        rb5.setEnabled(true);
        rb10.setEnabled(true);
        rb15.setEnabled(true);
        startButton.setEnabled(true);
        resetButton.setEnabled(false);
        minors.setEnabled(true);
        sharp_flat.setEnabled(true);

    }


}