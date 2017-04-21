package com.lib.meteorplayer;

import android.content.Context;
import android.media.AudioManager;
import android.media.MediaPlayer;

import java.io.File;
import java.io.IOException;

/**
 * 用于播放小而短的声音
 * 比如
 * "开启灯光控制"
 * "关闭灯光控制"
 *
 * @author EthanCo
 * @since 2017/4/20
 */

public class MeteorPlayer implements AudioManager.OnAudioFocusChangeListener {
    private final Context context;
    private MediaPlayer mediaPlayer;
    private boolean isPrepared;
    private boolean isPaused;
    private AudioFocus audioFocus;
    private String recordSongPath;

    public MeteorPlayer(Context context) {
        this.isPrepared = false;
        this.context = context;
    }

    //初始化 (懒加载)
    private void inspectInit() {
        if (mediaPlayer == null) {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mediaPlayer) {
                    isPrepared = true;
                    play();
                }
            });
        }
        if (audioFocus == null) {
            audioFocus = new AudioFocus(this, context);
        }
    }

    //恢复播放或开始播放
    private boolean play() {
        if (audioFocus != null) {
            audioFocus.requestFocus();
        }
        mediaPlayer.start();
        return true;
    }

    public boolean play(String filePath) {
        return play(new File(filePath));
    }

    public boolean play(File file) {
        if (!file.exists()) {
            LogUtil.w("文件不存在");
            return false;
        }
        if (!isSupportGenre(file)) {
            LogUtil.w("不支持该类型:" + file.getName());
            return false;
        }

        inspectInit();

        String filePath = file.getPath();
        if (isPaused && recordSongPath.equals(filePath)) {
            return play();
        }

        if (isPrepared) {
            mediaPlayer.reset();
        }

        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepareAsync();
            recordSongPath = filePath;
            return true;
        } catch (IOException e) {
            LogUtil.e("setDataSource错误:" + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public void pause() {
        if (mediaPlayer == null) return;
        if (audioFocus != null) {
            audioFocus.abandonFocus();
        }

        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            isPaused = true;
        }
    }

    private boolean isSupportGenre(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp3") ||
                fileName.endsWith(".wma") ||
                fileName.endsWith(".wav") ||
                fileName.endsWith(".ogg");
    }

    @Override
    public void onAudioFocusChange(int focusChange) {
        if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
            //失去焦点之后的操作
            //do nothing
        } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
            //获得焦点之后的操作
            //do nothing
        }
    }
}