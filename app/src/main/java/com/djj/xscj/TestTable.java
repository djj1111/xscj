package com.djj.xscj;

import android.os.Parcel;
import android.os.Parcelable;

import org.xutils.db.annotation.Column;
import org.xutils.db.annotation.Table;

/**
 * Created by djj on 2016/11/13.
 */

@Table(name = "TestTable", onCreated = "CREATE UNIQUE INDEX index_name ON TestTable(name,address,phone)")
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
    @Column(name = "id", isId = true)
    private int id;
    @Column(name = "inputdate")
    private String inputdate;
    @Column(name = "num")
    private String num;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;
    @Column(name = "phone")
    private String phone;
    @Column(name = "year")
    private String year;
    @Column(name = "month")
    private String month;
    @Column(name = "money")
    private String money;
    @Column(name = "filepath")
    private String filepath;
    //private boolean isadd = true, isdelete = false, isupdate = false;
    private int serialsnum;

    private TestTable(Parcel in) {
        //count++;
        /*boolean b[] = new boolean[3];
        in.readBooleanArray(b);*/
        serialsnum = in.readInt();
        id = in.readInt();
        String s[] = new String[9];
        in.readStringArray(s);
        /*isadd = b[0];
        isdelete = b[1];
        isupdate = b[2];*/
        inputdate=s[0];
        num= s[1];
        name =s[2];
        address = s[3];
        phone = s[4];
        year= s[5];
        month= s[6];
        money= s[7];
        filepath= s[8];
    }

    public TestTable() {
        count++;
        serialsnum = count;
    }

    // 一对一
    //public Child getChild(DbManager db) throws DbException {
    //    return db.selector(Child.class).where("parentId", "=", this.id).findFirst();
    //}
    public int getSerialsnum() {
        return serialsnum;
    }

    /*public boolean getIsadd() {
        return isadd;
    }

    public void setIsadd(boolean b) {
        this.isadd = b;
    }

    public boolean getIsdelete() {
        return isdelete;
    }

    public void setIsdelete(boolean b) {
        this.isdelete = b;
    }

    public boolean getIsupdate() {
        return isupdate;
    }

    public void setIsupdate(boolean b) {
        this.isupdate = b;
    }*/

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }
    public String getInputdate(){
        return inputdate;
    }
    public void setInputdate(String s){
        inputdate=s;
    }

    /*public void clone(TestTable t){
        id=t.getId();
        address=t.getAddress();
        phone=t.getPhone();
        name=t.getName();
        isdelete=t.getIsdelete();
        isupdate=t.getIsupdate();
        isadd=t.getIsadd();
    }*/

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

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
        String s[] = new String[]{inputdate,num,name, address, phone,year,month,money,filepath};
        out.writeStringArray(s);
    }
}