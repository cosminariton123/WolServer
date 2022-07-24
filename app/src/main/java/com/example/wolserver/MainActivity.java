package com.example.wolserver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Toast;



public class MainActivity extends AppCompatActivity {
    WolServer wolServer = new WolServer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


                String message;

                message = startServer();
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                while (true) {
                    message = listenForClients();
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                }


    }

    public String startServer(){
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                try {
                    wolServer.startServer(Config.wolPort);
                    message = "Server pornit";
                }
                catch (java.io.IOException e){
                    message = "Eroare la pornirea serverului";
                }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    public String listenForClients() {
        NetworkWorker networkWorker = new NetworkWorker() {
            @Override
            public void run() {
                    try {
                        wolServer.listenForClient();
                    } catch (java.io.IOException e) {
                        message = "Eroare de comunicare";
                        message = e.getMessage();

                    }
            }
        };

        startSeparateThreadToDoDirtyWorkAndWait(networkWorker);
        return networkWorker.getMessage();
    }

    private void startSeparateThreadToDoDirtyWorkAndWait(NetworkWorker networkWorker) {
        Thread thread = new Thread(networkWorker);
        thread.start();

        try {
            thread.join();
        }
        catch (InterruptedException e){
            Toast.makeText(getApplicationContext(), "Thread intrerupt din motive necunoscute!", Toast.LENGTH_SHORT).show();
        }
    }

}