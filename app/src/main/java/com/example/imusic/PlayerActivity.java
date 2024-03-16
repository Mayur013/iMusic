package com.example.imusic;

import static com.example.imusic.MainActivity.image;
import static com.example.imusic.MainActivity.songs;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PorterDuff;
import android.media.MediaPlayer;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


public class PlayerActivity extends AppCompatActivity {

    ImageView rewind,play,previous,next,forward;
    //Button play;
    TextView sngname,sngstart1,sngend;
    SeekBar seekBar;
    ImageView imageView;

    static MediaPlayer mediaPlayer;
    String nname;
    Bitmap iimages;
    int pposition;
    Thread updateSeekbar;

    static ArrayList<AudioData> myFiles=new ArrayList<AudioData>();


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home){
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player);

        getSupportActionBar().setTitle("Now Playing");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        rewind=findViewById(R.id.rewind);
        previous=findViewById(R.id.previous);
        play=findViewById(R.id.play);
        next=findViewById(R.id.next);
        forward=findViewById(R.id.forward);
        seekBar=findViewById(R.id.seekBar2);
        sngname=findViewById(R.id.sngname);
        sngstart1=findViewById(R.id.sngstart1);
        sngend=findViewById(R.id.sngend);
        imageView=findViewById(R.id.imageView);


        myFiles=songs;
        Intent intent=getIntent();
        pposition=intent.getIntExtra("position",0);

        //nname=intent.getStringExtra("name");
        nname=myFiles.get(pposition).getName().toString();

        //iimages=(Bitmap)intent.getParcelableExtra("image");

        sngname.setSelected(true);
        sngname.setText(nname);
        Glide.with(getApplicationContext()).asBitmap().load(image[pposition]).error(R.drawable.ooppp).into(imageView);


        Uri uri = null;
        if(myFiles!=null) {
            uri = Uri.parse(myFiles.get(pposition).getPath());
        }
        if(mediaPlayer!=null){
            mediaPlayer.stop();
            mediaPlayer.release();
           // Toast.makeText(this, "clear", Toast.LENGTH_SHORT).show();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }
        else{
            //Toast.makeText(this, "hmmm", Toast.LENGTH_SHORT).show();
            mediaPlayer=MediaPlayer.create(getApplicationContext(),uri);
            mediaPlayer.start();
        }

//        Timer timer = new Timer();
//        timer.scheduleAtFixedRate(new TimerTask() {
//            @Override
//            public void run() {
//                seekBar.setProgress(mediaPlayer.getCurrentPosition());
//            }
//        },0,10);





        play.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if(mediaPlayer.isPlaying()){
                mediaPlayer.pause();
                //play.setBackgroundResource(R.drawable.play);
                play.setImageResource(R.drawable.play);
            }
            else{
                mediaPlayer.start();
                //play.setBackgroundResource(R.drawable.pause);
                play.setImageResource(R.drawable.pause);
            }
        }
    });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                pposition=((pposition+1)%myFiles.size());
                Uri u=Uri.parse(myFiles.get(pposition).getPath().toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                nname=myFiles.get(pposition).getName();
                sngname.setText(nname);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                startAnimation(imageView);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                String endTime=createTime(mediaPlayer.getDuration());
                sngend.setText(endTime);
                //updateSeekbar.start();
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.stop();
                mediaPlayer.release();
                pposition=(pposition-1)<0?(myFiles.size()-1):(pposition-1);
                Uri u=Uri.parse(myFiles.get(pposition).getPath().toString());
                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
                nname=myFiles.get(pposition).getName();
                sngname.setText(nname);
                mediaPlayer.start();
                play.setImageResource(R.drawable.pause);
                startAnimation(imageView);
                seekBar.setMax(mediaPlayer.getDuration());
                seekBar.setProgress(0);
                //updateSeekbar.start();
                String endTime=createTime(mediaPlayer.getDuration());
                sngend.setText(endTime);
            }
        });

//        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                mediaPlayer.stop();
//                mediaPlayer.release();
//                pposition=((pposition+1)%myFiles.size());
//                Uri u=Uri.parse(myFiles.get(pposition).getPath().toString());
//                mediaPlayer=MediaPlayer.create(getApplicationContext(),u);
//                nname=myFiles.get(pposition).getName();
//                sngname.setText(nname);
//                mediaPlayer.start();
//                play.setImageResource(R.drawable.pause);
//                startAnimation(imageView);
//                seekBar.setMax(mediaPlayer.getDuration());
//                seekBar.setProgress(0);
//                String endTime=createTime(mediaPlayer.getDuration());
//                sngend.setText(endTime);
//
//            }
//        });

        rewind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mediaPlayer.seekTo(mediaPlayer.getCurrentPosition()-10000);
                //seekBar.setProgress(mediaPlayer.getCurrentPosition()-10000);
            }
        });
        forward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mediaPlayer.getCurrentPosition()+10000<= mediaPlayer.getDuration()) {
                    mediaPlayer.seekTo(mediaPlayer.getCurrentPosition() + 10000);
                }
                else{
                    mediaPlayer.seekTo(mediaPlayer.getDuration()-1);
                }
                //seekBar.setProgress(mediaPlayer.getCurrentPosition()+10000);

            }
        });



        updateSeekbar=new Thread(){
            @Override
            public void run() {
                super.run();
                int totalDuration=mediaPlayer.getDuration();
                int currentDuration=0;//mediaPlayer.getCurrentPosition();
                while(currentDuration<totalDuration){
                    try {
                        sleep(500);
                        currentDuration=mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentDuration);
                    }
                    catch (InterruptedException | IllegalStateException e){
                        e.printStackTrace();
                    }
                }

            }
        };
        seekBar.setMax(mediaPlayer.getDuration());
        updateSeekbar.start();
        seekBar.getProgressDrawable().setColorFilter(getResources().getColor(R.color.purple_500), PorterDuff.Mode.MULTIPLY);
        seekBar.getThumb().setColorFilter(getResources().getColor(R.color.purple_700),PorterDuff.Mode.SRC_IN);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        String endTime=createTime(mediaPlayer.getDuration());
        sngend.setText(endTime);
        final Handler handler=new Handler();
        final int delay=1000;
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                String cur=createTime(mediaPlayer.getCurrentPosition());
                sngstart1.setText(cur);
                handler.postDelayed(this,delay);
            }
        },delay);


    }
    public void startAnimation(View view){
        ObjectAnimator animator=ObjectAnimator.ofFloat(imageView,"rotation",0f,360f);
        animator.setDuration(1000);
        AnimatorSet animatorSet=new AnimatorSet();
        animatorSet.playTogether(animator);
        animatorSet.start();
    }
    public String createTime(int duration){
        String time="";
        int min=duration/1000/60;
        int sec=duration/1000%60;
        time+=min+":";
        if(sec<10){
            time+="0";
        }
        time+=sec;
        return  time;
    }


}