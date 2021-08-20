package com.example.evaluacionparcial;

import android.util.Log;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.nl.languageid.IdentifiedLanguage;
import com.google.mlkit.nl.languageid.LanguageIdentification;
import com.google.mlkit.nl.languageid.LanguageIdentifier;
import com.google.mlkit.nl.translate.TranslateLanguage;
import com.google.mlkit.nl.translate.Translation;
import com.google.mlkit.nl.translate.Translator;
import com.google.mlkit.nl.translate.TranslatorOptions;

import java.util.List;
import java.util.Objects;

public class Traslator {

    private String textResultParam = "";
    private String translatorFinish = "";

    private String texto_traducido = "";
    TextView view_texto_traducido = null;

    public Traslator (String textResultParam, String translatorFinish, TextView txtTraduccion) {
        this.textResultParam = textResultParam;
        this.translatorFinish = translatorFinish;
        this.view_texto_traducido = txtTraduccion;
    }

    public String getTextResultParam() {
        return textResultParam;
    }

    public void setTextResultParam(String textResultParam) {
        this.textResultParam = textResultParam;
    }

    public String getTranslatorFinish() {
        return translatorFinish;
    }

    public void setTranslatorFinish(String translatorFinish) {
        this.translatorFinish = translatorFinish;
    }

    public String getTexto_traducido() {
        return texto_traducido;
    }

    public void setTexto_traducido(String texto_traducido) {
        this.texto_traducido = texto_traducido;
        this.view_texto_traducido.setText(texto_traducido);
    }

    public void possibleLanguageIdentifier() {
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyPossibleLanguages(this.textResultParam)
                .addOnSuccessListener(new OnSuccessListener<List<IdentifiedLanguage>>() {
                    @Override
                    public void onSuccess(List<IdentifiedLanguage> identifiedLanguages) {
                        boolean flag = true;
                        String languageConfidence = "";
                        for (IdentifiedLanguage identifiedLanguage : identifiedLanguages) {

                            String language = identifiedLanguage.getLanguageTag();
                            float confidence = identifiedLanguage.getConfidence();
                            confidence = confidence * 100;

                            //https://developers.google.com/ml-kit/language/identification/langid-support?hl=es
                            languageConfidence = languageConfidence + "Lenguaje= " + language + " - Porcentaje= " + confidence + "\n";

                            if (flag) {
                                translatorLanguage(textResultParam, language, getTranslatorFinish());
                                flag = false;
                            }
                        }
                        Log.i("Logs", languageConfidence);
                        //textView.setText(languageConfidence);
                    }
                })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });

    }

    private void translatorLanguage(String textResultParam, String tagLanguageStart, String tagLanguageEnd) {
        // Create an English-German translator:
        try {
            TranslatorOptions options =
                    new TranslatorOptions.Builder()
                            .setSourceLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(tagLanguageStart)))
                            .setTargetLanguage(Objects.requireNonNull(TranslateLanguage.fromLanguageTag(tagLanguageEnd)))
                            .build();
            final Translator translator =
                    Translation.getClient(options);


            DownloadConditions conditions = new DownloadConditions.Builder()
                    .requireWifi()
                    .build();
            translator.downloadModelIfNeeded(conditions)
                    .addOnSuccessListener(
                            new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void v) {
                                    // Model downloaded successfully. Okay to start translating.
                                    // (Set a flag, unhide the translation UI, etc.)
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Model couldn’t be downloaded or other internal error.
                                    // ...
                                }
                            });

            translator.translate(textResultParam)
                    .addOnSuccessListener(
                            new OnSuccessListener<String>() {
                                @Override
                                public void onSuccess(@NonNull String translatedText) {
                                    Log.i("Logs", "Language: " + translatedText);
                                    setTexto_traducido(translatedText);
                                    //multinetext.setText("");
                                    //multinetext.setText(translatedText);
                                }
                            })
                    .addOnFailureListener(
                            new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    // Error.
                                    // ...
                                }
                            });
        } catch (Exception e) {
            Log.i("Logs", "Language: " + e.toString());

        }
    }

    private void languageIdentifier(String textResultParam) {
        LanguageIdentifier languageIdentifier =
                LanguageIdentification.getClient();
        languageIdentifier.identifyLanguage(textResultParam)
                .addOnSuccessListener(
                        new OnSuccessListener<String>() {
                            @Override
                            public void onSuccess(@Nullable String languageCode) {
                                if (languageCode.equals("und")) {
                                    Log.i("Logs", "Can't identify language.");
                                } else {
                                    Log.i("Logs", "Language: " + languageCode);
                                }
                            }
                        })
                .addOnFailureListener(
                        new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                // Model couldn’t be loaded or other internal error.
                                // ...
                            }
                        });
    }

}
