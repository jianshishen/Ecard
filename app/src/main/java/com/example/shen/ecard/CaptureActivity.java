package com.example.shen.ecard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;

public class CaptureActivity extends AppCompatActivity {
    String type;
    String number;
    String company;
    String companyselected;
    String[] companies;
    Button confirm;
    Button cancel;
    Spinner spinner;
    EditText editText;
    EditText othercompany;
    ImageView imageView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_capture);

        if ( ContextCompat.checkSelfPermission(CaptureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(CaptureActivity.this, new String[]{ Manifest.permission.CAMERA}, 3);
        }

        Intent intentlast=getIntent();
        type=intentlast.getStringExtra("type");
        spinner = (Spinner) findViewById(R.id.spinner);
        editText=(EditText)findViewById(R.id.capturenumber);
        othercompany=(EditText)findViewById(R.id.othercompany);
        imageView=(ImageView)findViewById(R.id.capturelogo);
        confirm=(Button)findViewById(R.id.captureconfirm);
        cancel=(Button)findViewById(R.id.capturecancel);
        companies = getResources().getStringArray(R.array.company);
        if(type.equals("add")) {
            new IntentIntegrator(this).initiateScan();
        }
        else if(type.equals("modify")){
            number=intentlast.getStringExtra("number");
            company=intentlast.getStringExtra("company");
            editText.setEnabled(false);
            switch (company){
                case "Woolworths":
                    othercompany.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.woolworths);
                    break;
                case "Coles":
                    othercompany.setVisibility(View.GONE);
                    imageView.setImageResource(R.drawable.flybuys);
                    break;
                default:
                    othercompany.setVisibility(View.VISIBLE);
                    othercompany.setText(company);
                    imageView.setImageResource(R.drawable.randomcard);
                    break;
            }
            editText.setText(number);
            int count=companies.length;
            for(int i=0;i<count;i++){
                if(company.equals(companies[i])){
                    spinner.setSelection(i,true);
                    break;
                }
                else{
                    spinner.setSelection(2,true);
                }
            }
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view,
                                       int pos, long id) {
                companyselected=companies[pos];
                switch (companyselected){
                    case "Woolworths":
                        othercompany.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.woolworths);
                        break;
                    case "Coles":
                        othercompany.setVisibility(View.GONE);
                        imageView.setImageResource(R.drawable.flybuys);
                        break;
                    case "Others":
                        othercompany.setVisibility(View.VISIBLE);
                        imageView.setImageResource(R.drawable.randomcard);
                        break;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Boolean duplicated=false;
                if(validinput()) {
                    if(NetworkTool.isNetworkConnected(CaptureActivity.this)) {
                        if (type.equals("add")) {
                            for(ListItem temp :CardsFragment.items){
                                if(temp.getNumber().equals(editText.getText().toString().trim())){
                                    Toast.makeText(CaptureActivity.this,"Duplicated Number!",Toast.LENGTH_LONG).show();
                                    duplicated=true;
                                    break;
                                }
                            }
                            if(!duplicated) {
                                if (companyselected.equals("Others")) {
                                    new addtask().execute(editText.getText().toString().trim(), othercompany.getText().toString().trim());
                                } else {
                                    new addtask().execute(editText.getText().toString().trim(), companyselected);
                                }
                            }
                        } else if (type.equals("modify")) {
                            if (companyselected.equals("Others")){
                                new addtask().execute(editText.getText().toString().trim(),othercompany.getText().toString().trim());
                            }
                            else {
                                new modifytask().execute(editText.getText().toString().trim(), companyselected);
                            }
                        }
                        finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==3){
            if (grantResults[0]==PackageManager.PERMISSION_GRANTED){
            }else {
                Toast.makeText(this,"Permission Denied",Toast.LENGTH_LONG).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    class addtask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try{
                CloudStorageAccount storageAccount=CloudStorageAccount.parse(NetworkTool.storageConnectionString);
                CloudTableClient tableClient = storageAccount.createCloudTableClient();
                CloudTable cloudTable = tableClient.getTableReference("card");

                CardEntity cardEntity = new CardEntity(HomepageActivity.username, params[0]);
                cardEntity.setCompany(params[1]);

                TableOperation insertCard = TableOperation.insertOrReplace(cardEntity);

                cloudTable.execute(insertCard);
                CardsFragment.refresh();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    class modifytask extends AsyncTask<String,Void,Void> {
        @Override
        protected Void doInBackground(String... params) {
            try{
                CloudStorageAccount storageAccount=CloudStorageAccount.parse(NetworkTool.storageConnectionString);
                CloudTableClient tableClient = storageAccount.createCloudTableClient();
                CloudTable cloudTable = tableClient.getTableReference("card");
                TableOperation retrieve = TableOperation.retrieve(HomepageActivity.username, params[0], CardEntity.class);
                CardEntity entity =
                        cloudTable.execute(retrieve).getResultAsType();
                TableOperation delete = TableOperation.delete(entity);
                cloudTable.execute(delete);
                CardEntity cardEntity = new CardEntity(HomepageActivity.username, params[0]);
                cardEntity.setCompany(params[1]);

                TableOperation insertCard = TableOperation.insertOrReplace(cardEntity);

                cloudTable.execute(insertCard);
                CardsFragment.refresh();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private boolean validinput() {
        String number=editText.getText().toString().trim();
        if(number.equals(""))
        {
            DialogTool.showDialog(this, "Input Number!");
            return false;
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if(result != null) {
            if(result.getContents() == null) {
            } else {
                editText.setText(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}
