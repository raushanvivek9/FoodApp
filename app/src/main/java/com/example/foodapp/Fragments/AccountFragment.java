package com.example.foodapp.Fragments;


import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.foodapp.R;

import com.example.foodapp.user.SeeOrderDetails;
import com.example.foodapp.user.User_login;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
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
import com.squareup.picasso.Picasso;


import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;


public class AccountFragment extends Fragment {
    private View account_view;
    CircleImageView profile_image;
    ImageView imageView;
    TextView holder_name,a_email_id,u_phone;
    TextView logout,order_detail;
    private FirebaseAuth mAuth;
    private static final int IMAGE_REQUEST=1;
    private FirebaseUser currentuser;
    Uri imageUri;
    private FirebaseStorage storage;
    private DatabaseReference myRef,tref;
    private StorageReference storageReference;
    private StorageTask uploadTask;
    private ProgressDialog loadingbar;
    private  String userid;
    private FirebaseAuth.AuthStateListener authStateListener;


    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        account_view=inflater.inflate(R.layout.fragment_account,container,false);
//        Toast.makeText(getActivity(),"user"+userid,Toast.LENGTH_LONG).show();
        profile_image=account_view.findViewById(R.id.profile_image);
        logout=account_view.findViewById(R.id.logout);
        holder_name=account_view.findViewById(R.id.holder_name);
        a_email_id=account_view.findViewById(R.id.a_email_id);
        u_phone=account_view.findViewById(R.id.u_phone);
        order_detail=account_view.findViewById(R.id.order_detail);
        mAuth=FirebaseAuth.getInstance();
        currentuser=mAuth.getCurrentUser();
        myRef= FirebaseDatabase.getInstance().getReference("Users").child(currentuser.getUid());
        storageReference= FirebaseStorage.getInstance().getReference().child("profile images");
        loadingbar=new ProgressDialog(getActivity());

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.exists() && (dataSnapshot.hasChild("name") && (dataSnapshot.hasChild("image"))))
                        {
                             String retrivename=dataSnapshot.child("name").getValue().toString();
                            String retriveemail=dataSnapshot.child("username").getValue().toString();
                            String retriveprofileimage=dataSnapshot.child("image").getValue().toString();
                            String retrivephone=dataSnapshot.child("phone_no").getValue().toString();
                            holder_name.setText(retrivename);
                            a_email_id.setText(retriveemail);
                            u_phone.setText(retrivephone);
                            Picasso.get().load(retriveprofileimage).into(profile_image);


                        }
                        else if(dataSnapshot.exists() && (dataSnapshot.hasChild("name")))
                        {
                            String retrivename=dataSnapshot.child("name").getValue().toString();
                            String retriveemail=dataSnapshot.child("username").getValue().toString();
                            String retrivephone=dataSnapshot.child("phone_no").getValue().toString();
                            holder_name.setText(retrivename);
                            a_email_id.setText(retriveemail);
                            u_phone.setText(retrivephone);

                        }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        profile_image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent gallery=new Intent();
                gallery.setAction(Intent.ACTION_GET_CONTENT);
                gallery.setType("image/*");
                startActivityForResult(gallery,IMAGE_REQUEST);
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tref=FirebaseDatabase.getInstance().getReference().child("Users").child(currentuser.getUid());
                tref.child("device_Token").setValue("");
                mAuth.getInstance().signOut();
                Intent i=new Intent(getContext(), User_login.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
                getActivity().finish();
            }
        });
        order_detail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(getContext(), SeeOrderDetails.class);
                i.putExtra("userid",userid);
                startActivity(i);
            }
        });

//        return null;
        return  account_view;
//
    }


    private String getFileExtension(Uri uri){
        ContentResolver contentResolver=getContext().getContentResolver();
        MimeTypeMap mimeTypeMap=MimeTypeMap.getSingleton();
        return  mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }
    private void uploadImage(){
        loadingbar.setTitle("set profile image");
        loadingbar.setMessage("please wait, your profile image is updating");
        loadingbar.setCanceledOnTouchOutside(false);
        loadingbar.show();

        if(imageUri!=null){
            final StorageReference fileReference=storageReference.child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            uploadTask=fileReference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot,Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if(!task.isSuccessful()){
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    if(task.isSuccessful()){
                        Uri downloadUri=task.getResult();
                        String mUri=downloadUri.toString();
                        myRef.child("image")
                        .setValue(mUri);
                        loadingbar.dismiss();

                    }else{
                        String message=task.getException().toString();
                        Toast.makeText(getActivity(),""+message,Toast.LENGTH_LONG).show();
                        loadingbar.dismiss();
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getActivity(),e.getMessage(),Toast.LENGTH_SHORT).show();
                    loadingbar.dismiss();
                }
            });
        }else {
            Toast.makeText(getActivity(),"No Image selected",Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==IMAGE_REQUEST && resultCode == RESULT_OK && data!=null &&data.getData()!=null){
            imageUri=data.getData();

            if(uploadTask!=null&& uploadTask.isInProgress())
            {
                Toast.makeText(getActivity(),"upload in progress",Toast.LENGTH_SHORT).show();
            }
            else
            {
                uploadImage();
            }
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        FirebaseAuth.getInstance().addAuthStateListener(authStateListener);
//    }
//
//    @Override
//    public void onStop() {
//        super.onStop();
//        if(authStateListener!=null){
//            FirebaseAuth.getInstance().removeAuthStateListener(authStateListener);
//        }
//    }
}
