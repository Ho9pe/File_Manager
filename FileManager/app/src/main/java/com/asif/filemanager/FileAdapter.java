package com.asif.filemanager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileViewHolder> {
    private Context context;
    private List<File> file;
    private OnFileSelectedListener listener;


    public FileAdapter(Context context, List<File> file,OnFileSelectedListener listener) {
        this.context = context;
        this.file = file;
        this.listener = listener;
    }

    @NonNull
    @Override
    public FileViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new FileViewHolder(LayoutInflater.from(context).inflate(R.layout.file_container, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull FileViewHolder holder, @SuppressLint("RecyclerView") int position) {

        holder.tvName.setText(file.get(position).getName());
        holder.tvName.setSelected(true);
        int items = 0;
        if(file.get(position).isDirectory()){
            File[] files = file.get(position).listFiles();
            for (File singleFile : files){
                if(!singleFile.isHidden()){
                    items+=1;
                }
            }
            holder.tvSize.setText(String.valueOf(items)+ " Files");

        }
        else{
            holder.tvSize.setText(Formatter.formatShortFileSize(context, file.get(position).length()));
        }
//image
        if(file.get(position).getName().toLowerCase().endsWith(".jpeg")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".jpg")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".png")){
            holder.imgFile.setImageResource(R.drawable.ic_image);
        }
//pdf doc apk
        else if(file.get(position).getName().toLowerCase().endsWith(".doc")){
            holder.imgFile.setImageResource(R.drawable.ic_docs);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".pdf")){
            holder.imgFile.setImageResource(R.drawable.ic_pdf);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".epub")){
            holder.imgFile.setImageResource(R.drawable.ic_epub);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".apk")){
            holder.imgFile.setImageResource(R.drawable.ic_android);
        }
//music video
        else if(file.get(position).getName().toLowerCase().endsWith(".mp3")){
            holder.imgFile.setImageResource(R.drawable.ic_music);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".mp4")){
            holder.imgFile.setImageResource(R.drawable.ic_player);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".mkv")){
            holder.imgFile.setImageResource(R.drawable.ic_player);
        }
//compressed files
        else if(file.get(position).getName().toLowerCase().endsWith(".7z")){
            holder.imgFile.setImageResource(R.drawable.ic_7z);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".rar")){
            holder.imgFile.setImageResource(R.drawable.ic_rar);
        }
        else if(file.get(position).getName().toLowerCase().endsWith(".zip")){
            holder.imgFile.setImageResource(R.drawable.ic_zip);
        }
        else{
            holder.imgFile.setImageResource(R.drawable.ic_folder);
        }
        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onFileClicked(file.get(holder.getAdapterPosition()));
            }
        });
        holder.container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                listener.onFileLongClicked(file.get(holder.getAdapterPosition()), position);
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return file.size();
    }
}
