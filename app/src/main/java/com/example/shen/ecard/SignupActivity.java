package com.example.shen.ecard;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

public class SignupActivity extends AppCompatActivity {

    EditText username;
    EditText password;
    Button confirm;
    Button cancel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        this.setTitle("Signup Page");
        username=(EditText)findViewById(R.id.signupusername);
        password=(EditText)findViewById(R.id.signuppassword);
        confirm=(Button)findViewById(R.id.signupconfirm);
        cancel=(Button)findViewById(R.id.signupcancel);

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(NetworkTool.isNetworkConnected(SignupActivity.this)) {
                    if (validinput()) {
                        String name = username.getText().toString().trim();
                        String pwd = password.getText().toString().trim();
                        new task().execute(name,pwd);
                    }
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
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

    class task extends AsyncTask<String,Void,Boolean> {
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
                if (specificEntity == null) {
                    try
                    {
                        UserEntity userEntity = new UserEntity("a", params[0]);
                        userEntity.setPassword(params[1]);
                        TableOperation insert = TableOperation.insertOrReplace(userEntity);
                        cloudTable.execute(insert);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                else{
                    return false;
                }
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return true;
        }
        protected void onPostExecute(Boolean result) {
            if(result){
                finish();
            }
            else{
                Toast.makeText(getApplicationContext(), "Username is occupied!", Toast.LENGTH_LONG).show();
            }
        }
    }
}
