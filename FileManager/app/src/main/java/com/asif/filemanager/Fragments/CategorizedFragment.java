package com.asif.filemanager.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.viewmodel.CreationExtras;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asif.filemanager.FileAdapter;
import com.asif.filemanager.FileOpener;
import com.asif.filemanager.OnFileSelectedListener;
import com.asif.filemanager.R;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class CategorizedFragment extends Fragment implements OnFileSelectedListener {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    File storage;
    String data;
    String[] items = {"Details", "Rename", "Copy", "Paste", "Delete", "Share"};

    private File selectedFile;
    File path;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_categorized, container, false);

        path = Environment.getExternalStorageDirectory();

        runtimePermission();

        return view;
    }

    private void runtimePermission() {
        Dexter.withContext(getContext()).withPermissions(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(new MultiplePermissionsListener() {
            @Override
            public void onPermissionsChecked(MultiplePermissionsReport multiplePermissionsReport) {
                displayFiles();
            }

            @Override
            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> list, PermissionToken permissionToken) {
                permissionToken.continuePermissionRequest();
            }
        }).check();
    }


    public ArrayList<File> findFiles(@NonNull File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();
        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findFiles(singleFile));
                } else {
                    String fileType = getArguments().getString("fileType");
                    switch (fileType) {
                        case "image":
                            if (isImageFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "video":
                            if (isVideoFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "music":
                            if (isMusicFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "docs/pdf":
                            if (isPdfOrDocFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "apk":
                            if (isApkFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                        case "downloads":
                            if (isDownloadFile(singleFile)) {
                                arrayList.add(singleFile);
                            }
                            break;
                    }
                }
            }
        }
        return arrayList;
    }

    private boolean isImageFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg") || extension.equalsIgnoreCase("png");
    }

    private boolean isVideoFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mkv");
    }

    private boolean isMusicFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("mp3");
    }

    private boolean isPdfOrDocFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("doc");
    }

    private boolean isApkFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("apk");
    }

    private boolean isDownloadFile(File file) {
        String extension = getFileExtension(file);
        return extension.equalsIgnoreCase("jpeg") || extension.equalsIgnoreCase("jpg") ||
                extension.equalsIgnoreCase("png") || extension.equalsIgnoreCase("mp3") ||
                extension.equalsIgnoreCase("mp4") || extension.equalsIgnoreCase("mkv") ||
                extension.equalsIgnoreCase("pdf") || extension.equalsIgnoreCase("doc") ||
                extension.equalsIgnoreCase("apk") || extension.equalsIgnoreCase("7z") ||
                extension.equalsIgnoreCase("rar") || extension.equalsIgnoreCase("zip") ||
                extension.equalsIgnoreCase("epub");
    }

    private String getFileExtension(File file) {
        String name = file.getName();
        int lastDotIndex = name.lastIndexOf(".");
        if (lastDotIndex > 0 && lastDotIndex < name.length() - 1) {
            return name.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }

    public ArrayList<File> findDownloadedFiles() {
        ArrayList<File> downloadedFiles = new ArrayList<>();
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File[] files = downloadDir.listFiles();

        if (files != null) {
            Arrays.sort(files, (file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));

            // Add the last 60 downloaded files
            int count = 0;
            for (File file : files) {
                if (count >= 60) {
                    break;
                }

                if (file.isFile()) {
                    downloadedFiles.add(file);
                    count++;
                }
            }
        }

        return downloadedFiles;
    }
    String fileType;
    private void displayFiles() {
            recyclerView = view.findViewById(R.id.recycler_internal);
            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
            fileList = new ArrayList<>();
            Bundle bundle = this.getArguments();
            fileType = bundle.getString("fileType");

            if (fileType.equals("downloads")) {
                fileList.addAll(findDownloadedFiles());
            } else {
                fileList.addAll(findFiles(Environment.getExternalStorageDirectory()));
            }
            fileList.sort(Comparator.comparingLong(File::lastModified).reversed());
            if(fileType != "downloads") {
                fileList = new ArrayList<>(fileList.subList(0, Math.min(fileList.size(), 20)));
            }
            fileAdapter = new FileAdapter(getContext(), fileList, this);
            recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(File file) {
        if(file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            CategorizedFragment internalFragment = new CategorizedFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        }
        else{
            try {
                FileOpener.openFile(getContext(), file);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

    }

    private String getMimeType(Uri uri) {
        ContentResolver resolver = getContext().getContentResolver();
        return resolver.getType(uri);
    }

    private boolean copyFile(File sourceFile, File destinationFile) {
        try {
            FileInputStream inputStream = new FileInputStream(sourceFile);
            FileOutputStream outputStream = new FileOutputStream(destinationFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
            inputStream.close();
            outputStream.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options = (ListView) optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
        options.setAdapter(customAdapter);
        optionDialog.show();
        options.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String selectedItem = adapterView.getItemAtPosition(i).toString();

                switch (selectedItem){
                    case "Details":
                        AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                        detailDialog.setTitle("Details");
                        final TextView details = new TextView(getContext());
                        detailDialog.setView(details);
                        Date lastModified = new Date(file.lastModified());
                        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                        String formattedDate = formatter.format(lastModified);

                        details.setText("File Name: " + file.getName()+ "\n" +
                                "Size: " + Formatter.formatShortFileSize(getContext(), file.length()) + "\n" +
                                "Path: " + file.getAbsolutePath() + "\n" +
                                "Last Modified: " + formattedDate);
                        detailDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog alertDialog_details = detailDialog.create();
                        alertDialog_details.show();
                        break;

                    case "Rename":
                        AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                        renameDialog.setTitle("Rename File: ");
                        final EditText name = new EditText(getContext());
                        renameDialog.setView(name);

                        renameDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                String new_name = name.getEditableText().toString();
                                String extension = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extension);

                                if(current.renameTo(destination)){
                                    fileList.set(position, destination);
                                    fileAdapter.notifyItemChanged(position);
                                    Toast.makeText(getContext(), "Renamed!", Toast.LENGTH_SHORT).show();
                                }
                                else{
                                    Toast.makeText(getContext(), "Couldn't Rename!", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        renameDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });
                        AlertDialog alertdialog_rename = renameDialog.create();
                        alertdialog_rename.show();
                        break;

                    case "Copy":
                        selectedFile = file;
                        Toast.makeText(getContext(), "File copied", Toast.LENGTH_SHORT).show();
                        optionDialog.dismiss();
                        break;

                    case "Paste":
                        if (selectedFile != null) {
                            File destinationDir = new File(file.getAbsolutePath());
                            if (destinationDir.exists() && destinationDir.isDirectory()) {
                                File newFile = new File(destinationDir, selectedFile.getName());
                                if (copyFile(selectedFile, newFile)) {
                                    fileList.add(newFile);
                                    fileAdapter.notifyItemInserted(fileList.size() - 1);
                                    Toast.makeText(getContext(), "File pasted", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(getContext(), "Failed to paste file", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(getContext(), "Invalid destination folder", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(getContext(), "No file to paste", Toast.LENGTH_SHORT).show();
                        }
                        optionDialog.dismiss();
                        break;

                    case "Delete":
                        AlertDialog.Builder deleteDialog = new AlertDialog.Builder(getContext());
                        deleteDialog.setTitle("DELETE "+ file.getName()+ "?");
                        deleteDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                file.delete();
                                fileList.remove(position);
                                fileAdapter.notifyDataSetChanged();
                                Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        deleteDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.dismiss();
                            }
                        });

                        AlertDialog alertDialog_delete = deleteDialog.create();
                        alertDialog_delete.show();
                        break;

                    case "Share":
                        String fileName = file.getName();
                        Uri fileUri = FileProvider.getUriForFile(getContext(), "com.asif.fileManager.fileProvider", file);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType(getMimeType(fileUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share " + fileName));
                        break;
                }
                optionDialog.dismiss();
            }
        });

    }

    class CustomAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return items.length;
        }

        @Override
        public Object getItem(int i) {
            return items[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            View myView = getLayoutInflater().inflate(R.layout.option_layout, null);
            TextView txtOptions = myView.findViewById(R.id.txtOption);
            ImageView imgOptions = myView.findViewById(R.id.imgOption);
            txtOptions.setText(items[i]);
            if(items[i].equals("Details")){
                imgOptions.setImageResource(R.drawable.ic_details);
            }
            else if(items[i].equals("Rename")){
                imgOptions.setImageResource(R.drawable.ic_rename);
            }
            else if(items[i].equals("Copy")){
                imgOptions.setImageResource(R.drawable.ic_copy);
            }
            else if(items[i].equals("Paste")){
                imgOptions.setImageResource(R.drawable.ic_paste);
            }
            else if(items[i].equals("Delete")){
                imgOptions.setImageResource(R.drawable.ic_delete);
            }
            else if(items[i].equals("Share")){
                imgOptions.setImageResource(R.drawable.ic_share);
            }
            return myView;
        }
    }

}

