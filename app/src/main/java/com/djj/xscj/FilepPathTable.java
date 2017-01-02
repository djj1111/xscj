package com.djj.xscj;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by djj on 2017/1/1.
 */

@Table(name = "FilepPathTable", onCreated = "CREATE INDEX index_mid ON FilepPathTable(mid)")
public class FilepPathTable {
   /* @Column(name = "id",isId = true)
    private int id;*/
   @Column(name = "filepath",isId = true)
   private String filepath;
    @Column(name = "mid")
    private int mid;


    public FilepPathTable() {
    }
    public void setmId(int id1){
        mid=id1;
    }
    public int getmId(){
        return mid;
    }
    public void setFilepath(String s){
        filepath=s;
    }
    public String getFilepath(){
        return filepath;
    }

    @Override
    public String toString() {
        return "mid="+mid+"filepath="+filepath;
    }
}
