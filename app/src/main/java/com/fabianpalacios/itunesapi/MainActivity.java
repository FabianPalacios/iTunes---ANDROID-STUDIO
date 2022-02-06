package com.fabianpalacios.itunesapi;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.fabianpalacios.itunesapi.Service.model.AppleMusicService;
import com.fabianpalacios.itunesapi.Service.model.Result;


import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import cafsoft.foundation.HTTPURLResponse;
import cafsoft.foundation.URLSession;


public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer = null;

    private EditText txtSearch= null;
    private Button btnSearch = null;

    private ListView listViewItems = null;
    private List<Result> results = null;

    private AppleMusicService service = null;
    int REQUEST_CODE = 200;
    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        verificarPermisos();


        initViews();
        initEvents();

        service = new AppleMusicService();


    }

    //Metodo para pedir permisos al usuario
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void verificarPermisos(){
        int permisoAlmacenamiento = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
         if ( permisoAlmacenamiento == PackageManager.PERMISSION_GRANTED){
             Toast.makeText(this, "Permiso Almacenamiento ", Toast.LENGTH_SHORT).show();
         } else{
             requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},REQUEST_CODE);
         }
    }

    //Inicializar los elementos de la view
    public  void initViews(){
        txtSearch = findViewById(R.id.txtSearch);
        listViewItems = findViewById(R.id.listViewItems);
    }

    //Metodo para hacer la busqueda
    public void btnGetInfoOnClick(View view){
        getMusicInfo(txtSearch.getText().toString());
    }


    public void initEvents(){
        listViewItems.setOnItemClickListener((adapterView, view, i, l) -> {

            CustomListAdapter.ViewHolder viewHolder = new CustomListAdapter.ViewHolder(view);
            Result song = (Result) listViewItems.getAdapter().getItem(i);

            String destFilename = this.getCacheDir() + "/" + song.getTrackId() + ".tmp.m4a";
            int state = song.getState();
            switch (state){
                case 1:
                    try{
                        downloadFile(new URL(song.getPreviewUrl()), destFilename);
                        song.setState(2);
                        viewHolder.imgAction.setImageResource(R.drawable.play);
                    } catch (MalformedURLException e) {
                        e.printStackTrace();
                    }
                    break;
                case 2:
                    mediaPlayer = MediaPlayer.create(this, Uri.parse(destFilename));
                    mediaPlayer.start();
                    song.setState(3);
                    viewHolder.imgAction.setImageResource(R.drawable.pause);
                    break;
                case 3:
                    mediaPlayer.stop();
                    song.setState(2);
                    viewHolder.imgAction.setImageResource(R.drawable.play);
                    break;
            }
        });
        mediaPlayer = new MediaPlayer();
    }

    //Se obtiene la lista de canciones con respecto a la busqueda y se la lista.
    public void getMusicInfo(String name) {
        results = new ArrayList<>();
        service.searchSongsByTerm(name,(isNetworkError, statusCode, root) -> {
            if (!isNetworkError) {
                if (statusCode == 200) {

                    for (Result e:  root.getResults()){
                        results.add(new Result(e.getTrackId(),e.getArtistName(),e.getTrackName(), e.getPreviewUrl(), e.getArtworkUrl100()));
                    }
                    runOnUiThread(() -> {
                        CustomListAdapter adapter = new CustomListAdapter(this, results);
                        listViewItems.setAdapter(adapter);
                    });
                } else {
                    Log.d("iTunes", "Service error");
                }
            } else {
                Log.d("Super Hero", "Network error");
            }
        });
    }


    //Descarga la canción que viene en una URL y le pone el nombre destFilename
    public void downloadFile(URL audioURL, String destFilename){
        URLSession.getShared().downloadTask(audioURL, (localAudioUrl, response, error) -> {

            if (error == null) {
                int respCode = ((HTTPURLResponse) response).getStatusCode();

                if (respCode == 200) {
                    File file = new File(localAudioUrl.getFile());
                    if (file.renameTo(new File(destFilename))) {
                        mediaPlayer = MediaPlayer.create(this, Uri.parse(destFilename));
                        //mediaPlayer.start();
                    }
                }
                else{
                    // Error (respCode)
                }
            }else {
                // Connection error
            }
        }).resume();
    }
}