package com.djj.xscj;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by djj on 2016/12/21.
 */

public class ListTestTable extends Object implements Parcelable {
    public static final Creator<ListTestTable> CREATOR = new Creator<ListTestTable>() {
        public ListTestTable createFromParcel(Parcel in) {
            return new ListTestTable(in);
        }

        public ListTestTable[] newArray(int size) {
            return new ListTestTable[size];
        }
    };
    private ArrayList<TestTable> mListTestTable;
    public ListTestTable(){
        mListTestTable=new ArrayList<>();
    }
    public boolean add(TestTable t){
        return mListTestTable.add(t);
    }
    public boolean revome(TestTable t){
        return mListTestTable.remove(t);
    }
    public boolean addall(ArrayList<TestTable> t){
        return mListTestTable.addAll(t);
    }
    protected ListTestTable(Parcel in) {
        TestTable[] temp;
        Parcelable[] parcelables = in.readParcelableArray(TestTable.class.getClassLoader());
        if (parcelables != null) {
            temp = Arrays.copyOf(parcelables, parcelables.length, TestTable[].class);
            mListTestTable=(ArrayList<TestTable>) Arrays.asList(temp);
        }
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelableArray(mListTestTable.toArray(new TestTable[mListTestTable.size()]),flags);
    }

    @Override
    public String toString() {
        //return super.toString();
        String s;
        if (mListTestTable.isEmpty()) {
            s="无内容";
        }else {
            s=mListTestTable.get(0).getInputdate()+"导入，共"+mListTestTable.size()+"条";
        }
        return s;
    }
}
