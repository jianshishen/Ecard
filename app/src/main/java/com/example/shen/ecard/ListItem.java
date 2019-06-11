package com.example.shen.ecard;

public class ListItem {
    String number;
    String company;

    public void setNumber(String number){
        this.number=number;
    }

    public String getNumber(){
        return number;
    }

    public void setCompany(String company){
        this.company=company;
    }

    public String getCompany(){
        return company;
    }

    public ListItem(){}

    public ListItem(String number,String company){
        this.number=number;
        this.company=company;
    }
}
