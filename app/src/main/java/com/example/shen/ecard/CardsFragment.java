package com.example.shen.ecard;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.table.CloudTable;
import com.microsoft.azure.storage.table.CloudTableClient;
import com.microsoft.azure.storage.table.TableOperation;
import com.microsoft.azure.storage.table.TableQuery;

import java.util.ArrayList;

public class CardsFragment extends android.support.v4.app.Fragment {
    ListView listView;
    public static ArrayList<ListItem> items;
    protected static MyAdapter myAdapter;
    int cardid;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cards,container,false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Cards");

        listView=(ListView)view.findViewById(R.id.listview);
        items=new ArrayList<ListItem>();

        myAdapter=new MyAdapter(this.getActivity(),items);
        new searchtask().execute(HomepageActivity.username);
        listView.setAdapter(myAdapter);

        setupListViewListener();
    }
    static class searchtask extends AsyncTask<String,Void,ArrayList<ListItem>> {
        @Override
        protected ArrayList<ListItem> doInBackground(String... params) {
            try{
                CloudStorageAccount storageAccount=CloudStorageAccount.parse(NetworkTool.storageConnectionString);
                CloudTableClient tableClient = storageAccount.createCloudTableClient();
                CloudTable cloudTable = tableClient.getTableReference("card");
                String partitionFilter = TableQuery.generateFilterCondition(
                        "PartitionKey",
                        TableQuery.QueryComparisons.EQUAL,
                        params[0]);
                TableQuery<CardEntity> partitionQuery =
                        TableQuery.from(CardEntity.class)
                                .where(partitionFilter);
                ArrayList<ListItem> tempitems=new ArrayList<ListItem>();
                for (CardEntity entity : cloudTable.execute(partitionQuery)) {
                    ListItem listItem=new ListItem(entity.getRowKey(),entity.getCompany());
                    tempitems.add(listItem);
                }
                return tempitems;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(ArrayList<ListItem> tempitems) {
            for(ListItem entity:tempitems) {
                items.add(entity);
            }
            myAdapter.notifyDataSetChanged();
        }
    }

    class deletetask extends AsyncTask<String,Void,Void> {
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
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
    }

    private void setupListViewListener() {
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long rowId){
                if(NetworkTool.isNetworkConnected(getActivity())) {
                    registerForContextMenu(listView);
                    cardid = position;
                }
                return false;
            }     });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(NetworkTool.isNetworkConnected(getActivity())) {
                    Intent intent=new Intent(getActivity(),DisplayActivity.class);
                    intent.putExtra("number",items.get(position).getNumber());
                    intent.putExtra("company",items.get(position).getCompany());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onCreateContextMenu(android.view.ContextMenu menu, View v, android.view.ContextMenu.ContextMenuInfo menuInfo) {
        menu.setHeaderTitle("Delete or Modify");
        menu.add(0, 0, 0, "Delete");
        menu.add(0,1,0,"Modify");
    };

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //Delete
            case 0:
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.dialog_delete_title)
                        .setMessage(R.string.dialog_delete_msg)
                        .setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                new deletetask().execute(items.get(cardid).getNumber());
                                refresh();
                            }                 })
                        .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                            }                 });
                builder.create().show();
                break;
            //Modify
            case 1:
                    Intent intent=new Intent(getActivity(),CaptureActivity.class);
                    intent.putExtra("number",items.get(cardid).getNumber());
                    intent.putExtra("company",items.get(cardid).getCompany());
                    intent.putExtra("type","modify");
                    startActivity(intent);
                break;
        }
        return true;
    };
    public static void refresh(){
        items.clear();
        new searchtask().execute(HomepageActivity.username);
    }
}
