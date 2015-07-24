package store.yifan.cn.networkacception;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.InjectView;
import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.http.protocol.AccessTokenProtocol;
import store.yifan.cn.networkacception.http.protocol.BaseProtocol;
import store.yifan.cn.networkacception.http.protocol.PasswordSaltProtocol;
import store.yifan.cn.networkacception.manager.BaseApplication;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.ui.widget.LoadingPage;
import store.yifan.cn.networkacception.utils.UIUtils;
import store.yifan.cn.networkacception.utils.encrypt.Base64;
import store.yifan.cn.networkacception.utils.encrypt.DigestUtils;
import store.yifan.cn.networkacception.utils.encrypt.Hmac;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-24 09:14<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class LoginActivity extends BaseActivity {
    @InjectView(R.id.login_name)
    EditText mLoginName;
    @InjectView(R.id.login_pwd)
    EditText mLoginPwd;
    @InjectView(R.id.login_submit)
    Button mLoginSubmit;
    @InjectView(R.id.login_loading)
    ProgressBar mLogin_loading;
    @InjectView(R.id.rememberPwd)
    CheckBox mRemeberPwd;
    private boolean loading = false;
    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        operateData();
    }

    private void operateData() {
        sp=getSharedPreferences("config",MODE_PRIVATE);
        String name=sp.getString("name","");
        String pwd=sp.getString("pwd","");
        if(!TextUtils.isEmpty(name)&&!TextUtils.isEmpty(name)){
            mLoginName.setText(name);
            mLoginPwd.setText(pwd);
        }
        mLoginSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (checkInput()) {
                    if (!loading) new sortTask().execute();
                } else {
                    UIUtils.showToastSafe(getString(R.string.check_params_error));
                }
            }
        });

    }

    private boolean checkInput() {
        if (TextUtils.isEmpty(mLoginName.getText())) return false;
        if (TextUtils.isEmpty(mLoginPwd.getText())) return false;

        return true;
    }

    class sortTask extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            loading = true;
            mLogin_loading.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                if (TextUtils.isEmpty(BaseApplication.getAcceptToken())) {
                    Constants.USER_NAME = mLoginName.getText().toString().trim();
                    BaseProtocol<HashMap<String, String>> passwordSalt = new PasswordSaltProtocol();
                    HashMap<String, String> map = passwordSalt.load(UIUtils.getString(R.string.ValidateUserCode_URI), BaseProtocol.POST);
                    if (map == null) {
                        UIUtils.showToastSafe(UIUtils.getString(R.string.network_error));
                    }
                    String newPwd = mLoginPwd.getText().toString().trim() + map.get("salt");

                    byte[] hashPwd = DigestUtils.encodeSHA512(newPwd
                            .getBytes("utf-8"));
                    String encryptedPWD = Base64.encodeBytes(hashPwd);

                    byte[] keyHash = Hmac.encodeHmacSHA512(
                            map.get("key").getBytes("utf-8"),
                            encryptedPWD.getBytes("utf-8"));
                    String encryptedKey = Base64.encodeBytes(keyHash);
                    return encryptedKey;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String s) {
            new AsscessTask().execute(s);
        }
    }

    class AsscessTask extends AsyncTask<String, HashMap<String, String>, HashMap<String, String>> {


        @Override
        protected HashMap doInBackground(String... params) {
            HashMap<String, String> obj = null;
            if (params != null) {
                obj = (HashMap<String, String>) new AccessTokenProtocol(params[0]).load(UIUtils.getString(R.string.AccessToken_URI), BaseProtocol.POST);
            }
            return obj;
        }

        @Override
        protected void onPostExecute(HashMap<String, String> map) {
            mLogin_loading.setVisibility(View.GONE);
            loading = false;
            if (map == null) {
                UIUtils.showToastSafe(getString(R.string.network_error));
            } else if ("0".equals(map.get("Result"))) {
                if(sp!=null){
                    SharedPreferences.Editor editor=sp.edit();
                    if(mRemeberPwd.isChecked()){
                        editor.putString("name",mLoginName.getText().toString().trim());
                        editor.putString("pwd",mLoginPwd.getText().toString().trim());
                    }else{
                        editor.putString("name","");
                        editor.putString("pwd","");
                    }
                    editor.commit();
                }
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
                UIUtils.showToastSafe(map.get("Msg"));
            }
        }
    }
}

