package com.example.dosribaar;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.dosribaar.databinding.ActivityUpdateProfilePicBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Date;

public class UpdateProfilePic extends AppCompatActivity {

    Uri uri;
    String CurrentImagePath;
    ActivityUpdateProfilePicBinding binding;
    int req = 101;
    FirebaseStorage storage;
    String ID;
    FirebaseDatabase database;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        binding = ActivityUpdateProfilePicBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        database = FirebaseDatabase.getInstance();
        ID = getIntent().getStringExtra("ID");
        dialog = new ProgressDialog(this);
        storage = FirebaseStorage.getInstance();

        binding.addimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent, 10);
            }
        });

        binding.cameraOpen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                askCameraPermission();
            }
        });


        binding.nextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (binding.coverPhoto.getVisibility() == View.GONE) {
                    binding.coverPhoto.setVisibility(View.VISIBLE);
                    binding.diagonalView.setVisibility(View.VISIBLE);
                    binding.nextBtn.setBackgroundDrawable(ContextCompat.getDrawable(UpdateProfilePic.this, R.drawable.nextbtnnot));
                    binding.nextBtn.setTextColor(getResources().getColor(R.color.black));
                    binding.nextBtn.setEnabled(false);
                    binding.TextUpload.setText("Upload Cover Photo");

                    final StorageReference storageReference = storage.getReference().child("ProfilePhoto").child(ID);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Profile Photo Upload", Toast.LENGTH_SHORT).show();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("User").child(ID).child("ProfilePhoto").setValue(uri.toString());

                                }
                            });
                        }
                    });


                } else {
                    dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
                    dialog.setTitle("Setting Up Your Profile");
                    dialog.setMessage("Please Wait.......");
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();


                    final StorageReference storageReference = storage.getReference().child("CoverPhoto").child(ID);
                    storageReference.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            Toast.makeText(getApplicationContext(), "Cover Photo Upload", Toast.LENGTH_SHORT).show();
                            storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    database.getReference().child("User").child(ID).child("CoverPhoto").setValue(uri.toString());
                                    dialog.dismiss();

                                }
                            });
                        }
                    });

                    Intent intent = new Intent(UpdateProfilePic.this, MainActivity.class);
                    intent.putExtra("ID", ID);
                    startActivity(intent);
                }

            }


        });

    }

    private void askCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) this, new String[]{Manifest.permission.CAMERA}, req);
            CaptureImage();
        } else {
            CaptureImage();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == req) {
            if (grantResults.length < 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 10) {
            if (data.getData() != null) {
                uri = data.getData();
                if (binding.coverPhoto.getVisibility() == View.VISIBLE) {
                    binding.coverPhoto.setImageURI(uri);
                } else {
                    binding.profileImage.setImageURI(uri);
                }
                binding.nextBtn.setBackgroundDrawable(ContextCompat.getDrawable(UpdateProfilePic.this, R.drawable.nextbtn));
                binding.nextBtn.setTextColor(getResources().getColor(R.color.white));
                binding.nextBtn.setEnabled(true);

            }
        }
        if (requestCode == 102) {
            uri = Uri.fromFile(new File(CurrentImagePath));
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(CurrentImagePath);
                binding.nextBtn.setBackgroundDrawable(ContextCompat.getDrawable(UpdateProfilePic.this, R.drawable.nextbtn));
                binding.nextBtn.setTextColor(getResources().getColor(R.color.white));
                binding.nextBtn.setEnabled(true);
                setPic();
            }
        }
    }

    private void setPic() {
        int targetW;
        int targetH;

        if (binding.coverPhoto.getVisibility() == View.VISIBLE) {
            targetW = binding.profileImage.getWidth();
            targetH = binding.profileImage.getHeight();
        } else {
            targetW = binding.coverPhoto.getWidth();
            targetH = binding.coverPhoto.getHeight();
        }

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(CurrentImagePath, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int scaleFactor = Math.max(1, Math.min(photoW / targetW, photoH / targetH));

        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap bitmap = BitmapFactory.decodeFile(CurrentImagePath, bmOptions);
        if (binding.profileImage.getVisibility() == View.VISIBLE) {
            binding.profileImage.setImageBitmap(bitmap);
        } else {
            binding.coverPhoto.setImageBitmap(bitmap);
        }

    }

    private File createImagefile() throws IOException {
        String timeStamp = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        }
        String imageName = "jpg_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File imageFile = File.createTempFile(imageName, ".jpg", storageDir);
        CurrentImagePath = imageFile.getAbsolutePath();
        return imageFile;

    }

    public void CaptureImage() {

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        File imageFile = null;

        try {
            imageFile = createImagefile();

        } catch (IOException e) {
            e.printStackTrace();
        }
        if (imageFile != null) {
            Uri imageUri = FileProvider.getUriForFile(this, "com.example.dosribaar.provider", imageFile);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            startActivityForResult(cameraIntent, 102);
        }
    }


}