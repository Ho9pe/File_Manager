package com.asif.filemanager.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asif.filemanager.FileAdapter;
import com.asif.filemanager.FileOpener;
import com.asif.filemanager.OnFileSelectedListener;
import com.asif.filemanager.R;
import com.google.android.material.navigation.NavigationView;
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
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class CardFragment extends Fragment implements OnFileSelectedListener {

    private FileAdapter fileAdapter;
    private List<File> fileList;
    File storage;
    String data;
    String[] items = {"Details", "Rename", "Copy", "Paste", "Delete", "Share"};
    private File selectedFile;

    String secStorage;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_card, container, false);

        TextView tv_pathHolder = view.findViewById(R.id.tv_pathHolder);
        ImageView img_back = view.findViewById(R.id.img_back);

        File[] externalCacheDirs = getContext().getExternalCacheDirs();
        boolean hasExternalStorage = false;
        for (File file : externalCacheDirs) {
            if (Environment.isExternalStorageRemovable(file)) {
                secStorage = file.getPath().split("/Android")[0];
                hasExternalStorage = true;
                break;
            }
        }


        if (hasExternalStorage) {
            storage = new File(secStorage);
            tv_pathHolder.setText(storage.getAbsolutePath());
            runtimePermission();
        } else {
            showSDCardUnavailableDialog();
        }

        try {
            data = getArguments().getString("path");
            File file = new File(data);
            storage = file;
        }catch (Exception e){
            e.printStackTrace();
        }


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

    private boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        return state.equals(Environment.MEDIA_MOUNTED);
    }

    private void showSDCardUnavailableDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("SD Card Unavailable");
        builder.setMessage("This device does not have an SD card or SD card access is not available.");
        builder.setPositiveButton("OK", (dialogInterface, i) -> {
            // Handle OK button click
            dialogInterface.dismiss();
            // Navigate to HomeFragment
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            fragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, new HomeFragment())
                    .commit();
            // Set "Home" item as selected in the navigation menu
            NavigationView navigationView = requireActivity().findViewById(R.id.nav_view);
            Menu menu = navigationView.getMenu();
            MenuItem homeItem = menu.findItem(R.id.nav_home);
            homeItem.setChecked(true);
        });
        builder.show();
    }

    public ArrayList<File> findFiles(@NonNull File file) {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.add(singleFile);
                }
            }
            for (File singleFile : files) {
                if (singleFile.getName().toLowerCase().endsWith(".jpeg") ||
                        singleFile.getName().toLowerCase().endsWith(".jpg") ||
                        singleFile.getName().toLowerCase().endsWith(".png") ||

                        singleFile.getName().toLowerCase().endsWith(".mp3") ||
                        singleFile.getName().toLowerCase().endsWith(".mp4") ||
                        singleFile.getName().toLowerCase().endsWith(".mkv") ||

                        singleFile.getName().toLowerCase().endsWith(".pdf") ||
                        singleFile.getName().toLowerCase().endsWith(".epub") ||
                        singleFile.getName().toLowerCase().endsWith(".doc") ||
                        singleFile.getName().toLowerCase().endsWith(".apk") ||

                        singleFile.getName().toLowerCase().endsWith(".7z") ||
                        singleFile.getName().toLowerCase().endsWith(".rar") ||
                        singleFile.getName().toLowerCase().endsWith(".zip")) {
                    arrayList.add(singleFile);
                }
            }

        }
        return arrayList;
    }
    private void displayFiles() {
        RecyclerView recyclerView = view.findViewById(R.id.recycler_internal);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        fileList = new ArrayList<>();
        fileList.addAll(findFiles(storage));
        fileAdapter = new FileAdapter(getContext(), fileList, this);
        recyclerView.setAdapter(fileAdapter);
    }

    @Override
    public void onFileClicked(@NonNull File file) {
        if(file.isDirectory()){
            Bundle bundle = new Bundle();
            bundle.putString("path", file.getAbsolutePath());
            CardFragment internalFragment = new CardFragment();
            internalFragment.setArguments(bundle);
            getFragmentManager().beginTransaction().replace(R.id.fragment_container, internalFragment).addToBackStack(null).commit();

        }
        else {
            try {
                if (isSDCardAvailable()) {
                    FileOpener.openFile(getContext(), file);
                } else {
                    Toast.makeText(getContext(), "SD card access is not available", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                Toast.makeText(getContext(), "Failed to open file", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
            } catch (IllegalArgumentException e) {
                Toast.makeText(getContext(), "File type not supported", Toast.LENGTH_SHORT).show();
                e.printStackTrace();
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
    public void onFileLongClicked(File file, int position) {
        final Dialog optionDialog = new Dialog(getContext());
        optionDialog.setContentView(R.layout.option_dialog);
        optionDialog.setTitle("Select Options.");
        ListView options = optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter(items);
        options.setAdapter(customAdapter);
        optionDialog.show();

        options.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedItem = adapterView.getItemAtPosition(i).toString();

            switch (selectedItem){
                case "Details":
                    AlertDialog.Builder detailDialog = new AlertDialog.Builder(getContext());
                    detailDialog.setTitle("Details");
                    final TextView details = new TextView(getContext());
                    detailDialog.setView(details);
                    Date lastModified = new Date(file.lastModified());
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.US);
                    String formattedDate = formatter.format(lastModified);

                    String detailsText = getString(R.string.file_details, file.getName(),
                            Formatter.formatShortFileSize(getContext(), file.length()),
                            file.getAbsolutePath(),
                            formattedDate);

                    details.setText(detailsText);
                    detailDialog.setPositiveButton("Ok", (dialogInterface, i1) -> dialogInterface.dismiss());

                    AlertDialog alertDialog_details = detailDialog.create();
                    alertDialog_details.show();
                    break;

                case "Rename":
                    AlertDialog.Builder renameDialog = new AlertDialog.Builder(getContext());
                    renameDialog.setTitle("Rename File: ");
                    final EditText name = new EditText(getContext());
                    renameDialog.setView(name);

                    renameDialog.setPositiveButton("OK", (dialogInterface, i12) -> {
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
                    });

                    renameDialog.setNegativeButton("Cancel", (dialogInterface, i13) -> dialogInterface.dismiss());
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
                    deleteDialog.setPositiveButton("Yes", (dialogInterface, i14) -> {
                        if (file.delete()) {
                            fileList.remove(position);
                            fileAdapter.notifyItemRemoved(position);
                            Toast.makeText(getContext(), "Deleted!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to delete the file.", Toast.LENGTH_SHORT).show();
                        }
                    });
                    deleteDialog.setNegativeButton("No", (dialogInterface, i15) -> dialogInterface.dismiss());

                    AlertDialog alertDialog_delete = deleteDialog.create();
                    alertDialog_delete.show();
                    break;

                case "Share":
                    String fileName = file.getName();
                    Uri fileUri = FileProvider.getUriForFile(getContext(), getContext().getPackageName() + ".fileProvider", file);
                    Intent shareIntent = new Intent(Intent.ACTION_SEND);
                    shareIntent.setType(getMimeType(fileUri));
                    shareIntent.putExtra(Intent.EXTRA_STREAM, fileUri);
                    shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    startActivity(Intent.createChooser(shareIntent, "Share " + fileName));
                    break;
            }
            optionDialog.dismiss();
        });

    }

    static class CustomAdapter extends BaseAdapter{

        private final String[] items;

        public CustomAdapter(String[] items) {
            this.items = items;
        }

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
            if (view == null) {
                view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.option_layout, viewGroup, false);
            }
            TextView txtOptions = view.findViewById(R.id.txtOption);
            ImageView imgOptions = view.findViewById(R.id.imgOption);

            txtOptions.setText(items[i]);

            switch (items[i]) {
                case "Details":
                    imgOptions.setImageResource(R.drawable.ic_details);
                    break;
                case "Rename":
                    imgOptions.setImageResource(R.drawable.ic_rename);
                    break;
                case "Copy":
                    imgOptions.setImageResource(R.drawable.ic_copy);
                    break;
                case "Paste":
                    imgOptions.setImageResource(R.drawable.ic_paste);
                    break;
                case "Delete":
                    imgOptions.setImageResource(R.drawable.ic_delete);
                    break;
                case "Share":
                    imgOptions.setImageResource(R.drawable.ic_share);
                    break;
            }
            return view;
        }
    }

}

