package com.asif.filemanager.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.text.format.Formatter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.asif.filemanager.DatabaseHelper;
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
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HomeFragment extends Fragment implements OnFileSelectedListener {

    private FileAdapter fileAdapter;
    private List<File> fileList;

    String[] items = {"Details", "Rename", "Copy", "Paste", "Delete", "Share"};

    private File selectedFile;
    View view;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_home, container, false);

        LinearLayout linearImages = view.findViewById(R.id.linearImages);
        LinearLayout linearVideos = view.findViewById(R.id.linearVideos);
        LinearLayout linearMusics = view.findViewById(R.id.linearMusics);
        LinearLayout linearDocs = view.findViewById(R.id.linearDocs);
        LinearLayout linearDownloads = view.findViewById(R.id.linearDownloads);
        LinearLayout linearApks = view.findViewById(R.id.linearApks);

        linearImages.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "image");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        linearVideos.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "video");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        linearMusics.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "music");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        linearDocs.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "docs/pdf");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        linearDownloads.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "downloads");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        linearApks.setOnClickListener(view -> {
            Bundle args = new Bundle();
            args.putString("fileType", "apk");
            CategorizedFragment categorizedFragment = new CategorizedFragment();
            categorizedFragment.setArguments(args);

            getFragmentManager().beginTransaction().add(R.id.fragment_container, categorizedFragment).addToBackStack(null).commit();

        });
        runtimePermission();

        // Insert file information into the database when the Home page is accessed
        insertFilesIntoDatabase();

        return view;
    }

    // Insert file information into the database when the Home page is accessed
    private void insertFilesIntoDatabase() {
        if (isStoragePermissionGranted()) {
            DatabaseHelper dbHelper = new DatabaseHelper(getContext());
            if (dbHelper.isEmpty()) {
                ArrayList<File> files = findFiles(Environment.getExternalStorageDirectory());
                for (File file : files) {
                    String name = file.getName();
                    String path = file.getAbsolutePath();
                    String type = getFileType(name);
                    long size = file.length();
                    long lastModified = file.lastModified();

                    dbHelper.insertFile(name, path, type, size, lastModified);
                }
            }
        }
    }

    private boolean isStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    //Determination of the file type based on the file name
    private String getFileType(String fileName) {
        String fileExtension = getFileExtension(fileName);
        if (fileExtension != null) {
            switch (fileExtension.toLowerCase()) {
                case "jpg":
                case "jpeg":
                case "png":
                case "gif":
                    return "image";
                case "mp4":
                case "avi":
                case "mkv":
                    return "video";
                case "mp3":
                case "wav":
                case "ogg":
                    return "music";
                case "pdf":
                case "doc":
                case "docx":
                case "txt":
                    return "document";
                case "apk":
                    return "application";
                default:
                    return "unknown";
            }
        }

        return "unknown";
    }
    private String getFileExtension(String fileName) {
        int dotIndex = fileName.lastIndexOf('.');
        if (dotIndex != -1 && dotIndex < fileName.length() - 1) {
            return fileName.substring(dotIndex + 1);
        }
        return null;
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


    public ArrayList<File> findFiles(@NonNull File file)
    {
        ArrayList<File> arrayList = new ArrayList<>();
        File[] files = file.listFiles();

        if (files != null) {
            for (File singleFile : files) {
                if (singleFile.isDirectory() && !singleFile.isHidden()) {
                    arrayList.addAll(findFiles(singleFile));
                }else if (singleFile.getName().toLowerCase().endsWith(".jpeg") ||
                        singleFile.getName().toLowerCase().endsWith(".jpg") ||
                        singleFile.getName().toLowerCase().endsWith(".png") ||
                        singleFile.getName().toLowerCase().endsWith(".heic") ||

                        singleFile.getName().toLowerCase().endsWith(".mp3") ||
                        singleFile.getName().toLowerCase().endsWith(".mp4") ||
                        singleFile.getName().toLowerCase().endsWith(".mkv") ||

                        singleFile.getName().toLowerCase().endsWith(".pdf") ||
                        singleFile.getName().toLowerCase().endsWith(".epub") ||
                        singleFile.getName().toLowerCase().endsWith(".doc") ||
                        singleFile.getName().toLowerCase().endsWith(".docx") ||
                        singleFile.getName().toLowerCase().endsWith(".txt") ||
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
        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        List<DatabaseHelper.FileModel> fileModels = dbHelper.getAllFiles();

        RecyclerView recyclerView = view.findViewById(R.id.recycler_recent);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));

        fileList = new ArrayList<>();

        for (DatabaseHelper.FileModel fileModel : fileModels) {
            File file = new File(fileModel.getPath());
            fileList.add(file);
        }

        Collections.sort(fileList, (file1, file2) -> Long.compare(file2.lastModified(), file1.lastModified()));

        // Get only the first 20 files
        fileList = new ArrayList<>(fileList.subList(0, Math.min(fileList.size(), 20)));
        // Create the adapter and set it to the RecyclerView
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
        ListView options = optionDialog.findViewById(R.id.List);
        CustomAdapter customAdapter = new CustomAdapter();
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
                    Uri fileUri = FileProvider.getUriForFile(getContext(), "com.asif.fileManager.fileProvider", file);
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
