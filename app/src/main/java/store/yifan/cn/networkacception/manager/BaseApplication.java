package store.yifan.cn.networkacception.manager;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;

import store.yifan.cn.networkacception.utils.SpeechSynthesisUtil;

/**
 * Created by mwqi on 2014/7/11.
 */
public class BaseApplication extends Application {
    /**
     * 全局Context，原理是因为Application类是应用最先运行的，所以在我们的代码调用时，该值已经被赋值过了
     */
    private static BaseApplication mInstance;
    /**
     * 主线程ID
     */
    private static int mMainThreadId = -1;
    /**
     * 主线程ID
     */
    private static Thread mMainThread;
    /**
     * 主线程Handler
     */
    private static Handler mMainThreadHandler;
    /**
     * 主线程Looper
     */
    private static Looper mMainLooper;
    /**
     * AcceptToken
     */
    private static String AcceptToken;
    @Override
    public void onCreate() {
        mMainThreadId = android.os.Process.myTid();
        mMainThread = Thread.currentThread();
        mMainThreadHandler = new Handler();
        mMainLooper = getMainLooper();
        mInstance = this;
        initSpeechExecute();
        super.onCreate();
    }

    /**
     * 初始化 语音功能
     */
    private void initSpeechExecute() {
        // TODO Auto-generated method stub
        if (Constants.speechUtil == null) {


            SpeechUtility.createUtility(getApplicationContext(),
                    SpeechConstant.APPID + "=5485587a");

            Constants.speechUtil = new SpeechSynthesisUtil(getApplicationContext());
        }
    }

    public static BaseApplication getApplication() {
        return mInstance;
    }

    /**
     * 获取主线程ID
     */
    public static int getMainThreadId() {
        return mMainThreadId;
    }

    /**
     * 获取主线程
     */
    public static Thread getMainThread() {
        return mMainThread;
    }

    /**
     * 获取主线程的handler
     */
    public static Handler getMainThreadHandler() {
        return mMainThreadHandler;
    }

    /**
     * 获取主线程的looper
     */
    public static Looper getMainThreadLooper() {
        return mMainLooper;
    }

    public static String getAcceptToken() {
        return AcceptToken;
    }

    public static void setAcceptToken(String acceptToken) {
        AcceptToken = acceptToken;
    }
}
