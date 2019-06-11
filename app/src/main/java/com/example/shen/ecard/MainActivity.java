package com.example.shen.ecard;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

public class MainActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button confirm;
    Button signup;
    Switch switchfingerprint;
    SharedPreferences preferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        preferences = PreferenceManager.getDefaultSharedPreferences(this);

        setContentView(R.layout.activity_main);

        this.setTitle("Login Page");

        switchfingerprint=(Switch)findViewById(R.id.switchfingerprint);
        username=(EditText)findViewById(R.id.editText);
        password=(EditText)findViewById(R.id.editText2);
        confirm=(Button)findViewById(R.id.button);
        signup=(Button)findViewById(R.id.button2);
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkTool.isNetworkConnected(MainActivity.this)) {
                    if (validinput()) {
                        String name = username.getText().toString().trim();
                        String pwd = password.getText().toString().trim();
                        new task().execute(name, pwd);
                    }
                }
            }
        });
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkTool.isNetworkConnected(MainActivity.this)) {
                    Intent intent=new Intent(MainActivity.this,SignupActivity.class);
                    startActivity(intent);
                }
            }
        });
    }

    private boolean validinput() {
        String name=username.getText().toString().trim();
        String pwd=password.getText().toString().trim();
        if(name.equals(""))
        {
            DialogTool.showDialog(this, "Input Username!");
            return false;
        }
        if(pwd.equals(""))
        {
            DialogTool.showDialog(this, "Input Password!");
            return false;
        }
        return true;
    }
    class task extends AsyncTask<String,Void,Boolean>{
        @Override
        protected Boolean doInBackground(String... params) {
            try{
                CloudStorageAccount storageAccount=CloudStorageAccount.parse(NetworkTool.storageConnectionString);
                CloudTableClient tableClient = storageAccount.createCloudTableClient();
                CloudTable cloudTable = tableClient.getTableReference("user");
                TableOperation retrieve =
                        TableOperation.retrieve("a",params[0],UserEntity.class);
                UserEntity specificEntity =
                        cloudTable.execute(retrieve).getResultAsType();
                if (specificEntity != null)
                {
                    if(params[1].equals(specificEntity.getPassword())){
                        return true;
                    }
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return false;
        }
        protected void onPostExecute(Boolean result) {
            if(result == true) {
                if(switchfingerprint.isChecked()){
                    SharedPreferences.Editor editor = preferences.edit();
                    editor.putString("Flag","true");
                    editor.putString("username",username.getText().toString().trim());
                    editor.apply();
                }
                Intent intent = new Intent(MainActivity.this, HomepageActivity.class);
                intent.putExtra("username",username.getText().toString().trim());
                startActivity(intent);
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Login error!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
