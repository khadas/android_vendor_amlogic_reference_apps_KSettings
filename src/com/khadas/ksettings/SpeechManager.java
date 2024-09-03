package com.khadas.ksettings;

import android.os.Build;
import android.speech.tts.TextToSpeech;
import android.text.TextUtils;
import android.util.Log;

import java.util.Locale;

/**
 *  语音播报管理类（目前是采用谷歌原生的语音引擎，android 5.1上支持中文播报）
 * Created by york on 2018/12/06.
 */
public class SpeechManager {
    private static String TAG = "tts";

    public static final String DEFAULT_TTS_ENGINE_PKG_NAME = "com.google.android.tts";//默认语音引擎包名

    public static SpeechManager mInstance = null;
    private TextToSpeech mTextToSpeech = null;
    private boolean isTextToSpeechInitSuccess = false;

    public static SpeechManager getInstance(){
        if(mInstance == null){
            mInstance = new SpeechManager();
        }
        return mInstance;
    }

    /**
     * 初始化语音引擎
     */
    public void initSpeechEngine() {
        if(mTextToSpeech != null && !isTextToSpeechInitSuccess){
            mTextToSpeech = null;
        }

        if(mTextToSpeech == null){
            mTextToSpeech = new TextToSpeech(Kvim4ToolsService.mContext, mInitListener,DEFAULT_TTS_ENGINE_PKG_NAME);
        }
    }

    TextToSpeech.OnInitListener mInitListener = new TextToSpeech.OnInitListener() {
        @Override
        public void onInit(int status) {
            Log.v(TAG,"----- initSpeechEngine();status:"+status);
            if (status == TextToSpeech.SUCCESS) {
//                int result = mTextToSpeech.setLanguage(Locale.CHINESE/*ENGLISH*//*.CHINA*/);
//                Log.v(TAG,"----- initSpeechEngine();result:"+result);
//                if (result == TextToSpeech.LANG_COUNTRY_AVAILABLE
//                        || result == TextToSpeech.LANG_AVAILABLE){
//                    onSpeechInitSuccess();
//                }else{
//                    isTextToSpeechInitSuccess = false;
//                }
                onSpeechInitSuccess();

            }else{
                isTextToSpeechInitSuccess = false;
            }
        }
    };
    /**
     * 语音引擎初始化成功
     */
    private void onSpeechInitSuccess(){
        setSpeechRate(1.0f);
        setSpeechPitch(1.1f);

        isTextToSpeechInitSuccess = true;
    }

    /**
     * tts播报（speak接口默认是采用队列方式顺序播放的，如果有要求停止当前播放队列内容播放，可以调用stop接口）
     * @param tts 播报内容
     */
    public void playTts(String tts){
        if(mTextToSpeech == null || !isTextToSpeechInitSuccess){
            initSpeechEngine();
        }

        if(mTextToSpeech != null && !TextUtils.isEmpty(tts)) {
            Log.v(TAG,"===playTts()-->tts:"+tts);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                Log.d(TAG, "===tts.play: " + tts);
                mTextToSpeech.speak(tts, TextToSpeech.QUEUE_ADD, null, null);
            } else {
                Log.d(TAG, "====tts.play: " + tts);
                mTextToSpeech.speak(tts, TextToSpeech.QUEUE_ADD, null);
            }
        }
    }

    /**
     * tts播报
     * @param tts 播报内容
     * @param isNeedToStopCurrentSpeaking 是否打断当前播放内容，最高优先级播放
     */
    public void playTts(String tts, boolean isNeedToStopCurrentSpeaking){
        if(mTextToSpeech == null || !isTextToSpeechInitSuccess){
            initSpeechEngine();
        }

        if(mTextToSpeech != null && !TextUtils.isEmpty(tts)) {
            Log.v(TAG,"---- playTts();tts:"+tts);
            if(isNeedToStopCurrentSpeaking && mTextToSpeech.isSpeaking()){
                Log.v(TAG,"---- playTts();current is speaking,just stop it,and start a new one.");
                mTextToSpeech.stop();
            }

            mTextToSpeech.speak(tts,
                    TextToSpeech.QUEUE_ADD, null);
        }
    }

    /**
     * 设置语速
     * @param rate
     */
    private void setSpeechRate(float rate){
        if(mTextToSpeech != null){
            mTextToSpeech.setSpeechRate(rate);
        }
    }

    /**
     * 设置音高
     * @param pitch
     */
    private void setSpeechPitch(float pitch){
        if(mTextToSpeech != null){
            mTextToSpeech.setPitch(pitch);
        }
    }

    /**
     * 销毁播报
     */
    public void destory(){
        if(mTextToSpeech != null){
            mTextToSpeech.stop();
            mTextToSpeech.shutdown();
            mTextToSpeech = null;
            Log.v(TAG,"destory();textToSpeech shutdown success!!!");
        }
    }
}
