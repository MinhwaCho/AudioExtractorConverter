package com.example.audioextractor;

import android.util.Log;

import com.arthenica.ffmpegkit.FFmpegKit;
import com.arthenica.ffmpegkit.FFmpegSession;
import com.arthenica.ffmpegkit.ReturnCode;

import java.io.File;
import java.io.IOException;

public class Ffmpegkit {

    private static final String TAG = "FFPMEG_FAILURE";
    public void useFfmpeg(String input, String output){

        FFmpegSession session = null;
        //"-i '/storage/emulated/0/iu.mp4' -c:v mpeg4 '"+f.getCanonicalPath()+"/file3.mp4'"
        //"-i '/storage/emulated/0/iu.mp3' -acodec pcm_s16le -ac 1 -ar 16000 '"+f.getCanonicalPath()+"/iu.wav'" //mp3 to target wav
        //"-i '/storage/emulated/0/iu.mp4' -f mp3 -ab 192000 -vn '"+f.getCanonicalPath()+"/iu.mp3'" //mp4 to mp3
        //"-i '/storage/emulated/0/iu.mp4' -ac 2 -f wav '"+f.getCanonicalPath()+"/iu2.wav'" //mp4 to wav
        //"-t 30 -i "+f.getCanonicalPath()+"'/iump42mp3.mp3' -acodec copy "+f.getCanonicalPath()+"'/iucut.mp3'" //0~30sec
        //session = FFmpegKit.execute("-i '/storage/emulated/0/iu.mp4' -f mp3 -ab 192000 -vn '"+f.getCanonicalPath()+"/iump42mp3.mp3'");
        //"-i '/storage/emulated/0/iu.mp4' -acodec pcm_s16le -ac 1 -ar 16000 '"+f.getCanonicalPath()+"/iump42wavstraight.wav'" //mp4 to target wav

        session = FFmpegKit.execute("-i '"+input+"' -acodec pcm_s16le -ac 1 -ar 16000 '"+output+"'");

        if (ReturnCode.isSuccess(session.getReturnCode())) {

            // SUCCESS

        } else if (ReturnCode.isCancel(session.getReturnCode())) {

            // CANCEL

        } else {

            // FAILURE
            Log.d(TAG, String.format("Command failed with state %s and rc %s.%s", session.getState(), session.getReturnCode(), session.getFailStackTrace()));

        }
    }
}
