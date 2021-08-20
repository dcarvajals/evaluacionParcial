package com.example.evaluacionparcial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.HttpsCallableResult;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;
import com.google.mlkit.vision.text.latin.TextRecognizerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    Uri uriImage;
    ImageView img;
    TextView txtPais;
    TextView txtTraduccion;
    String globalMsg = "";
    private FirebaseFunctions mFunctions;
    InputImage inputImage;
    String textResult = "";
    String jsonResponse = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = (ImageView)findViewById(R.id.iv_foto);
        txtPais = (TextView)findViewById(R.id.tv_pais);
        txtTraduccion = (TextView)findViewById(R.id.tv_traduccion);

        Button btnBuscar = (Button)findViewById(R.id.idbuscar);
        btnBuscar.setOnClickListener( v -> {
            buscar();
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            //verifica si se ha seleccionado una imagen
            if (requestCode == 200) {
                //obtener imageUri
                uriImage = data.getData();
                if (uriImage != null) {
                    try {
                        //ubicar imagen en contenedor ImageView
                        img.setImageURI(uriImage);
                        searchFace(uriImage);
                    } catch (Exception ex) {
                        Log.e("", ex.toString());
                    }
                }
            } else if (resultCode == 300) {
                Log.e("", "asd");

            }
        }
    }

    public void selectImage(View view) {
        Intent gallery = new Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setType("Image/*");
        startActivityForResult(gallery, 200);
    }

    public void searchFace(Uri uri) throws IOException {
        InputImage image;
        try {
            image = InputImage.fromFilePath(MainActivity.this, uri);
            detectText(uri);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void detectText (Uri uri) throws IOException {
        inputImage = InputImage.fromFilePath(MainActivity.this, uri);
        TextRecognizer recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS);

        //3. Process the image
        //https://developers.google.com/ml-kit/vision/text-recognition/android#3.-process-the-image
        Task<Text> result =
                recognizer.process(inputImage)
                        .addOnSuccessListener(new OnSuccessListener<Text>() {
                            @Override
                            public void onSuccess(Text visionText) {
                                //4. Extract text from blocks of recognized text
                                //https://developers.google.com/ml-kit/vision/text-recognition/android#4.-extract-text-from-blocks-of-recognized-text
                                String resultText = visionText.getText();
                                for (Text.TextBlock block : visionText.getTextBlocks()) {
                                    String blockText = block.getText();
                                    Point[] blockCornerPoints = block.getCornerPoints();
                                    Rect blockFrame = block.getBoundingBox();
                                    for (Text.Line line : block.getLines()) {
                                        String lineText = line.getText();
                                        Point[] lineCornerPoints = line.getCornerPoints();
                                        Rect lineFrame = line.getBoundingBox();
                                        for (Text.Element element : line.getElements()) {
                                            String elementText = element.getText();
                                            Point[] elementCornerPoints = element.getCornerPoints();
                                            Rect elementFrame = element.getBoundingBox();
                                        }
                                    }
                                }

                                textResult = resultText;
                                Log.i("Logs", textResult);
                                Traslator tras = new Traslator(textResult, "es", txtTraduccion);
                                tras.possibleLanguageIdentifier();
                                txtPais.setText(textResult);

                            }
                        })
                        .addOnFailureListener(
                                new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        // Task failed with an exception
                                        // ...
                                    }
                                });

    }

    public void buscar () {
        String url = "http://www.geognos.com/api/en/countries/info/all.json";
        Log.i("Logs", url);
        RequestQueue queue = Volley.newRequestQueue(this);
        JsonObjectRequest jsonRespuesta = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                new com.android.volley.Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i("Logs", response.toString());
                        JsonObject json = Methods.stringToJSON(response.toString());
                        json = Methods.JsonToSubJSON(json, "Results");
                        if (json.size() > 0) {
                            Set<String> keys = json.keySet();
                            for (String key : keys) {
                                JsonObject jsonPro = Methods.JsonToSubJSON(json, key);
                                String countryName = txtTraduccion.getText().toString();
                                if (Methods.JsonToString(jsonPro, "Name", "").toLowerCase().equals(countryName.toLowerCase())) {
                                    //JsonCountry = jso;
                                    Log.i("Logs", "KEY => "+key);
                                    Log.i("Logs", "PAIS A BUSCAR => " + countryName);
                                    Log.i("Logs", "JSON ENCONTRADO => "+jsonPro.toString());

                                    jsonResponse = jsonPro.toString();

                                    Intent intent = new Intent(MainActivity.this, MapaPro.class);
                                    Bundle b = new Bundle();
                                    b.putString("DataPais", jsonResponse);
                                    intent.putExtras(b);
                                    startActivity(intent);
                                }
                            }
                        }
                    }
                },
                new com.android.volley.Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("Error", error.getMessage());
                    }
                }
        );
        queue.add(jsonRespuesta);
    }

}