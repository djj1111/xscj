package com.djj.xscj;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.djj1111.android.filetools.SDCardScanner;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import static android.app.Activity.RESULT_OK;

/**
 * Created by djj on 2016/12/4.
 */

public class WorkFragment extends Fragment {

    public final static String TAG = "WorkFragment";
    private final static int TYPE_FILE_IMAGE = 1, TYPE_FILE_VEDIO = 2, TYPE_FILE_AUDIO = 3, SYSTEM_CAMERA_REQUESTCODE = 11;

    private TextView tinputdate, tcnum, tnum, tname, taddress, tcellphone, tphone, tyear, tmonth, tmoney, tfilenums;
    private Button bphoto, brecord;
    private View item;

    private TestTable mTestTable;
    private String photofile, audiofile;
    private SoundPool mPool;
    private int VoicID;
    private Drawable backgroundcolor;
    //private View item;
    //private EditText textid, texttext, textip, texttime;

    public static WorkFragment getInstances() {
        WorkFragment myFragment = new WorkFragment();
        return myFragment;
    }

    /*@Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        *//*if (savedInstanceState == null) {
            mTestTable = this.getArguments().getParcelable("mTestTable");
        } else {
            mTestTable = savedInstanceState.getParcelable("mTestTable");
        }*//*


    }*/

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Log.d(TAG, "onCreateView  mData = " + mData);
        // String str = "";
        /*if (savedInstanceState != null) {
            mTestTable=new TestTable();
            mTestTable.setId(savedInstanceState.getInt("id"));
            mTestTable.setName(savedInstanceState.getString("name"));
            mTestTable.setAddress(savedInstanceState.getString("address"));
            mTestTable.setPhone(savedInstanceState.getString("phone"));
        }*/
        mTestTable = this.getArguments().getParcelable("mTestTable");
        // 这里必须是null
        //Log.d("Myfragment","count="+count);
        //Toast.makeText(getActivity(),"serials="+mTestTable.getSerialsnum(), Toast.LENGTH_SHORT).show();
        /*if (savedInstanceState != null)
            mTestTable = savedInstanceState.getParcelable("mTestTable");*/

        View item = inflater.inflate(R.layout.workfragment_item, container, false);
        tinputdate = (TextView) item.findViewById(R.id.tvinputdate);
        tcnum = (TextView) item.findViewById(R.id.tvcnum);
        tnum = (TextView) item.findViewById(R.id.tvnum);
        tname = (TextView) item.findViewById(R.id.tvname);
        taddress = (TextView) item.findViewById(R.id.tvaddress);
        tcellphone = (TextView) item.findViewById(R.id.tvcellphone);
        tphone = (TextView) item.findViewById(R.id.tvphone);
        tyear = (TextView) item.findViewById(R.id.tvyear);
        tmonth = (TextView) item.findViewById(R.id.tvmonth);
        tmoney = (TextView) item.findViewById(R.id.tvmoney);
        tfilenums = (TextView) item.findViewById(R.id.tvfilenums);
        tinputdate.setText(mTestTable.getInputdate());
        bphoto = (Button) item.findViewById(R.id.button_photo);
        brecord = (Button) item.findViewById(R.id.button_record);
        backgroundcolor = brecord.getBackground();
        bphoto.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                getphoto();
            }
        });
        brecord.setOnTouchListener(new Button.OnTouchListener() {
            private long lastTime = 0;

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final long currentTime = Calendar.getInstance().getTimeInMillis();
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    if (isrecordstop){
                        if (currentTime - lastTime < 1000) {
                            //lastTime=currentTime;
                            return true;
                        } else {
                            lastTime = currentTime;
                            audiofile = startrecord(getOutFile(TYPE_FILE_AUDIO));
                            return true;
                        }
                    }
                    return true;
                }
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    final long time = currentTime - lastTime;
                    if (!isrecordstop) {
                        if (time < 1000) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        Thread.sleep(1000 - time);
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                    stoprecord();
                                    lastTime = Calendar.getInstance().getTimeInMillis();
                                }
                            }).start();

                        } else {
                            stoprecord();
                            lastTime = Calendar.getInstance().getTimeInMillis();
                        }
                    }

                    return true;
                }
                return true;
            }
        });
        VoicID = initSoundPool();

        return item;
    }

    private int initSoundPool() {
        /**
         * 21版本后，SoundPool的创建发生很大改变
         */
        //判断系统sdk版本，如果版本超过21，调用第一种
        if (Build.VERSION.SDK_INT >= 21) {
            SoundPool.Builder builder = new SoundPool.Builder();
            builder.setMaxStreams(1);//传入音频数量
            //AudioAttributes是一个封装音频各种属性的方法
            AudioAttributes.Builder attrBuilder = new AudioAttributes.Builder();
            attrBuilder.setLegacyStreamType(AudioManager.STREAM_MUSIC);//设置音频流的合适的属性
            builder.setAudioAttributes(attrBuilder.build());//加载一个AudioAttributes
            mPool = builder.build();
        } else {
            mPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        }
        //load的返回值是一个int类的值：音频的id，在SoundPool的play()方法中加入这个id就能播放这个音频
        return mPool.load(getContext(), R.raw.reset_sound, 1);
    }

    private void getphoto() {
        Intent intent = new Intent();
        intent.setAction(MediaStore.ACTION_IMAGE_CAPTURE);
        Uri imageFileUri = getOutFileUri(TYPE_FILE_IMAGE);//得到一个File Uri
        photofile = imageFileUri.getPath();
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageFileUri);
        startActivityForResult(intent, SYSTEM_CAMERA_REQUESTCODE);
    }

    private Uri getOutFileUri(int fileType) {
        return Uri.fromFile(getOutFile(fileType));
    }

    @Nullable
    private File getOutFile(int fileType) {
        List<String> SDcard = SDCardScanner.getExtSDCardPaths();
        if (SDcard.isEmpty()) {
            Toast.makeText(getActivity(), "无内存卡", Toast.LENGTH_SHORT).show();
            return null;
        }
        File mediaStorageDir = new File(SDcard.get(0), ".wxwaterwork_cb");
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Toast.makeText(getActivity(), "创建文件存储路径目录失败", Toast.LENGTH_SHORT).show();
                return null;
            }
        }
        File file = new File(getFilePath(mediaStorageDir, fileType));
        return file;
    }

    @Nullable
    private String getFilePath(File mediaStorageDir, int fileType) {
        TelephonyManager telephonyManager = (TelephonyManager) getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        String imei = telephonyManager.getDeviceId();
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        //file.separator 目录分隔符
        String filePath = mediaStorageDir.getPath() + File.separator;
        if (fileType == TYPE_FILE_IMAGE) {
            filePath += (mTestTable.getNum() + timeStamp + ".jpg");
        } else if (fileType == TYPE_FILE_VEDIO) {
            filePath += (mTestTable.getNum() + timeStamp + ".mp4");
        } else if (fileType == TYPE_FILE_AUDIO) {
            filePath += (mTestTable.getNum() + timeStamp + ".aac");
        } else {
            return null;
        }
        return filePath;
    }

    private MediaRecorder myRecorder;

    @Nullable
    private String startrecord(File file) {
        isrecordstop = false;
        myRecorder = new MediaRecorder();
        // 从麦克风源进行录音
        myRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        // 设置输出格式
        myRecorder.setOutputFormat(MediaRecorder.OutputFormat.AAC_ADTS);
        // 设置编码格式
        myRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);

        try {//file.createNewFile();
            myRecorder.setOutputFile(file.getAbsolutePath());
            myRecorder.prepare();
            // 开始录音
            myRecorder.start();

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        mPool.play(VoicID, 1, 1, 0, 0, 1);
        brecord.setBackgroundColor(getResources().getColor(android.R.color.holo_orange_dark));
        return file.getAbsolutePath();
    }

    private boolean isrecordstop = true;

    private void stoprecord() {
        boolean haveerror;
        try {
            myRecorder.stop();
            myRecorder.release();
            haveerror = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
            haveerror = true;
        }
        if (!haveerror) {
            if (audiofile != null) {
                mTestTable.addfilepath(audiofile);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        tfilenums.setText(String.valueOf(mTestTable.getFilenums()));
                    }
                });
            }
        } else {
            if (audiofile != null) {
                File f = new File(audiofile);
                if (f.exists()) f.delete();
            }
        }
        audiofile = null;
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                brecord.setBackground(backgroundcolor);
            }
        });
        isrecordstop = true;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SYSTEM_CAMERA_REQUESTCODE) {
            if (resultCode == RESULT_OK) {
                mTestTable.addfilepath(photofile);
                tfilenums.setText(String.valueOf(mTestTable.getFilenums()));
            } else {
                Toast.makeText(getActivity(), "拍照不成功", Toast.LENGTH_SHORT).show();
            }
            photofile = null;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /*@Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable("mTestTable", mTestTable);
    }*/

    @Override
    public void onDestroyView() {
        this.getArguments().putParcelable("mTestTable", mTestTable);
        super.onDestroyView();
    }

   /* public TestTable getTestTable() {
        return mTestTable;
    }*/
}
