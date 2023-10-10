package com.asif.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private final Context context;
    private final List<File> fileList;
    private List<File> filteredList;
    private final OnFileSelectedListener listener;


    public FileAdapter(Context context, List<File> fileList, OnFileSelectedListener listener) {
        this.context = context;
        this.fileList = fileList;
        this.filteredList = fileList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvName.setText(fileList.get(position).getName());
        holder.tvName.setSelected(true);

        if (fileList.get(position).isDirectory()) {
            File[] files = fileList.get(position).listFiles();

            if (files != null) {
                int items = 0;
                for (File singleFile : files) {
                    if (!singleFile.isHidden()) {
                        items += 1;
                    }
                }
                holder.tvSize.setText(items+ " Files");
            } else {
                holder.tvSize.setText("0 Files");
            }
        } else {
            holder.tvSize.setText(Formatter.formatShortFileSize(context, fileList.get(position).length()));
        }
//jpeg jpg png heic
        if(fileList.get(position).getName().toLowerCase().endsWith(".jpeg")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".jpg")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".png")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".heic")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
//pdf doc docx txt epub apk
        else if(fileList.get(position).getName().toLowerCase().endsWith(".doc")){
            holder.imgFile.setImageResource(R.drawable.ic_docs);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".docx")){
            holder.imgFile.setImageResource(R.drawable.ic_docs);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".txt")){
            holder.imgFile.setImageResource(R.drawable.ic_docs);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".pdf")){
            holder.imgFile.setImageResource(R.drawable.ic_pdf);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".epub")){
            holder.imgFile.setImageResource(R.drawable.ic_epub);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".apk")){
            holder.imgFile.setImageResource(R.drawable.ic_android);
        }
//mp3 mp4 mkv
        else if(fileList.get(position).getName().toLowerCase().endsWith(".mp3")){
            holder.imgFile.setImageResource(R.drawable.ic_music);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".mp4")){
            holder.imgFile.setImageResource(R.drawable.ic_player);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".mkv")){
            holder.imgFile.setImageResource(R.drawable.ic_player);
        }
//7z rar zip
        else if(fileList.get(position).getName().toLowerCase().endsWith(".7z")){
            holder.imgFile.setImageResource(R.drawable.ic_7z);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".rar")){
            holder.imgFile.setImageResource(R.drawable.ic_rar);
        }
        else if(fileList.get(position).getName().toLowerCase().endsWith(".zip")){
            holder.imgFile.setImageResource(R.drawable.ic_zip);
        }
        else{
            holder.imgFile.setImageResource(R.drawable.ic_folder);
        }
        holder.container.setOnClickListener(view -> listener.onFileClicked(fileList.get(holder.getAdapterPosition())));
        holder.container.setOnLongClickListener(view -> {
            listener.onFileLongClicked(fileList.get(holder.getAdapterPosition()), position);
            return true;
        });

    }

    @Override
    public int getItemCount() {
        return fileList.size();
    }

    public void updateData(List<File> updatedList) {
        this.fileList.clear();
        this.fileList.addAll(updatedList);
        notifyDataSetChanged();
    }
}
