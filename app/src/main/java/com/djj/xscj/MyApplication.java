package com.djj.xscj;

import android.app.Application;

import org.xutils.DbManager;
import org.xutils.x;

/**
 * Created by djj on 2016/11/13.
 */

public class MyApplication extends Application {
    private DbManager.DaoConfig daoConfig;
    private String username, password, ip;
    private int port;
    public DbManager.DaoConfig getDaoConfig() {
        return daoConfig;
    }
    public String getUsername(){
        return username;
    }
    public void setUsername(String username){
        this.username=username;
    }
    public String getPassword(){
        return password;
    }
    public void setPassword(String s){
        password=s;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String s) {
        this.ip = s;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int s) {
        port = s;
    }



    @Override
    public void onCreate() {
        super.onCreate();
        x.Ext.init(this);
        daoConfig = new DbManager.DaoConfig()
                .setDbName("test.db")
                // 不设置dbDir时, 默认存储在app的私有目录.
                .setDbVersion(2)
                .setDbOpenListener(new DbManager.DbOpenListener() {
                    @Override
                    public void onDbOpened(DbManager db) {
                        // 开启WAL, 对写入加速提升巨大
                        db.getDatabase().enableWriteAheadLogging();
                    }
                })
                .setDbUpgradeListener(new DbManager.DbUpgradeListener() {
                    @Override
                    public void onUpgrade(DbManager db, int oldVersion, int newVersion) {
                        // TODO: ...
                        // db.addColumn(...);
                        // db.dropTable(...);
                        // ...
                        // or
                        // db.dropDb();
                    }
                });
    }

}
