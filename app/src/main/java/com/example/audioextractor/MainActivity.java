package com.example.audioextractor;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class MainActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //how to set
         /*1. build.gradle(project)
          repositories {
              mavenCentral()
          }*/

        /* 2. build.gradle(app)
            dependencies {
                implementation 'com.arthenica:ffmpeg-kit-full:4.4.LTS'
           }*/



        // permission
        requestPermission();


        // 바꿀 Video
        File input = new File(Environment.getExternalStorageDirectory(), "chicken.mp4");
        // 추출된 Audio file name 정하기
        String audio = "/chicken.wav";
        // 추출된 Audio file을 담을 cache directory name
        String dir = "Test";
        // extract Audio Stream(wav < 16bit 16kHz mono >) from Video(mp4)
        extractAudio(input,audio,dir);

        // wav 파일 쪼개기
        // 1. wav -> 10sec씩 쪼개기 (파일명은 1.pcm - 2.pcm - ...)
        File wav = new File(getCacheDir(),dir+"/"+audio);   // wav 파일 경로
        splitWav2Pcm(wav,dir,10);
        // 2. 필요한 상황이면 pcm -> wav
//        File t = new File(getCacheDir(),"Test/8.pcm");
//        makeCacheFile("Test/8.wav");
//        File tt = new File(getCacheDir(),"Test/8.wav");
//        try {
//            new Main().rawToWave(t,tt);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }

    }

    private void requestPermission(){
        Util.requestPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE);
        Util.requestPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void makeCacheFile(String name) {
        File storage = getCacheDir();
        String filename = name;
        File tempFile = new File(storage,filename);

        try {
            FileOutputStream out = new FileOutputStream(tempFile);
            // File 내용 쓰기기
            out.close();
            Log.d("FILE WRITE", "FILE PATH: " + storage + "/" + filename);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void splitWav2Pcm(File wavFile, String dir, int sec){
        FileInputStream in = null;
        FileOutputStream out = null;
        int channels = 1;
        long byteRate = 16 * 16000 * channels/8;
        int splitunit = (int)(sec*byteRate);
        String newFileName = null;
        String newFilePath = null;

        try{
            in = new FileInputStream(wavFile);

            byte[] part = new byte[splitunit];
            int avg = (int)(wavFile.length()-44)/splitunit;	// 길이를 정해진 시간단위로 잘라서 몇개인지 알아내기
            int count = 0;
            int fosize=0;
            in.read(part,0,44); // header(44) 크기만큼 읽기
            while((count++)!=avg) {
                in.read(part,0,splitunit);	// part에 저장
                fosize+=splitunit;
                // file 만들고
                newFileName = "/"+Integer.toString(count)+".pcm";
                newFilePath = dir+newFileName;
                makeCacheFile(newFilePath);
                // part 배열을 만든 file에 저장
                out = new FileOutputStream(new File(getCacheDir(),newFilePath));
                out.write(part);
            }
            // 나머지 데이터(int)(wavFile.length()-44)-fosize
            //int n = in.read(part,0,avg-fosize);
            int remain = in.read(part,0,(int)(wavFile.length()-44)-fosize);
            newFileName = "/"+Integer.toString(count)+".pcm";
            newFilePath = dir+newFileName;
            makeCacheFile(newFilePath);
            out = new FileOutputStream(new File(getCacheDir(),newFilePath));
            out.write(part,0, remain);


            in.close();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }// FileInputStream.read


    }
    private String makeFolder(String folder_name) throws IOException {
        File f = new File(getCacheDir(), folder_name);  // path = data/data/cache/NewFolder
        Log.d("NewFolderName",f.getCanonicalPath());
        if (!f.exists()) {
            f.mkdirs();
        }
        return f.getCanonicalPath();
    }
    private void extractAudio(File in, String name, String folderName){
        String input = null;
        String output = null;
        String f = null;
        try {
            f = makeFolder(folderName);   // cache에 folder 만든 후 folder의 경로 얻기 (새로 생길 wav 파일을 저장할 공간)
            input = in.getCanonicalPath();   // mp4파일 경로
            output = f+name;  // 새롭게 생길 wav파일 경로 (/data/data/cache/NewFolder/*.wav)

            new Ffmpegkit().useFfmpeg(input,output);    // FFmpeg 명령어 사용하여 전환
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}