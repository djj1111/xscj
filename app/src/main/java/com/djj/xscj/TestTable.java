package com.djj.xscj;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

import java.util.ArrayList;

/**
 * Created by djj on 2016/11/13.
 */

@Table(name = "TestTable", onCreated = "CREATE INDEX index_user ON TestTable(user);" +
        "CREATE INDEX index_user_inputdate ON TestTable(user,inputdate)")
public class TestTable implements Parcelable {

    public static final Creator<TestTable> CREATOR = new Creator<TestTable>() {
        public TestTable createFromParcel(Parcel in) {
            return new TestTable(in);
        }

        public TestTable[] newArray(int size) {
            return new TestTable[size];
        }
    };
    private static int count = 0;
    @Column(name = "id", isId = true,autoGen = false)
    private int id;
    /*@Column(name = "mid")
    private int mid;*/
    @Column(name = "inputdate")
    private String inputdate;
    @Column(name = "user")
    private String user;
    @Column(name = "num")
    private String num;
    @Column(name = "cnum")
    private String cnum;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "cellphone")
    private String cellphone;
    @Column(name = "phone")
    private String phone;
    @Column(name = "year")
    private String year;
    @Column(name = "month")
    private String month;
    @Column(name = "money")
    private String money;
    /*@Column(name = "filepath")
    private String filepath;*/
    @Column(name = "filenums")
    private int filenums;
    @Column(name = "imei")
    private String imei;
    //private boolean isadd = true, isdelete = false, isupdate = false;
    private int serialsnum;
    private ArrayList<String> filepath;

    private TestTable(Parcel in) {

        serialsnum = in.readInt();
        id = in.readInt();
        //mid=in.readInt();
        filenums=in.readInt();
        String s[] = new String[12];
        in.readStringArray(s);
        inputdate=s[0];
        user=s[1];
        num= s[2];
        cnum=s[3];
        name =s[4];
        address = s[5];
        cellphone = s[6];
        phone=s[7];
        year= s[8];
        month= s[9];
        money= s[10];
        imei= s[11];
        filepath=in.createStringArrayList();
    }

    public TestTable() {
        count++;
        serialsnum = count;
        filepath=new ArrayList<>();
    }

    // 一对一
    //public Child getChild(DbManager db) throws DbException {
    //    return db.selector(Child.class).where("parentId", "=", this.id).findFirst();
    //}
    public void setid(int id){
        this.id=id;
    }
    public int getid(){
        return id;
    }
    public void setSerialsnum(int id){
        this.serialsnum=id;
    }
    public int getSerialsnum(){
        return serialsnum;
    }
    /*public void setMid(int id){
        this.mid=id;
    }
    public int getMid(){
        return mid;
    }*/
    public void setFilenums(int filenums){
        this.filenums=filenums;
    }
    public int getFilenums(){
        return filenums;
    }
    public void setInputdate(String s){
        inputdate=s;
    }
    public String getInputdate(){
        return inputdate;
    }
    public void setUser(String s){
        user=s;
    }
    public String getUser(){
        return user;
    }
    public void setNum(String s){
        num=s;
    }
    public String getNum(){
        return num;
    }
    public void setCnum(String s){
        cnum=s;
    }
    public String getCnum(){
        return cnum;
    }
    public void setName(String s){
        name=s;
    }
    public String getName(){
        return name;
    }
    public void setAddress(String s){
        address=s;
    }
    public String getAddress(){
        return address;
    }
    public void setCellphone(String s){
        cellphone=s;
    }
    public String getCellphone(){
        return cellphone;
    }
    public void setPhone(String s){
        phone=s;
    }
    public String getPhone(){
        return phone;
    }
    public void setYear(String s){
        year=s;
    }
    public String getYear(){
        return year;
    }
    public void setMonth(String s){
        month=s;
    }
    public String getMonth(){
        return month;
    }
    public void setMoney(String s){
        money=s;
    }
    public String getMoney(){
        return money;
    }
    public void setImei(String s){
        imei=s;
    }
    public String getImei(){
        return imei;
    }
    public void addfilepath(String s){
        filepath.add(s);
        filenums+=1;
    }
    public ArrayList<String> getfilepath(){
        return filepath;
    }
   /* public void setFilepath(ArrayList s){
        if (filepath==null) filepath=new ArrayList<>();
        filepath.addAll(s);
    }*/


    /*public void clone(TestTable t){
        id=t.getId();
        address=t.getAddress();
        phone=t.getPhone();
        name=t.getName();
        isdelete=t.getIsdelete();
        isupdate=t.getIsupdate();
        isadd=t.getIsadd();
    }*/



    @Override
    public String toString() {
        return "TestTable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", addrress='" + address + '\'' +
                ", phone=" + phone +
                '}';
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        //boolean b[] = new boolean[]{isadd, isdelete, isupdate};
        //out.writeBooleanArray(b);
        out.writeInt(serialsnum);
        out.writeInt(id);
        //out.writeInt(mid);
        out.writeInt(filenums);
        String s[] = new String[]{inputdate,user,num,cnum,name, address, cellphone,phone,year,month,money,imei};
        out.writeStringArray(s);
        out.writeStringList(filepath);
    }
}