package com.curiolearning.curio;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.curiolearning.curio.Common.CurrentChaptersArray;
import com.curiolearning.curio.Model.Chapter;
import com.curiolearning.curio.Model.SubChapter;
import com.curiolearning.curio.Util.DataBaseUtil;
import com.google.android.gms.ads.MobileAds;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.instabug.library.Instabug;
import com.instabug.library.invocation.InstabugInvocationEvent;
import com.wang.avi.AVLoadingIndicatorView;

import java.util.ArrayList;

public class SplashActivity extends AppCompatActivity {
    DatabaseReference chapters;
    DatabaseReference subChapters;
    ArrayList<Chapter> chaptersArray = new ArrayList<>();
    final Context context = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        final CoordinatorLayout coordinatorLayout=findViewById(R.id.coordinator);

        //admob
        MobileAds.initialize(this, "ca-app-pub-2696517167911064~3367214157");

        //progress
        final AVLoadingIndicatorView loading= findViewById(R.id.loading);
        loading.show();

        //firebase
        DataBaseUtil.getDatabase();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        chapters = database.getReference("chapters");
        chapters.keepSynced(true);
        subChapters = database.getReference("subChapters");
        subChapters.keepSynced(true);

        DatabaseReference connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected");
        connectedRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                boolean connected = snapshot.getValue(Boolean.class);
                if (connected) {
                    Toast.makeText(context, "Connected", Toast.LENGTH_SHORT).show();
                } else {
                    if (!isConnectedToInternet(context)) {

                        Toast.makeText(context, "Please connect to the Internet", Toast.LENGTH_SHORT).show();}
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                System.err.println("Listener was cancelled");
            }
        });

        chapters.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterable<DataSnapshot> chaptersSet = dataSnapshot.getChildren();
                for (DataSnapshot n : chaptersSet) {
                    final Chapter chapter = n.getValue(Chapter.class);
                    final String chapterKey = n.getKey();
                    assert chapter != null;
                    chapter.setId(Integer.parseInt(chapterKey));

                    //subchapters
                    subChapters.orderByChild("chapterId").equalTo(chapterKey).addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            Iterable<DataSnapshot> subChaptersSet = dataSnapshot.getChildren();
                            ArrayList<SubChapter> subChaptersArray = new ArrayList<>();
                            for (DataSnapshot n : subChaptersSet) {
                                SubChapter subChapter = n.getValue(SubChapter.class);
                                assert subChapter != null;
                                subChapter.setId(Integer.parseInt(n.getKey()));
                                subChaptersArray.add(subChapter);
                            }
                            chapter.setsubChapters(subChaptersArray);
                            chaptersArray.add(chapter);
                            CurrentChaptersArray.currentChaptersArray = chaptersArray;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {

                                    Intent imCuriousIntent = new Intent(SplashActivity.this, ChaptersActivity.class);
                                    startActivity(imCuriousIntent);
                                    finish();
                                }
                            },2000);
                            loading.hide();

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {


                        }
                    });
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private boolean isConnectedToInternet(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        assert cm != null;
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }
}
