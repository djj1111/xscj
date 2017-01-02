package com.djj.xscj;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

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
    public int size(){
        return mListTestTable.size();
    }
    public boolean addall(ArrayList<TestTable> t){
        return mListTestTable.addAll(t);
    }
    private ListTestTable(Parcel in) {
        /*TestTable[] temp;
        Parcelable[] parcelables = in.readParcelableArray(TestTable.class.getClassLoader());
        if (parcelables != null) {
            temp = Arrays.copyOf(parcelables, parcelables.length, TestTable[].class);
            mListTestTable=(ArrayList<TestTable>) Arrays.asList(temp);
        }*/
        ArrayList<TestTable> parts = new ArrayList<TestTable>();
        in.readTypedList(parts, TestTable.CREATOR);
        mListTestTable=parts;
        /*DetectedFace face = new DetectedFace();

        face.facePart = facePart;
        face.nosePart = nosePart;
        face.eyePart = eyePart;
        face.eyebrowPart = eyebrowPart;
        face.mouthPart = mouthPart;

        face.parts = parts;

        return face;*/

    }
    public void setListTestTable(ArrayList<TestTable> mListTestTable){
        this.mListTestTable=mListTestTable;
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel out, int flags) {
        //out.writeParcelableArray(mListTestTable.toArray(new TestTable[mListTestTable.size()]),flags);
        out.writeTypedList(mListTestTable);
    }
    public ArrayList<TestTable> getListTestTable(){
        return mListTestTable;
    }
    public int gethasfileitemnums(){
        int filecoun=0;
        for(TestTable t : mListTestTable){
            if(t.getFilenums()>0){
                filecoun++;
            }
        }
        return filecoun;
    }

    @Override
    public String toString() {
        //return super.toString();
        String s;

        if (mListTestTable.isEmpty()) {
            s="无内容";
        }else {
            s=mListTestTable.get(0).getInputdate()+"导入\n已完成"+gethasfileitemnums()+"条/共"+mListTestTable.size()+"条";
        }
        return s;
    }
}
