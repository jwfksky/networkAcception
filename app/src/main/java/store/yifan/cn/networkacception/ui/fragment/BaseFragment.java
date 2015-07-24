package store.yifan.cn.networkacception.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collection;
import java.util.List;

import butterknife.ButterKnife;
import store.yifan.cn.networkacception.ui.widget.LoadingPage;
import store.yifan.cn.networkacception.utils.UIUtils;
import store.yifan.cn.networkacception.utils.ViewUtils;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-04-15 14:26<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public abstract class BaseFragment extends Fragment {

    protected LoadingPage mContentView;
    protected int currentPage=1;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        currentPage=1;
        //  if(mContentView==null){//如果为空，就新建一个
        mContentView = new LoadingPage(UIUtils.getContext()) {

            @Override
            public View createLoadedView() {
                return BaseFragment.this.createSuccessView();
            }

            @Override
            public LoadResult load() {
                return BaseFragment.this.load();
            }
        };
      /*  }else{//不为null时，需要把自身从父布局中移除，因为ViewPager会再次添加
            ViewUtils.removeSelfFromParent(mContentView);
        }*/
        return mContentView;
    }

    /**
     * 当显示的时候，加载该页面
     */
    public void show() {

        if (mContentView != null)
            mContentView.show();

    }
    public void refreshOrLoad(){
        if (mContentView != null)
            mContentView.executeTask();
    }
    protected LoadingPage.LoadResult checkResult(Object obj) {

        if (obj == null) {
            return LoadingPage.LoadResult.ERROR;
        }
        if (obj instanceof List) {
            List list = (List) obj;
            if (list.size() <= 0) {
                return LoadingPage.LoadResult.EMPTY;
            }
        }
        return LoadingPage.LoadResult.SUCCEED;
    }

    protected abstract LoadingPage.LoadResult load();

    protected abstract View createSuccessView();

    @Override
    public void onResume() {
        super.onResume();
        show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
    }

}
