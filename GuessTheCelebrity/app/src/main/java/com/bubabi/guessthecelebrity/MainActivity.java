package com.bubabi.guessthecelebrity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> celebURLs = new ArrayList<>();
    ArrayList<String> celebNames = new ArrayList<>();
    int chosenCeleb = 0;
    int locationOfCorrectAnswer = 0;
    String[] answers = new String[4];
    ImageView imageView;
    Button button0; Button button1; Button button2; Button button3;

    public void celebChosen(View view) throws ExecutionException, InterruptedException {

        if(view.getTag().toString().equals(Integer.toString(locationOfCorrectAnswer))){

            Toast.makeText(getApplicationContext(), "Correct!", Toast.LENGTH_SHORT).show();

        } else{

            Toast.makeText(getApplicationContext(), "Wrong! It was " + celebNames.get(chosenCeleb), Toast.LENGTH_SHORT).show();

        }

        createNewQuestion();


    }


    public static class ImageDownloader extends AsyncTask<String, Void, Bitmap>{

        @Override
        protected Bitmap doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);

                HttpURLConnection connection = (HttpURLConnection) url.openConnection();

                connection.connect();

                InputStream inputStream = connection.getInputStream();
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                return bitmap;


            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public static class DownloadTask extends AsyncTask<String, Void, String>{

        private static final String TAG = "DownloadTask";

        @Override
        protected String doInBackground(String... urls) {

            String result = "";
            URL url;
            HttpURLConnection httpURLConnection = null;

            try{
                url = new URL(urls[0]);
                Log.i(TAG, "doInBackground: " + urls[0]);

                httpURLConnection = (HttpURLConnection) url.openConnection();

                InputStream in = httpURLConnection.getInputStream();

                InputStreamReader reader = new InputStreamReader(in);

                int data = reader.read();

                while(data != -1){
                    char current = (char) data;
                    result += current;
                    data = reader.read();
                }

                return result;
            }
            catch(Exception e){
                e.printStackTrace();
            }

            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        button0 = findViewById(R.id.button0);
        button1 = findViewById(R.id.button1);
        button2 = findViewById(R.id.button2);
        button3 = findViewById(R.id.button3);

        DownloadTask downloadTask = new DownloadTask();
        String result = null;

        try {
            result = downloadTask.execute("http://www.posh24.se/kandisar").get();

            String[] splitResult = result.split("<div class=\"sidebarContainer\">");

            // finding URLs using by regex

            Pattern p = Pattern.compile("<img src=\"(.*?)\"");
            Matcher m = p.matcher(splitResult[0]);

            while(m.find()){
                celebURLs.add(m.group(1));
            }

            // finding names using by regex

            p = Pattern.compile("alt=\"(.*?)\"");
            m = p.matcher(splitResult[0]);

            while(m.find()){
                celebNames.add(m.group(1));
            }

            createNewQuestion();


        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

    public void createNewQuestion() throws ExecutionException, InterruptedException {
        // creating random number for user
        Random random = new Random();
        chosenCeleb = random.nextInt(celebURLs.size());

        ImageDownloader imageDownloader = new ImageDownloader();
        Bitmap celebImage;
        celebImage = imageDownloader.execute(celebURLs.get(chosenCeleb)).get();

        imageView.setImageBitmap(celebImage);

        locationOfCorrectAnswer = random.nextInt(4);

        int incorrectAnswerLocation;

        for(int i=0; i<4; i++){

            if(i == locationOfCorrectAnswer){

                answers[i] = celebNames.get(chosenCeleb);

            } else{

                incorrectAnswerLocation = random.nextInt(celebURLs.size());

                while(incorrectAnswerLocation == chosenCeleb){

                    incorrectAnswerLocation = random.nextInt(celebURLs.size());

                }

                answers[i] = celebNames.get(incorrectAnswerLocation);

            }
        }

        button0.setText(answers[0]);
        button1.setText(answers[1]);
        button2.setText(answers[2]);
        button3.setText(answers[3]);



    }

}
