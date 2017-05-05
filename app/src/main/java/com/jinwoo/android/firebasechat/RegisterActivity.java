package com.jinwoo.android.firebasechat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    EditText editName, editId, editPwd;
    Button btnSave;

    FirebaseDatabase database;
    DatabaseReference regRef;

    String userId = "";
    String username = "";
    String userPwd = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // 데이터 베이스 연결
        database = FirebaseDatabase.getInstance();

        // 위젯 세팅
        editName = (EditText)findViewById(R.id.editName);
        editId = (EditText)findViewById(R.id.editId);
        editPwd = (EditText)findViewById(R.id.editPwd);
        btnSave = (Button)findViewById(R.id.btnSave);

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                regRef = database.getReference("user");

                String userId = editId.getText().toString();
                String username = editName.getText().toString();
                String userPwd = editPwd.getText().toString();

                if(isSame()) {

                    Toast.makeText(RegisterActivity.this, "There is an another account same as your account", Toast.LENGTH_SHORT).show();


                } else {
                    Map<String, String> postRegMap = new HashMap<>();
                    postRegMap.put("name", username);
                    postRegMap.put("password", userPwd);

                    Map<String, Object> regMap = new HashMap<>();
                    regMap.put(userId, postRegMap);

                    regRef.updateChildren(regMap);

                    finish();
                }
            }
        });
    }

    public boolean isSame(){

        boolean res = true;

        String id = regRef.getKey()


        return res;

    }

}
