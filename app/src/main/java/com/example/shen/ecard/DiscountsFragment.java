package com.example.shen.ecard;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.microsoft.azure.storage.CloudStorageAccount;
import com.microsoft.azure.storage.blob.CloudBlob;
import com.microsoft.azure.storage.blob.CloudBlobClient;
import com.microsoft.azure.storage.blob.CloudBlobContainer;
import com.microsoft.azure.storage.blob.ListBlobItem;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardGridView;


public class DiscountsFragment extends Fragment {

    CardArrayAdapter cardArrayAdapter;
    CardGridView cardGridView;

    ArrayList<Card> cards;
    ProgressBar progressBar;

    File[] files;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_discounts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getActivity().setTitle("Discounts");
        try {
            FileUtils.deleteDirectory(new File(NetworkTool.getroot(getActivity())+"/"));
        }
        catch (IOException e){

        }
        cardGridView = (CardGridView) getActivity().findViewById(R.id.carddemo);
        progressBar=(ProgressBar)getActivity().findViewById(R.id.progressBar);


        cards=new ArrayList<Card>();
        cardArrayAdapter=new CardArrayAdapter(getActivity(),cards);
        cardGridView.setAdapter(cardArrayAdapter);
        cardGridView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        new task().execute(HomepageActivity.username);
    }

    class task extends AsyncTask<String,Void,Boolean> {
        @Override
        protected Boolean doInBackground(String... params) {
            try{
                CloudStorageAccount storageAccount=CloudStorageAccount.parse(NetworkTool.storageConnectionString);
                CloudBlobClient blobClient = storageAccount.createCloudBlobClient();
                CloudBlobContainer container = blobClient.getContainerReference("discounts");
                for (ListBlobItem blobItem : container.listBlobs()) {
                    if (blobItem instanceof CloudBlob) {
                        CloudBlob blob = (CloudBlob) blobItem;
                        blob.download(new FileOutputStream(NetworkTool.getroot(getActivity())+"/" + blob.getName()));
                    }
                }
                return true;
            }
            catch (Exception e){
                e.printStackTrace();
            }
            return null;
        }
        protected void onPostExecute(Boolean result) {
            FilenameFilter pngFilter = new FilenameFilter() {
                public boolean accept(File file, String name) {
                    if (name.endsWith(".png")||name.endsWith(".jpg")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            };
            File allfile=new File(NetworkTool.getroot(getActivity())+"/");
            files=allfile.listFiles(pngFilter);
            for (final File afile:files){
                Drawable d = Drawable.createFromPath(afile.getAbsolutePath());
                Card card=new Card(getActivity());
                card.setOnClickListener(new Card.OnCardClickListener() {
                    @Override
                    public void onClick(Card card, View view) {
                        Intent intent = new Intent();
                        intent.setAction(Intent.ACTION_VIEW);
                        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        intent.setDataAndType(Uri.parse("file://" +afile.getAbsolutePath()), "image/*");
                        startActivity(intent);
                    }
                });
                card.setBackgroundResource(d);
                cards.add(card);
            }
            progressBar.setVisibility(View.GONE);
            cardArrayAdapter.notifyDataSetChanged();
        }
    }
}
