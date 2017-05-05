package com.jinwoo.android.firebasechat;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import static com.jinwoo.android.firebasechat.R.id.btnSignin;

public class MainActivity extends AppCompatActivity {

    EditText editId, editPwd;
    Button btnSignin, btnRegister;

    // 데이터 베이스 연결
    FirebaseDatabase database;
    DatabaseReference userRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        database = FirebaseDatabase.getInstance();
        userRef = database.getReference("user");

        editId = (EditText) findViewById(R.id.editId);
        editPwd = (EditText) findViewById(R.id.editPwd);

        btnRegister = (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnSignin = (Button)findViewById(R.id.btnSignin);
        btnSignin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String id = editId.getText().toString();
                final String pw = editPwd.getText().toString();

                // DB 1. 파이어베이스로 child(id) 레퍼런스에 대한 쿼리를 날린다. userRef.child(id) 검색에 대한 쿼리
                userRef.child(id).addListenerForSingleValueEvent(new ValueEventListener() {

                    // DB 2. 파이어베이스는 데이터쿼리가 완료되면 스냅샷에 담아서 onDataChange를 호출해준다.
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if(dataSnapshot.getChildrenCount() > 0){
                            String fbPw = dataSnapshot.child("password").getValue().toString();
                            String name = dataSnapshot.child("name").getValue().toString();
                            if(fbPw.equals(pw)){
                                Intent intent = new Intent(MainActivity.this, RoomListActivity.class);
                                intent.putExtra("userid",id);
                                intent.putExtra("username",name);
                                startActivity(intent);
                            } else {
                                Toast.makeText(MainActivity.this, "Wrong PassWord", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(MainActivity.this, "Nothing", Toast.LENGTH_SHORT).show();
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        });
    }
}
