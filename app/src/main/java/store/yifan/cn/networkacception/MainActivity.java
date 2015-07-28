package store.yifan.cn.networkacception;


import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.widget.FrameLayout;

import java.util.Date;

import butterknife.ButterKnife;
import butterknife.InjectView;
import store.yifan.cn.appstore.R;
import store.yifan.cn.networkacception.bean.BillBean;
import store.yifan.cn.networkacception.http.protocol.BaseProtocol;
import store.yifan.cn.networkacception.http.protocol.BillListProtocol;
import store.yifan.cn.networkacception.ui.fragment.BaseFragment;
import store.yifan.cn.networkacception.ui.fragment.BillDetailFragment;
import store.yifan.cn.networkacception.ui.fragment.BillListFragment;
import store.yifan.cn.networkacception.ui.fragment.SearchBillFragment;
import store.yifan.cn.networkacception.utils.UIUtils;

public class MainActivity extends BaseActivity implements SearchBillFragment.OnSearchClickListener, BillListFragment.BillDetailClick {


    @InjectView(R.id.main_fl)
    FrameLayout mMainFl;
    private ActionBar mActionBar;
    private FragmentManager fm;
    private BaseFragment baseFragment;
    private FragmentTransaction fx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void initActionbar() {
        mActionBar = getSupportActionBar();
        mActionBar.setTitle(getString(R.string.app_name));
        mActionBar.setDisplayHomeAsUpEnabled(true);
        mActionBar.setHomeButtonEnabled(true);


    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void initData() {

    }

    @Override
    public void init() {
        setContentView(R.layout.activity_main);
        ButterKnife.inject(this);
        fm = getSupportFragmentManager();
        baseFragment = (SearchBillFragment) fm.findFragmentById(R.id.main_fl);
        if (baseFragment == null) {
            baseFragment = new SearchBillFragment();
            fm.beginTransaction().add(R.id.main_fl, baseFragment).commit();
        }


    }

    @Override
    public void onBackPressed() {
        if (fm.getBackStackEntryCount() == 0) {
            finish();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void onSearchClick(String str) {

        if (baseFragment == null || !(baseFragment instanceof BillListFragment)) {
            baseFragment = new BillListFragment();
        }
        if (!baseFragment.isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putString("params", str);
            baseFragment.setArguments(bundle);
            fx = fm.beginTransaction();
            fx.replace(R.id.main_fl, baseFragment, "BillList");
            fx.addToBackStack(null);
            fx.commit();
        }

    }

    @Override
    public void onBillDetailClick(BillBean bean) {

        if (baseFragment == null || !(baseFragment instanceof BillDetailFragment)) {
            baseFragment = new BillDetailFragment();
        }
        if (!baseFragment.isVisible()) {
            Bundle bundle = new Bundle();
            bundle.putParcelable("bean", bean);
            baseFragment.setArguments(bundle);
            fm.beginTransaction().add(R.id.main_fl, baseFragment, "BillListDetail").addToBackStack(null).commit();
        }
    }
}
