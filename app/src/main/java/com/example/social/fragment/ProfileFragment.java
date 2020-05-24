package com.example.social.fragment;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.social.R;
import com.example.social.databinding.ProfileEditDialogBinding;
import com.example.social.databinding.ProfileFragmentBinding;
import com.example.social.model.messaging.Contact;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

public class ProfileFragment extends Fragment implements View.OnClickListener{
    private ProfileFragmentBinding profileFragmentBinding;
    private FirebaseUser firebaseUser;
    private DatabaseReference databaseReference;
    private StorageReference storageReference;
    private static final int IMAGE_REQUEST = 1;
    private Uri imageUri;
    private StorageTask uploadTask;
    private BottomSheetDialog bottomSheetDialog;

    public ProfileFragment() {
        // Required empty public constructor
    }

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        this.profileFragmentBinding = DataBindingUtil.inflate(inflater,R.layout.fragment_profile, container, false);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());
        profileFragmentBinding.layoutUserProfile.setOnClickListener(this);
        storageReference = FirebaseStorage.getInstance().getReference("uploads");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Contact contact = dataSnapshot.getValue(Contact.class);
                if (getContext() != null) {
                    if (contact != null) {
                        profileFragmentBinding.profileUserName.setText(contact.getUsername());
                        profileFragmentBinding.profileUserDescription.setText(contact.getDescription());
                        if (contact.getImageURL().equals("default"))
                            profileFragmentBinding.imageViewProfile.setImageResource(R.drawable.application_logo_black);
                         else {
                            Glide.with(getContext()).load(contact.getImageURL())
                                    .into(profileFragmentBinding.imageViewProfile);
                                }
                        }
                    }
                }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                //empty method
            }
        });
        return this.profileFragmentBinding.getRoot();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.layout_user_profile:
                initializeBottomSheetDialog();
                break;
            case R.id.layout_view_profile_picture:
                //todo : view profile picture
                break;
            case R.id.layout_upload_profile_picture:
                openImageFromGallery();
                break;
            case R.id.layout_edit_description:
                //todo: description edit
                break;
        }
    }

    private void openImageFromGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,IMAGE_REQUEST);
    }

    private String getFileExtension(Uri uri){
        if (getContext() != null) {
            ContentResolver resolver = getContext().getContentResolver();
            MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
            return mimeTypeMap.getExtensionFromMimeType(resolver.getType(uri));
        } return "";
    }

    private void uploadImage(){
        final ProgressDialog dialog = new ProgressDialog(getContext());
        dialog.setMessage("Uploading..");
        dialog.show();
        if (imageUri != null){
            final StorageReference fileReference = storageReference.child(System.currentTimeMillis()
                +"."+getFileExtension(imageUri));
            uploadTask = fileReference.putFile(imageUri);
            uploadTask.continueWithTask((Continuation<UploadTask.TaskSnapshot, Task<Uri>>) task -> {
                if (!task.isSuccessful()) {
                    throw Objects.requireNonNull(task.getException());
                }
                return fileReference.getDownloadUrl();
            }).addOnCompleteListener((OnCompleteListener<Uri>) task -> {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    if (downloadUri != null) {
                        String uriFile = downloadUri.toString();
                        databaseReference = FirebaseDatabase.getInstance().getReference("Users")
                                .child(firebaseUser.getUid());
                        HashMap<String, Object> hashMap = new HashMap<>();
                        hashMap.put("imageURL", uriFile);
                        databaseReference.updateChildren(hashMap);
                        dialog.dismiss();
                    } else {
                        Toast.makeText(getContext(), "Failed !", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                    }
                }
            }).addOnFailureListener(e -> Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show());
        } else {
            Toast.makeText(getContext(),"No image Selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == IMAGE_REQUEST && resultCode == Activity.RESULT_OK
                && data != null && data.getData() != null){
            imageUri = data.getData();
            if (uploadTask != null && uploadTask.isInProgress()){
                Toast.makeText(getContext(), "Upload in progress", Toast.LENGTH_SHORT).show();
            } else {
                uploadImage();
            }
        }
    }

    private void initializeBottomSheetDialog(){
        if (getContext() != null) {
           if (bottomSheetDialog == null)
               bottomSheetDialog = new BottomSheetDialog(getContext());
            ProfileEditDialogBinding profileEditDialogBinding = DataBindingUtil.
                    inflate(LayoutInflater.from(getContext()), R.layout.profile_picture_dialog, null, false);
            profileEditDialogBinding.layoutEditDescription.setOnClickListener(this);
            profileEditDialogBinding.layoutUploadProfilePicture.setOnClickListener(this);
            profileEditDialogBinding.layoutViewProfilePicture.setOnClickListener(this);
            bottomSheetDialog.setContentView(profileEditDialogBinding.getRoot());
            bottomSheetDialog.show();
        }
    }
}
