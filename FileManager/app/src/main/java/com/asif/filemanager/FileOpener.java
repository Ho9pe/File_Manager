package com.asif.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileOpener {
    public static void openFile(Context context, File file) throws IOException {

        File selectedFile = file;
        //Uri uri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName() + ".provider", file);
        Uri uri = FileProvider.getUriForFile(context, "com.asif.fileManager.fileProvider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        if(uri.toString().toLowerCase().contains(".doc")){
            intent.setDataAndType(uri, "application/msword");
        }
        else if(uri.toString().toLowerCase().contains(".pdf") || uri.toString().toLowerCase().contains(".epub")){
            intent.setDataAndType(uri, "application/pdf");
        }
        else if(uri.toString().toLowerCase().contains(".mp3")){
            intent.setDataAndType(uri, "audio/x-wav");
        }
        else if(uri.toString().toLowerCase().contains(".mp4") || uri.toString().toLowerCase().contains(".mpv")){
            intent.setDataAndType(uri, "video/*");
        }
        else if(uri.toString().toLowerCase().contains(".jpeg") || uri.toString().toLowerCase().contains(".jpg") || uri.toString().toLowerCase().contains(".png")){
            intent.setDataAndType(uri, "image/jpeg");
        }
        else if(uri.toString().toLowerCase().contains(".7z") || uri.toString().toLowerCase().contains(".zip")){
            intent.setDataAndType(uri, "application/zip");
        }
        else if(uri.toString().toLowerCase().contains(".rar")){
            intent.setDataAndType(uri, "application/x-rar-compressed");
        }
        else{
            intent.setDataAndType(uri, "*/*");
        }
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        context.startActivity(intent);
    }
}
