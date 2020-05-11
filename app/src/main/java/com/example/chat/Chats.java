package com.example.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.AlignmentSpan;
import android.text.style.RelativeSizeSpan;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
public class Chats extends AppCompatActivity implements View.OnClickListener {
    String p,a;
    DatabaseReference db,ud,dbr;
    FirebaseAuth auth;
    TableLayout t;
    Button b;
    EditText text;
    ScrollView scr;
    int c=0,fl=1,bl=0,f=0;
    int bot=0;
    int auto=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chats);
        Intent intent=getIntent();
        p=intent.getExtras().getString("person");
        String t1=p.substring(p.indexOf('&')+1);
        p=p.substring(0,p.indexOf('@'));
        auth=FirebaseAuth.getInstance();
        getSupportActionBar().setTitle(t1);
        t=findViewById(R.id.table);
        a=auth.getCurrentUser().getEmail().substring(0,auth.getCurrentUser().getEmail().indexOf('@'));
        if(a.contains("."))
            a=a.replace('.','!');
        dbr=FirebaseDatabase.getInstance().getReference().child("Users").child(a);
        dbr.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Details dd=dataSnapshot.getValue(Details.class);
                bot=dd.chatbot;
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        chatbase(a,p);
        b=findViewById(R.id.button2);
        b.setOnClickListener(this);
        text=findViewById(R.id.editText3);
        scr=findViewById(R.id.scrollView2);
        ud=FirebaseDatabase.getInstance().getReference().child("Last");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.chat_menu,menu);
        return true;
    }

    @Override
    protected boolean onPrepareOptionsPanel(View view, Menu menu) {
        MenuItem item=menu.findItem(R.id.item6);
        if(bl==1)
            item.setTitle("Unblock");
        else if(bl==0)
            item.setTitle("Block");
        return true;
    }

    public void chatbase(String a1, String p1)
    {
        int i=0;String n="";
        while(i<a1.length() && i<p1.length())
        {
            if((int)(a1.charAt(i))<(int)(p1.charAt(i)))
            {
                n=""+(a1+"^"+p1);
                break;
            }
            else if((int)(a1.charAt(i))>(int)(p1.charAt(i)))
            {
                n=""+(p1+"^"+a1);
                break;
            }else{
                if(a1.length()<p1.length())
                    n=""+(a1+"^"+p1);
                else
                    n=""+(p1+"^"+a1);
            }
            i++;
        }
        db=FirebaseDatabase.getInstance().getReference().child("ChatBox").child(n);
        viewChat();
    }

    public void viewChat()
    {
        db.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                t.removeAllViews();
                fl=1;
                c=0;
                for (DataSnapshot ds1 : dataSnapshot.getChildren()) {
                    try {
                        User u = ds1.getValue(User.class);
                        final TableRow tr=new TableRow(getApplicationContext());
                        tr.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT,TableLayout.LayoutParams.WRAP_CONTENT));
                        tr.setGravity(Gravity.CENTER);
                        if(!ds1.getKey().equals(a+"\\"+"BLOCK") && !ds1.getKey().equals(p+"\\"+"BLOCK") && !ds1.getKey().equals("BLANK") && !ds1.getKey().equals("LAST")) {
                            if (u.email.contains(a + "\\")) {
                                String z = (u.email.substring(u.email.indexOf('\\') + 1));
                                LayoutInflater inflater = getLayoutInflater();

                                if(!u.email.contains("*%SEEN%*"))
                                {

                                    View v = inflater.inflate(R.layout.right_chat, null);
                                    TextView tvt = v.findViewById(R.id.r_message);
                                    if (z.indexOf('$') > -1)
                                    {
                                        String p;
                                        if(z.contains("*%SEEN%*"))
                                            p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1,z.lastIndexOf('*')-7);
                                        else
                                            p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1);
                                        SpannableString spannableString=new SpannableString(p);
                                        spannableString.setSpan(new RelativeSizeSpan(0.6f),p.indexOf(':')-2,p.length(),0);
                                        spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':')-2,p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        tvt.setText(spannableString);
                                    }
                                    else
                                        tvt.setText(z);
                                    t.addView(v);
                                }
                                else{
                                    View v = inflater.inflate(R.layout.seen, null);
                                    TextView tvt = v.findViewById(R.id.r_message);
                                    if (z.indexOf('$') > -1){
                                        String p;
                                        if(z.contains("*%SEEN%*"))
                                            p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1,z.lastIndexOf('*')-7);
                                        else
                                            p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1);
                                        SpannableString spannableString=new SpannableString(p);
                                        spannableString.setSpan(new RelativeSizeSpan(0.6f),p.indexOf(':')-2,p.length(),0);
                                        spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':')-2,p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                        tvt.setText(spannableString);
                                    }
                                    else
                                    {
                                        tvt.setText(z);
                                    }
                                    t.addView(v);
                                }
                                c++;

                            }
                            if (u.email.contains(p + "\\")) {
                                String z = (u.email.substring(u.email.indexOf('\\') + 1));
                                LayoutInflater inflater = getLayoutInflater();
                                View v = inflater.inflate(R.layout.left_chat, null);
                                TextView tvt = v.findViewById(R.id.l_message);
                                if (z.indexOf('$') > -1)
                                {
                                    String p;
                                    if(z.contains("*%SEEN%*"))
                                        p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1,z.lastIndexOf('*')-7);
                                    else
                                        p=z.substring(0, z.indexOf('$'))+"    "+z.substring(z.indexOf('#')+1);
                                    SpannableString spannableString=new SpannableString(p);
                                    spannableString.setSpan(new RelativeSizeSpan(0.6f),p.indexOf(':')-2,p.length(),0);
                                    spannableString.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), p.indexOf(':')-2,p.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                                    tvt.setText(spannableString);
                                }
                                else
                                {
                                    tvt.setText(z);
                                }
                                c++;
                                if(!u.email.contains("*%SEEN%*") && f==0) {
                                    seen(u.email,ds1.getKey());
                                }
                                t.addView(v);

                            }
                        }
                        else if(u.email.equals("Block"))
                        {
                            fl=fl*0;
                            if(ds1.getKey().equals(a+"\\"+"BLOCK"))
                                bl=1;
                        }
                        else if(u.email.equals("Unblock")){
                            fl=fl*1;
                            if(ds1.getKey().equals(a+"\\"+"BLOCK"))
                                bl=0;
                        }
                    } catch (NullPointerException npe) {
                    }
                }
                scr.post(new Runnable() {
                    @Override
                    public void run() {
                        scr.fullScroll(View.FOCUS_DOWN);
                    }
                });
                invalidateOptionsMenu();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
    public void seen(String em,String k)
    {
        if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("GOOD MORNING")||em.contains("Good morning")||em.contains("good morning")||em.contains("Good Morning")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Good Morning (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("GOOD AFTERNOON")||em.contains("Good afternoon")||em.contains("good afternoon")||em.contains("Good Afternoon")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Good Afternoon (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("GOOD EVENING")||em.contains("Good evening")||em.contains("good evening")||em.contains("Good Evening")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Good Evening (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("GOOD NIGHT")||em.contains("Good night")||em.contains("good night")||em.contains("Good Night")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Good Night (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("HELLO")||em.contains("Hello")||em.contains("hello")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Hello (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("BYE")||em.contains("Bye")||em.contains("bye")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Bye (auto generated)");
        }
        else if((auto==0)&&(!em.contains("*%SEEN%*"))&&(!em.contains("(auto generated)"))&&(em.contains("THANK YOU")||em.contains("Thank you")||em.contains("thank you") ||em.contains("Thank You")||em.contains("Thanks")||em.contains("thanks")) && bot==1)
        {
            auto++;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
            addChat(a+"\\"+"Welcome (auto generated)");
        }
        else {
            auto=0;
            User b = new User(em + "*%SEEN%*");
            db.child(k).setValue(b);
        }
    }
    public void addChat(String n) {
        if(c==0)
        {
            User b=new User("Unblock");
            db.child(a+"\\"+"BLOCK").setValue(b);
            db.child(p+"\\"+"BLOCK").setValue(b);
        }
        String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        String currentTime = new SimpleDateFormat("hh:mm aa", Locale.getDefault()).format(new Date());
        String currentTime2 = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        if(n.contains("^BLOCK^")) {
            User b = new User("Block");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else if(n.contains("^UNBLOCK^")){
            User b= new User("Unblock");
            db.child(a+"\\"+"BLOCK").setValue(b);
        }
        else {
            n = n + "$" + currentDate + "#" + currentTime;
            User u = new User(n);
            db.push().setValue(u);
            dbr.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    Details detail=dataSnapshot.getValue(Details.class);
                    try {
                        if(detail.soundEffect==1){
                            MediaPlayer ring= MediaPlayer.create(getApplicationContext(),R.raw.sent);
                            ring.start();
                        }
                    } catch (Exception e) {
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            User u2=new User(""+currentDate+"%"+currentTime);
            db.child("LAST").setValue(u2);
            u=new User(commonChatBoxName(a,p));
            ud.child(""+currentDate + "*" + currentTime2).setValue(u);
            viewChat();
        }
    }
    public String commonChatBoxName(String a1,String p1){
        int i=0;String n="";
        while(i<a1.length() && i<p1.length())
        {
            if((int)(a1.charAt(i))<(int)(p1.charAt(i)))
            {
                n=""+(a1+"^"+p1);
                break;
            }
            else if((int)(a1.charAt(i))>(int)(p1.charAt(i)))
            {
                n=""+(p1+"^"+a1);
                break;
            }else{
                if(a1.length()<p1.length())
                    n=""+(a1+"^"+p1);
                else
                    n=""+(p1+"^"+a1);
            }
            i++;
        }
        return n;
    }
    @Override
    public void onBackPressed() {
        finish();
        f=1;
        startActivity(new Intent(getApplicationContext(),ChatBox.class));
    }

    @Override
    public void onClick(View v) {
        if(v==b)
        {
            if(fl==1) {
                String n = text.getText().toString();
                if (n.length() == 0) {
                    Toast.makeText(getApplicationContext(), "Message cannot be blank", Toast.LENGTH_SHORT).show();
                    return;
                }
                text.setText("");
                text.setHint("Type your message");
                chatbase(a, p);
                addChat(a + "\\" + n);
            }
            else
            {
                Toast.makeText(getApplicationContext(),"Chat is Blocked",Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()){
            case R.id.item4:
                Intent intent=new Intent(getApplicationContext(),ProfilePerson.class);
                intent.putExtra("Email",p);
                finish();
                startActivity(intent);
                return true;
            case R.id.item5:
                Snackbar.make(findViewById(R.id.scrollView2), "Delete Chats will be available in next Update", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return true;
            case R.id.item6:
                if(item.getTitle().equals("Block")) {
                    chatbase(a, p);
                    addChat(a + "\\" + "^BLOCK^");
                    Toast.makeText(getApplicationContext(), "Blocked", Toast.LENGTH_SHORT).show();
                    item.setTitle("Unblock");
                }
                else if(item.getTitle().equals("Unblock")) {
                    chatbase(a, p);
                    addChat(a + "\\" + "^UNBLOCK^");
                    Toast.makeText(getApplicationContext(), "Unblocked", Toast.LENGTH_SHORT).show();
                    item.setTitle("Block");
                }
                if(bl==1)
                    item.setTitle("Unblock");
                else if(bl==0)
                    item.setTitle("Block");
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}