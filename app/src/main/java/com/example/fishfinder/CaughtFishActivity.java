package com.example.fishfinder;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;


import com.example.fishfinder.data.GeneralTest;
import com.example.fishfinder.data.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;


public class CaughtFishActivity extends AppCompatActivity {

    private EditText edtSaveTitle;
    private EditText edtSaveFishName;
    private ImageButton imgButtonTakePicture;
    private Button btnSubmitSave;
    private EditText edtSaveLatitude;
    private EditText edtSaveLongitude;

    private EditText edtSaveWeight;
    private EditText edtSaveLength;
    private EditText edtSaveSpecies;
    private EditText edtSaveGenus;
    private EditText edtSaveBait;
    private EditText edtSaveBodyShape;
    private EditText edtSaveUserComments;



    private final int TAKE_PICTURE = 9990;

    private String latitudeVal;
    private String longitudeVal;

    FirebaseDatabase firebase;

    FirebaseAuth firebaseAuth;
    FirebaseUser firebaseUser;
    String userId;
    String userEmail;



    private Bitmap bitMapToSave;
    private boolean imageTaken;

    private int databaseSize;
    private int fishImageStorageSize;

    private boolean successfulUpload;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_caught_fish);

        edtSaveTitle = (EditText) findViewById(R.id.edtSaveTitle);
        edtSaveFishName = (EditText) findViewById(R.id.edtSaveFishName);
        imgButtonTakePicture = (ImageButton) findViewById(R.id.imgButtonTakePicture);
        btnSubmitSave = (Button) findViewById(R.id.btnSubmitSave);
        edtSaveWeight = (EditText) findViewById(R.id.edtSaveWeight);
        edtSaveLength = (EditText) findViewById(R.id.edtSaveLength);
        edtSaveSpecies = (EditText) findViewById(R.id.edtSaveSpecies);
        edtSaveGenus = (EditText) findViewById(R.id.edtSaveGenus);
        edtSaveBait = (EditText) findViewById(R.id.edtSaveBait);
        edtSaveBodyShape = (EditText) findViewById(R.id.edtSaveBodyShape);
        edtSaveUserComments = (EditText) findViewById(R.id.edtSaveUserComments);



        edtSaveLatitude = (EditText) findViewById(R.id.edtSaveLatitude);
        edtSaveLongitude = (EditText) findViewById(R.id.edtSaveLongitude);


        firebase = FirebaseDatabase.getInstance(); //get the root node point of the database, this is so we can get the references based on the root node to get the desired data references

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser(); //get the current user based on the auth

        userId = firebaseUser.getUid().toString();
        userEmail = firebaseUser.getEmail().toString(); //just get some data we need

        FirebaseStorage storage = FirebaseStorage.getInstance(); //get our firebase storage instance
        StorageReference storageRef = storage.getReference(); // get a reference to our storage at firebase
        StorageReference userFishImagesRef = storageRef.child("UserFishImages" + "/"); //set the directory to reference a node called UserFishImages

        //get the fishImageStorageSize which is updated everytime a user uploads an fishimage as a submission
        //https://stackoverflow.com/questions/52823473/how-to-count-files-image-from-firebase-storage says theres no way to do it with an api call so this has to be done instead
        firebase.getReference("GeneralDatabaseData/UserFishImagesCount").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                fishImageStorageSize = dataSnapshot.getValue(Integer.class); //get the integer from this reference
                Toast.makeText(CaughtFishActivity.this, "" + fishImageStorageSize, Toast.LENGTH_SHORT).show(); //check if it really retrieved this data.
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        //this is used to grab the count for user uploaded records
        firebase.getReference("GeneralTest").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    databaseSize = (int) dataSnapshot.getChildrenCount(); //grab the count for the children.
                }
                else {
                    //if that database does not exist
                    databaseSize = 0; //init the database size to be 0 so we can use it as an id.
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });




        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if(extras == null) {
                latitudeVal = "";
                longitudeVal = "";

            } else {
                longitudeVal = extras.getString("latitude");
                latitudeVal = extras.getString("longitude");

                edtSaveLongitude.setText(longitudeVal);
                edtSaveLatitude.setText(latitudeVal);

            }
        } else {
//            newString= (String) savedInstanceState.getSerializable("STRING_I_NEED");
        }

        imgButtonTakePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(i, TAKE_PICTURE);
            }
        });



        //save to database history in general, all submissions will be here regardless of whether the user wants to save to his own profile page or not. Or regardless to whether the person want to publish on community page
        btnSubmitSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save to firebase first as a general untracked object that is just part of the database
                //easier to retrieve it later if the user decides to save officially to their profile and or publish it.


                successfulUpload = false;

                //a database using GeneralTest as the object to inject to firebase
                //just adding all the user submitted values to this class
                GeneralTest toAdd = new GeneralTest();
                // TODO: pass value from EditText instead of the variable passed in
                toAdd.setLatitude(latitudeVal);
                toAdd.setLongitude(longitudeVal);

                toAdd.setTitle(edtSaveTitle.getText().toString());
                toAdd.setUserId(userId);
                toAdd.setEmail(userEmail);
                toAdd.setFishname(edtSaveFishName.getText().toString());
                toAdd.setWeight(edtSaveWeight.getText().toString());
                toAdd.setLength(edtSaveLength.getText().toString());
                toAdd.setSpecies(edtSaveSpecies.getText().toString());
                toAdd.setGenus(edtSaveGenus.getText().toString());
                toAdd.setBait(edtSaveBait.getText().toString());
                toAdd.setBodyshape(edtSaveBodyShape.getText().toString());
                toAdd.setUsercomment(edtSaveUserComments.getText().toString());

                //still need to add stuff like, likes = 0, image etc


// //               firebase.getReference("GeneralTest").child(FirebaseAuth.getInstance().getCurrentUser().getUid());

//                //saving images to storage https://firebase.google.com/docs/storage/android/upload-files
//                ByteArrayOutputStream baos = new ByteArrayOutputStream();
//                bitMapToSave.compress(Bitmap.CompressFormat.JPEG, 100, baos);
//                byte[] data = baos.toByteArray();
//
//
//                int theStorageSize = fishImageStorageSize; //trying to make this as atomically as I possible can so reference it now! maybe do a critical section clause here... but threading problem?
//                StorageReference newUserImageToUpload = userFishImagesRef.child(String.valueOf(theStorageSize) + ".jpg"); //set the child to save to, its going to be called like 1.jpg or like 2.jpg etc... inside
//                //UserFishImages directory

                //only add it to database if user took a picture of the caught fish
                //have a boolean to check if image is uploaded successfully, if not dont enter past here
                if (imageTaken) {
                    //saving images to storage https://firebase.google.com/docs/storage/android/upload-files
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bitMapToSave.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                    byte[] data = baos.toByteArray();


                    int theStorageSize = fishImageStorageSize; //trying to make this as atomically as I possibly can so reference it now! maybe do a critical section clause here... but threading problem?
                    StorageReference newUserImageToUpload = userFishImagesRef.child(String.valueOf(theStorageSize) + ".jpg"); //set the child to save to, its going to be called like 1.jpg or like 2.jpg etc... inside
                    //UserFishImages directory

                    UploadTask uploadTask = newUserImageToUpload.putBytes(data); //actually upload it usering UploadTask
                    //inside on successlistener or onfailure update successfulUpload to handle gatekeeping from proceeding to next phase
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            successfulUpload = false;
                            Toast.makeText(v.getContext(), "Failure to upload " + fishImageStorageSize + ".jpg, Please Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            successfulUpload = true;
                            //update database count now for imagestoragesize
                            toAdd.setImgId(String.valueOf(theStorageSize)); //add the last reference to the object we are to Add to database
                            int updateStorageSize = fishImageStorageSize + 1; //
                            firebase.getReference("GeneralDatabaseData/UserFishImagesCount").setValue(updateStorageSize); //update the database for the total amount of User submitted image count

                            firebase.getReference("GeneralTest").child(String.valueOf(databaseSize)).setValue(toAdd); //store the fishinfos saved object to database as a record

                            //toss in all the required intents to next page in case it is needed to be saved to profile or published onto main page
                            Intent goToConfirmSavePublishActivity = new Intent(v.getContext(), ConfirmSavePublishActivity.class);

                             //tossing everything to next page
                            goToConfirmSavePublishActivity.putExtra("imgId", toAdd.getImgId());
                            goToConfirmSavePublishActivity.putExtra("latitude", toAdd.getLatitude());
                            goToConfirmSavePublishActivity.putExtra("longitude", toAdd.getLongitude());
                            goToConfirmSavePublishActivity.putExtra("title", toAdd.getTitle());
                            goToConfirmSavePublishActivity.putExtra("userId", toAdd.getUserId());
                            goToConfirmSavePublishActivity.putExtra("email", toAdd.getEmail());
                            goToConfirmSavePublishActivity.putExtra("fishname", toAdd.getFishname());
                            goToConfirmSavePublishActivity.putExtra("weight", toAdd.getWeight());
                            goToConfirmSavePublishActivity.putExtra("length", toAdd.getLength());
                            goToConfirmSavePublishActivity.putExtra("species", toAdd.getSpecies());
                            goToConfirmSavePublishActivity.putExtra("genus", toAdd.getGenus());
                            goToConfirmSavePublishActivity.putExtra("bait", toAdd.getBait());
                            goToConfirmSavePublishActivity.putExtra("bodyshape", toAdd.getBodyshape());
                            goToConfirmSavePublishActivity.putExtra("usercomment", toAdd.getUsercomment());

                            startActivity(goToConfirmSavePublishActivity);//go to Save on profile and/or publish on community page
                        }
                    });

                } else {
                    //you lying
                    //thus you shall not pass
                    Toast.makeText(v.getContext(), "Please upload your caught fish picture by using the Image Button", Toast.LENGTH_SHORT).show();
                }





//                UploadTask uploadTask = newUserImageToUpload.putBytes(data); //actually upload it usering UploadTask
//                //inside on successlistener or onfailure update successfulUpload to handle gatekeeping from proceeding to next phase
//                uploadTask.addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        successfulUpload = false;
//                        Toast.makeText(v.getContext(), "Failure to upload " + fishImageStorageSize + ".jpg, Please Try again", Toast.LENGTH_SHORT).show();
//                    }
//                });
//                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                    @Override
//                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                        successfulUpload = true;
//                        //update database count now for imagestoragesize
//                        toAdd.setImgId(String.valueOf(theStorageSize)); //add the last reference to jpg
//                        int updateStorageSize = fishImageStorageSize + 1;
//                        firebase.getReference("GeneralDatabaseData/UserFishImagesCount").setValue(updateStorageSize);
//
//                        firebase.getReference("GeneralTest").child(String.valueOf(databaseSize)).setValue(toAdd);
//                        Intent goToConfirmSavePublishActivity = new Intent(v.getContext(), ConfirmSavePublishActivity.class);
//                        startActivity(goToConfirmSavePublishActivity);
//                    }
//                });


                //Toast.makeText(v.getContext(), FirebaseAuth.getInstance().getCurrentUser().getUid().toString(), Toast.LENGTH_SHORT).show(); //testing if the user auth makes it here, and yes it does

//                firebase.getReference("GeneralTest").child(String.valueOf(databaseSize)).setValue(toAdd);
//                Intent goToConfirmSavePublishActivity = new Intent(v.getContext(), ConfirmSavePublishActivity.class);
//                startActivity(goToConfirmSavePublishActivity);
            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        if (!(resultCode == RESULT_OK)) {

            return;
        }

        switch (requestCode) {
            case TAKE_PICTURE:
                Bundle bundleData = data.getExtras();
                Bitmap photo = (Bitmap) bundleData.get("data");
                imgButtonTakePicture.setImageBitmap(photo); //set the photo on the imgbutton to show the user the picture that was just taken
                bitMapToSave = photo; //remember the last photo's bitmap so if we need to save it later we can just call this reference and turn it to byte array to upload to firebase
                imageTaken = true;
                break;
            default:
                break;
        }

        super.onActivityResult(requestCode, resultCode, data);
        return;
    }
}