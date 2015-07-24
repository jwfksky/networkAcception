package store.yifan.cn.networkacception.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.ButterKnife;
import butterknife.InjectView;
import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.MainActivity;
import store.yifan.cn.networkacception.http.protocol.AccessTokenProtocol;
import store.yifan.cn.networkacception.http.protocol.BaseProtocol;
import store.yifan.cn.networkacception.http.protocol.BillListProtocol;
import store.yifan.cn.networkacception.http.protocol.PasswordSaltProtocol;
import store.yifan.cn.networkacception.manager.BaseApplication;
import store.yifan.cn.networkacception.ui.widget.LoadingPage;
import store.yifan.cn.networkacception.utils.UIUtils;
import store.yifan.cn.networkacception.utils.encrypt.Base64;
import store.yifan.cn.networkacception.utils.encrypt.DigestUtils;
import store.yifan.cn.networkacception.utils.encrypt.Hmac;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-08 11:27<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class SearchBillFragment extends BaseFragment implements View.OnClickListener {
    @InjectView(R.id.search_endTime)
    TextView mSearchEndTime;
    @InjectView(R.id.search_startTime)
    TextView mSearchStartTime;
    @InjectView(R.id.submit_uncheck)
    Button mSubmitUncheck;
    @InjectView(R.id.submit_checked)
    Button mSubmitChecked;
    @InjectView(R.id.search_planTime)
    Spinner mPlanTime;
    @InjectView(R.id.search_queryKey)
    EditText mQueryKey;
    private String tag = "";
    private View view;
    private String[] defaultSort;
    public interface OnSearchClickListener {
        void onSearchClick(String tag);
    }


    private void operateView() {
        mSearchEndTime.setOnClickListener(this);
        mSearchStartTime.setOnClickListener(this);
        mSubmitUncheck.setOnClickListener(this);
        mSubmitChecked.setOnClickListener(this);
        ArrayAdapter arrayAdapter=new ArrayAdapter(getActivity(),R.layout.spinner_search_item,defaultSort);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPlanTime.setAdapter(arrayAdapter);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.search_endTime:
                initDatePicker(mSearchEndTime);
                break;
            case R.id.search_startTime:
                initDatePicker(mSearchStartTime);
                break;
            case R.id.submit_uncheck:
                tag = "0";
                submit();//uncheck tag 0
                break;
            case R.id.submit_checked://checked tag 1
                tag = "1";
                submit();
                break;
        }
    }

    public String getSubmitParams() {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("mSearchStartTime", mSearchStartTime.getText().toString());
            jsonObject.put("mSearchEndTime", mSearchEndTime.getText().toString());
            jsonObject.put("queryKey", mQueryKey.getText());
            jsonObject.put("sort", defaultSort[0].equals(mPlanTime.getSelectedItem().toString())?"ASC":"DESC");
            jsonObject.put("tag", tag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return jsonObject.toString();
    }

    private void submit() {
        if (checkParams()) {
            ((MainActivity) getActivity()).onSearchClick(getSubmitParams());
        } else {
             UIUtils.showToastSafe(UIUtils.getString(R.string.check_params_error));
        }
    }

    private boolean checkParams() {
        if (TextUtils.isEmpty(mSearchStartTime.getText())) return false;
        if (TextUtils.isEmpty(mSearchEndTime.getText())) return false;
        /*if (TextUtils.isEmpty(mPlanTime.getSelectedItem().toString())) return false;
        if (TextUtils.isEmpty(mQueryKey.getText())) return false;*/
        UIUtils.showToastSafe(mPlanTime.getSelectedItem().toString());
        return true;

    }

    protected void initDatePicker(final TextView tv) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog picker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                String month="";
                int intM=monthOfYear + 1;
                if(intM<10){
                    month="0"+String.valueOf(intM);
                }else{
                    month=String.valueOf(intM);
                }
                tv.setText(year + "-" + month + "-" + dayOfMonth);
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));
        picker.show();
    }


    @Override
    protected LoadingPage.LoadResult load() {

        return LoadingPage.LoadResult.SUCCEED;
    }

    @Override
    protected View createSuccessView() {
        view = UIUtils.inflate(R.layout.fragment_search);
        ButterKnife.inject(this, view);
        defaultSort=getResources().getStringArray(R.array.search_sort);
        operateView();
        return view;
    }
}
