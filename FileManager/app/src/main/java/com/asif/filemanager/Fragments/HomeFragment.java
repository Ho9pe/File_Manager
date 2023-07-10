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
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
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
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment implements OnFileSelectedListener {

    private RecyclerView recyclerView;
    private FileAdapter fileAdapter;
    private List<File> fileList;
    private LinearLayout linearImages, linearVideos, linearMusics, linearDocs, linearDownloads, linearApks;

    File storage;
    String data;
    String[] items = {"Details", "Rename", "Delete", "Share"};
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        linearImages = view.findViewById(R.id.linearImages);
        linearVideos = view.findViewById(R.id.linearVideos);
        linearMusics = view.findViewById(R.id.linearMusics);
        linearDocs = view.findViewById(R.id.linearDocs);
        linearDownloads = view.findViewById(R.id.linearDownloads);
        linearApks = view.findViewById(R.id.linearApks);

        linearImages.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "image");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
        linearVideos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "video");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
        linearMusics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "music");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
        linearDocs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "docs/pdf");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
        linearDownloads.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "downloads");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
        linearApks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Bundle args = new Bundle();
                args.putString("fileType", "apk");
                CatagorizedFragment catagorizedFragment = new CatagorizedFragment();
                catagorizedFragment.setArguments(args);

                getFragmentManager().beginTransaction().add(R.id.fragment_container, catagorizedFragment).addToBackStack(null).commit();

            }
        });
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


    public ArrayList<File> findFiles(File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findFiles(singleFile));
                } else if (singleFile.getName().toLowerCase().endsWith(".jpeg") ||
                        singleFile.getName().toLowerCase().endsWith(".jpg") ||
                        singleFile.getName().toLowerCase().endsWith(".png") ||
                        singleFile.getName().toLowerCase().endsWith(".mp3") ||
                        singleFile.getName().toLowerCase().endsWith(".mp4") ||
                        singleFile.getName().toLowerCase().endsWith(".mkv") ||
                        singleFile.getName().toLowerCase().endsWith(".pdf") ||
                        singleFile.getName().toLowerCase().endsWith(".doc") ||
                        singleFile.getName().toLowerCase().endsWith(".apk") ||
                        singleFile.getName().toLowerCase().endsWith(".7z") ||
                        singleFile.getName().toLowerCase().endsWith(".rar") ||
                        singleFile.getName().toLowerCase().endsWith(".zip")) {
                    arrayList.add(singleFile);
                }
            }
            arrayList.sort(Comparator.comparing(File::lastModified).reversed());
        }
        return arrayList;
    }

    private void displayFiles() {
        recyclerView = view.findViewById(R.id.recycler_recent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(Environment.getExternalStorageDirectory()));
        fileList = new ArrayList<>(fileList.subList(0, Math.min(fileList.size(), 10))); // Get only the first 10 files
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }


    @Override
    public void onFileClicked(File file) {
        if(file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            InternalFragment internalFragment = new InternalFragment();
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
                                optionDialog.cancel();
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
                                String extention = file.getAbsolutePath().substring(file.getAbsolutePath().lastIndexOf("."));
                                File current = new File(file.getAbsolutePath());
                                File destination = new File(file.getAbsolutePath().replace(file.getName(), new_name) + extention);

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
                                optionDialog.cancel();
                            }
                        });
                        AlertDialog alertdialog_rename = renameDialog.create();
                        alertdialog_rename.show();
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
                                optionDialog.cancel();
                            }
                        });

                        AlertDialog alertDialog_delete = deleteDialog.create();
                        alertDialog_delete.show();
                        break;

                    case "Share":
                        String fileName = file.getName();
                        Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileprovider", file);
                        Intent shareIntent = new Intent(Intent.ACTION_SEND);
                        shareIntent.setType(getMimeType(fileUri));
                        shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        startActivity(Intent.createChooser(shareIntent, "Share " + fileName));
                        break;
                }
            }
        });

    }


    class CustomAdapter extends BaseAdapter {

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
