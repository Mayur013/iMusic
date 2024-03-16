package com.example.imusic;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.FileDescriptor;
import java.io.IOException;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
        ListView listView;
        String[] items;
   static ArrayList<AudioData> songs;
   static Bitmap image[];


    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView=findViewById(R.id.listView);
        runtimePermission();

    }
    public void runtimePermission(){
       Dexter.withContext(this).withPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
               .withListener(new PermissionListener() {
                   @Override
                   public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
                        DisplaySongs();

                   }

                   @Override
                   public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {
                        closeNow();
                   }

                   @Override
                   public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                   }
               }).check();
    }




    void DisplaySongs(){
        songs=loadAudio(MainActivity.this);
        image=new Bitmap[songs.size()];
        items=new String[songs.size()];

        for(int i=0;i<songs.size();i++){
            image[i]=getAlbumart(Long.parseLong(songs.get(i).getAlbumId()));

            items[i]=songs.get(i).getName().toString().replace(".mp3" , "");
            songs.get(i).setName(items[i]);

        }


        CustomAdapter customAdapter=new CustomAdapter();
        listView.setAdapter(customAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent=new Intent(MainActivity.this,PlayerActivity.class);
               // intent.putExtra("name",songs.get(position).getName().toString());
//                intent.putExtra("image",image[position]);
               // intent.putExtra("view",view);
                intent.putExtra("position",position);

                startActivity(intent);
            }
        });

    }




    private ArrayList<AudioData> loadAudio(Context context){
        ArrayList<AudioData> tmpList=new ArrayList<>();
        Uri uri= MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
        String[] projection={
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.ALBUM_ID

        };
        Cursor cursor=context.getContentResolver().query(uri,projection,null,null,null);
        if(cursor!=null){
            while(cursor.moveToNext()){
                Uri uri1;
                tmpList.add(new AudioData(
                        cursor.getString(0),
                        cursor.getString(1),
                        cursor.getString(2)

                ));

            }
            cursor.close();
        }
        return tmpList;
    }

    class CustomAdapter extends BaseAdapter{



        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View view = getLayoutInflater().inflate(R.layout.list_item, null);
            TextView textsong = view.findViewById(R.id.txtsongname);
            ImageView imageView = view.findViewById(R.id.imgsong);
            textsong.setSelected(true);
            textsong.setText(items[position]);

                Glide.with(MainActivity.this).asBitmap().load(image[position]).error(R.drawable.pngegg).into(imageView);


            return view;
        }
    }


public Bitmap getAlbumart(long album_id) {
    Bitmap bm = null;
    Uri uri = null;
    try
    {
        final Uri sArtworkUri = Uri
                .parse("content://media/external/audio/albumart");

         uri = ContentUris.withAppendedId(sArtworkUri, album_id);

        ParcelFileDescriptor pfd = this.getContentResolver()
                .openFileDescriptor(uri, "r");

        if (pfd != null)
        {
            FileDescriptor fd = pfd.getFileDescriptor();
            bm = BitmapFactory.decodeFileDescriptor(fd);
        }
    } catch (Exception e) {
    }
    return bm;
}
private void closeNow() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
        finishAffinity();
    } else {
        finish();
    }
}



}