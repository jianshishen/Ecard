package com.example.shen.ecard;

import com.microsoft.azure.storage.table.TableServiceEntity;

public class UserEntity extends TableServiceEntity {
    public UserEntity(String partitionkey,String rowkey){
        this.partitionKey=partitionkey;
        this.rowKey=rowkey;
    }
    public UserEntity(){}

    String password;

    public String getPassword(){
        return this.password;
    }

    public void setPassword(String password){
        this.password=password;
    }
}
