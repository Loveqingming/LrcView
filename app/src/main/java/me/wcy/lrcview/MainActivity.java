package me.wcy.lrcview;

import android.content.res.AssetFileDescriptor;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements MediaPlayer.OnPreparedListener {
    private LrcView mLrcView;
    private MediaPlayer mMediaPlayer;
    private String mMp3Name;//歌曲歌词文件放在assets下
    private String mLrcName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLrcView = (LrcView) findViewById(R.id.lrc);

        mMediaPlayer = new MediaPlayer();
        mMp3Name = "mp3.mp3";
        mLrcName = "lrc.lrc";

        try {
            //播放歌曲
            AssetFileDescriptor afd = getAssets().openFd(mMp3Name);
            mMediaPlayer.setDataSource(afd.getFileDescriptor());
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync();

            //加载歌词
            mLrcView.loadLrc(mLrcName);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onPrepared(MediaPlayer mp) {
        mMediaPlayer.start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (mMediaPlayer.isPlaying()) {
                    mLrcView.updateTime(mMediaPlayer.getCurrentPosition());

                    //每隔100ms刷新一次
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDestroy() {
        mMediaPlayer.stop();
        super.onDestroy();
    }
}
