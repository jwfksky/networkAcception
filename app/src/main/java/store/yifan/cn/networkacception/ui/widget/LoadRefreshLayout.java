package store.yifan.cn.networkacception.ui.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AbsListView;
import android.widget.ListView;

import store.yifan.cn.appstore.R;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-07-15 16:11<br\>
 * Version: 1.0<br\>
 * Desc:<br\>
 * Revise:<br\>
 */
public class LoadRefreshLayout extends SwipeRefreshLayout implements AbsListView.OnScrollListener {
    private boolean loading;
    private int mTouchSlop;
    private ListView mListView;
    private int initY;
    private int finalY;
    private View mListViewFooter;
    private OnLoadListener onLoadListener;

    public LoadRefreshLayout(Context context) {
        this(context, null);
    }

    public LoadRefreshLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        mListViewFooter = LayoutInflater.from(context).inflate(R.layout.listview_footer, null);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        if (mListView == null) {
            initListView();
        }
    }

    private void initListView() {
        int childCount = getChildCount();
        if (childCount > 0) {
            View childView = getChildAt(0);
            if (childView instanceof ListView) {
                mListView = (ListView) childView;
                mListView.setOnScrollListener(this);
            }
        }
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                initY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_MOVE:
                finalY = (int) ev.getRawY();
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (canLoad()) {
                    loadData();
                }
                break;

        }
        return super.onInterceptTouchEvent(ev);
    }

    private void loadData() {
        if(onLoadListener!=null){
            setLoading(true);
            onLoadListener.load();
        }
    }

    private boolean canLoad() {
        return isBottom() && !loading && isPullUp();
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        setEnabled(scrollState == SCROLL_STATE_IDLE);
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if(canLoad()){
            loadData();
        }
    }

    //check current item or not equal with ListView's last item
    public boolean isBottom() {
        if (mListView != null && mListView.getAdapter() != null) {
            return mListView.getLastVisiblePosition() == (mListView.getAdapter().getCount() - 1);
        }
        return false;
    }

    // check the touch gesture distance  equal or above the miniest touch slop
    public boolean isPullUp() {
        return (initY - finalY) >= mTouchSlop;
    }

    public void setOnLoadListener(OnLoadListener onLoadListener) {
        this.onLoadListener = onLoadListener;
    }

    public void setLoading(boolean load) {
        loading=load;
        if(loading){
            mListView.addFooterView(mListViewFooter);
        }else{
            mListView.removeFooterView(mListViewFooter);
            initY=0;
            finalY=0;
        }
    }

    public boolean isLoading() {
        return loading;
    }

    public interface OnLoadListener{
        void load();
    }
}
