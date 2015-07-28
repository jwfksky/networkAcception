package store.yifan.cn.networkacception.ui.fragment;

import android.content.Context;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.MainActivity;
import store.yifan.cn.networkacception.bean.BillBean;
import store.yifan.cn.networkacception.http.protocol.BaseProtocol;
import store.yifan.cn.networkacception.http.protocol.BillListProtocol;
import store.yifan.cn.networkacception.manager.Constants;
import store.yifan.cn.networkacception.ui.widget.LoadRefreshLayout;
import store.yifan.cn.networkacception.ui.widget.LoadingPage;
import store.yifan.cn.networkacception.utils.CommonUtils;
import store.yifan.cn.networkacception.utils.UIUtils;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-08 14:03<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class BillListFragment extends BaseFragment {
    View view;
    @InjectView(R.id.bill_lv)
    ListView mBillLv;
    @InjectView(R.id.swipe)
    LoadRefreshLayout swipe;
    private List<BillBean> billList = new ArrayList<>();
    private List<BillBean> currentList;
    private MyTestAdapter adapter;

    private HashMap<String, String> hashMap;
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (swipe != null && adapter != null) {
                swipe.setRefreshing(false);
                swipe.setLoading(false);
                adapter.notifyDataSetChanged();
            }
        }
    };

    public interface BillDetailClick {
        void onBillDetailClick(BillBean bean);
    }

    @Override
    protected LoadingPage.LoadResult load() {

        Bundle bundle = getArguments();
        String params = (String) bundle.get("params");
        hashMap = (HashMap<String, String>) CommonUtils.parserToMap(params);
        hashMap.put("currentPage", String.valueOf(currentPage));
        BaseProtocol<List<BillBean>> baseProtocol = new BillListProtocol(hashMap);
        currentList = baseProtocol.load(UIUtils.getString(R.string.getMobileDataList), BaseProtocol.POST);
        if (currentPage == 1) {
            //页面第一次加载后，createSuccessView成功后不再执行。若billList=null，则改变billList的内存映射。刷新和加载失效
            billList.clear();
        }
        if (currentList != null)
            billList.addAll(currentList);
        handler.sendEmptyMessage(0);
        return currentList != null ? checkResult(billList) : checkResult(null);
    }

    @Override
    protected View createSuccessView() {
        view = UIUtils.inflate(R.layout.fragment_bill_list);
        ButterKnife.inject(this, view);
        operateView();
        return view;
    }

    private void operateView() {
        adapter = new MyTestAdapter(getActivity(), billList);
        mBillLv.setAdapter(adapter);
        swipe.setColorSchemeResources(R.color.default_green_light, R.color.default_green_dark);
        swipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!swipe.isLoading()) {
                    currentPage = 1;
                    refreshOrLoad();
                } else {
                    swipe.setRefreshing(false);
                }

            }
        });
        swipe.setOnLoadListener(new LoadRefreshLayout.OnLoadListener() {
            @Override
            public void load() {
                if (currentPage == Constants.totalPage) {
                    UIUtils.showToastSafe("no more data!");
                }
                if (!swipe.isRefreshing()&& Constants.totalPage > 1 && currentPage < Constants.totalPage) {
                    currentPage++;
                    refreshOrLoad();
                } else {
                    swipe.setLoading(false);
                }
            }
        });

        mBillLv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (billList != null && billList.size() >= position) {
                    BillBean bean = billList.get(position);
                    ((MainActivity) getActivity()).onBillDetailClick(bean);
                }

            }
        });
    }

    class MyTestAdapter extends BaseAdapter {

        private Context context;
        private List<BillBean> data;

        public MyTestAdapter(Context context, List data) {
            this.context = context;
            this.data = data;
        }

        @Override
        public int getCount() {
            int count = (data == null) ? 0 : data.size();
            System.out.println(count + "count");
            return count;
        }

        @Override
        public Object getItem(int position) {
            return billList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder viewHolder;
            if (convertView == null) {
                // View view = UIUtils.inflate(R.layout.list_item_bill);
                convertView = LayoutInflater.from(context).inflate(R.layout.list_item_bill, null);
                viewHolder = new ViewHolder(convertView);
                convertView.setTag(viewHolder);
            }
            convertView.setBackgroundResource(position%2==0? R.color.default_green_dark1:R.color.default_green_dark2);
            if(position%2==0){}
            viewHolder = (ViewHolder) convertView.getTag();
            if (billList != null) {
                BillBean bill = billList.get(position);
                viewHolder.mMoneyTv.setText(String.valueOf(bill.getTOTAL_AMOUNT()));
                viewHolder.mOrderNumTv.setText(bill.getBILL_NUM());
                viewHolder.mPlanBeginTimeTv.setText("");
                viewHolder.mPlanEndTimeTv.setText("");
                viewHolder.mPlanIdTv.setText(bill.getPLAN_NUM()+"");
                if(bill.getPAYED_AT()==null){
                    viewHolder.mPayStatusTv.setText("No");
                }else{
                    viewHolder.mPayStatusTv.setText("Yes");
                }
                viewHolder.mOpModeTv.setText(bill.getOP_MODE_NAME());
                viewHolder.mSubmitTimeTv.setText(bill.getPAYED_AT());
            }


            return convertView;
        }


    }

    /**
     * This class contains all butterknife-injected Views & Layouts from layout file 'list_item_bill.xml'
     * for easy to all layout elements.
     *
     * @author ButterKnifeZelezny, plugin for Android Studio by Avast Developers (http://github.com/avast)
     */
    static class ViewHolder {
        @InjectView(R.id.orderNum_tv)
        TextView mOrderNumTv;
        @InjectView(R.id.planId_tv)
        TextView mPlanIdTv;
        @InjectView(R.id.submitTime_tv)
        TextView mSubmitTimeTv;
        @InjectView(R.id.payStatus_tv)
        TextView mPayStatusTv;
        @InjectView(R.id.money_tv)
        TextView mMoneyTv;
        @InjectView(R.id.planBeginTime_tv)
        TextView mPlanBeginTimeTv;
        @InjectView(R.id.planEndTime_tv)
        TextView mPlanEndTimeTv;
        @InjectView(R.id.opMode_tv)
        TextView mOpModeTv;
        ViewHolder(View view) {
            ButterKnife.inject(this, view);
        }
    }

}
