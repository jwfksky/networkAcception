package store.yifan.cn.networkacception.ui.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import butterknife.ButterKnife;
import butterknife.InjectView;
import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.bean.BillBean;
import store.yifan.cn.networkacception.ui.widget.LoadingPage;
import store.yifan.cn.networkacception.utils.UIUtils;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-17 13:15<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class BillDetailFragment extends BaseFragment {
    @InjectView(R.id.orderNum_tv_detail)
    TextView mOrderNumTvDetail;
    @InjectView(R.id.planId_tv_detail)
    TextView mPlanIdTvDetail;
    @InjectView(R.id.money_tv_detail)
    TextView mMoneyTvDetail;
    @InjectView(R.id.submit_time_detail)
    TextView mSubmitTimeDetail;
    @InjectView(R.id.submit_btn_detail)
    Button mSubmitBtnDetail;

    @Override
    protected LoadingPage.LoadResult load() {
        return LoadingPage.LoadResult.SUCCEED;
    }

    @Override
    protected View createSuccessView() {
        View view = UIUtils.inflate(R.layout.fragment_bill_detail);
        ButterKnife.inject(this, view);
        initView();
        return view;
    }

    private void initView() {
        Bundle bundle = getArguments();
        BillBean bean = bundle.getParcelable("bean");
        if (bean != null) {
            double amout = bean.getTOTAL_AMOUNT();
            if (amout == 0D) {
                mSubmitBtnDetail.setVisibility(View.GONE);
            } else {
                mSubmitBtnDetail.setVisibility(View.VISIBLE);
            }
            mOrderNumTvDetail.setText(bean.getBILL_NUM());
            mPlanIdTvDetail.setText(bean.getPLAN_NUM()+"");
            mMoneyTvDetail.setText(String.valueOf(amout));
            mSubmitTimeDetail.setText(bean.getPAYED_AT());
        }
    }


}
