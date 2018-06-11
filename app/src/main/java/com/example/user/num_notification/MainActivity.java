package com.example.user.num_notification;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.annotation.NonNull;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends Activity implements View.OnClickListener {
    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
        }
    };
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    Button login;
    private String TITLE, CONTENT;
    private ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        login = (Button)findViewById(R.id.login);
        login.setOnClickListener(this);
        Button test = (Button)findViewById(R.id.test);
        test.setOnClickListener(this);
        mAuth = FirebaseAuth.getInstance();
        progressDialog = new ProgressDialog(MainActivity.this);
        SharedPreferences u = getSharedPreferences("USERNAME", MODE_PRIVATE);
        SharedPreferences p = getSharedPreferences("PASSWORD", MODE_PRIVATE);
        registerReceiver(receiver, new IntentFilter());
        String value1 = u.getString("Mail", "");
        String value2 = p.getString("Pword", "");
        EditText uname = (EditText)findViewById(R.id.username);
        EditText pword = (EditText)findViewById(R.id.password);
        if(!value1.isEmpty()){
            uname.setText(value1);
        }
        if(!value2.isEmpty()){
            pword.setText(value2);
        }
      //  Intent intent = new Intent(this, Notify.class);
      //  startService(intent);
        startService(new Intent(NotifyService.class.getName()));
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference reference = database.getReference("notifications");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Notify not = null;
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    not = snapshot.getValue(Notify.class);
                   
                }
               // sendNotification(not.getTITLE(), not.getSUMMARY(), not.getLINK());
                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                progressDialog.dismiss();
                Toast.makeText(MainActivity.this, "Датаг уншиж чадсангүй: " + databaseError.getCode(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter());
    }
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void sendNotification(String title, String summary, String link) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "NUM_CHANNEL",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("YOUR_NOTIFICATION_CHANNEL_DISCRIPTION");
            mNotificationManager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.mipmap.ic_launcher) // notification icon
                .setContentTitle(title) // title for notification
                .setContentText(summary+"\n"+link)// message for notification
                //.setSound(alarmSound) // set alarm sound for notification
                .setAutoCancel(true); // clear notification after click
        try {
            Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), notification);
            r.play();
        } catch (Exception e) {
            e.printStackTrace();
        }

        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        mNotificationManager.notify(0, mBuilder.build());
    }
    @Override
    public void onStop() {
        super.onStop();
        //   if (mAuthListener != null) {
        //       mAuth.removeAuthStateListener(mAuthListener);
        //   }
        progressDialog.dismiss();
    }
    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnectedOrConnecting();
    }

    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.login:{

                if(!isOnline()){
                    Toast.makeText(this, "Интернэт холболтоо шалгана уу!!!", Toast.LENGTH_LONG).show();
                    return;
                }
                EditText uname = (EditText)findViewById(R.id.username);
                EditText pword = (EditText)findViewById(R.id.password);
                String username, password;
                username = uname.getText().toString();
                password = pword.getText().toString();
                if(username.isEmpty()){
                    Toast.makeText(this, "Хэрэглэгчийн нэр хоосон байна!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                if(password.isEmpty()){
                    Toast.makeText(this, "Нууц үг хоосон байна!!!", Toast.LENGTH_SHORT).show();
                    return;
                }
                CheckBox ch = (CheckBox)findViewById(R.id.remember);
                //mAuth = FirebaseAuth.getInstance();
                Log.d("medeelel", "taskiing deer irsem");
                progressDialog.setMessage("Шалгаж байна ....");
                progressDialog.show();
                mAuth.signInWithEmailAndPassword(username, password)
                        .addOnCompleteListener(this, new OnCompleteListener<AuthResult>(){
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task){
                                if(task.isSuccessful()){
                                    Toast.makeText(MainActivity.this, "Амжилттай нэвтэрлээ", Toast.LENGTH_SHORT).show();
                                    Log.i("medeelel", "yes");
                                    FirebaseUser user = mAuth.getCurrentUser();
                                    progressDialog.dismiss();
                                    Intent goParent = new Intent(MainActivity.this, Parent.class);
                                    MainActivity.this.startActivity(goParent);
                                }else{
                                    Toast.makeText(MainActivity.this, "Хэрэглэгчийн нэр эсвэл нууц үг буруу!", Toast.LENGTH_SHORT).show();
                                    Log.i("medeelel", "no");
                                    progressDialog.dismiss();
                                    return;
                                }
                            }

                        });
                if(ch.isChecked()){
                    SharedPreferences uName = getSharedPreferences("USERNAME", MODE_PRIVATE);
                    SharedPreferences pWord = getSharedPreferences("PASSWORD", MODE_PRIVATE);
                    SharedPreferences.Editor editor = uName.edit();
                    SharedPreferences.Editor editor2 = pWord.edit();
                    editor.putString("Mail", username);
                    editor2.putString("Pword", password);
                    editor2.commit();
                    editor.commit();
                }


                break;

            }
            case R.id.test:{
                //sendNotification(view);
                break;
            }

        }
    }
    private void signIn(String email, String password) {
        Log.d("medeelel", "signIn:" + email+"password"+password);
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    Log.d("medeelel", "signInWithEmail:success");
                    FirebaseUser user = mAuth.getCurrentUser();
                } else {
                    Log.w("medeelel", "signInWithEmail:failure", task.getException());
                    Toast.makeText(MainActivity.this, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
                Log.d("medeelel", "yadaj orson");
            }
        });
    }

}
