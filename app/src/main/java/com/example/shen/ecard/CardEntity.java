package com.example.shen.ecard;

import com.microsoft.azure.storage.table.TableServiceEntity;


public class CardEntity extends TableServiceEntity {
    public CardEntity(String partitionkey,String rowkey){
        this.partitionKey=partitionkey;
        this.rowKey=rowkey;
    }
    public CardEntity(){}

    String company;

    public String getCompany(){
        return this.company;
    }

    public void setCompany(String company){
        this.company=company;
    }
}
