package store.yifan.cn.networkacception.utils;

import android.text.Layout;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class ViewUtils {
	/** 把自身从父View中移除 */
	public static void removeSelfFromParent(View view) {
		if (view != null) {
			ViewParent parent = view.getParent();
			if (parent != null && parent instanceof ViewGroup) {
				ViewGroup group = (ViewGroup) parent;
				group.removeView(view);
			}
		}
	}

	/** 请求View树重新布局，用于解决中层View有布局状态而导致上层View状态断裂 */
	public static void requestLayoutParent(View view, boolean isAll) {
		ViewParent parent = view.getParent();
		while (parent != null && parent instanceof View) {
			if (!parent.isLayoutRequested()) {
				parent.requestLayout();
				if (!isAll) {
					break;
				}
			}
			parent = parent.getParent();
		}
	}

	/** 判断触点是否落在该View上 */
	public static boolean isTouchInView(MotionEvent ev, View v) {
		int[] vLoc = new int[2];
		v.getLocationOnScreen(vLoc);
		float motionX = ev.getRawX();
		float motionY = ev.getRawY();
		return motionX >= vLoc[0] && motionX <= (vLoc[0] + v.getWidth()) && motionY >= vLoc[1] && motionY <= (vLoc[1] + v.getHeight());
	}

	/** FindViewById的泛型封装，减少强转代码 */
	public static <T extends View> T findViewById(View layout, int id) {
		return (T) layout.findViewById(id);
	}
	/**
     * 按行获取TextView的数据
     *
     * @return
     */
    public static List<String> getValueByLine(final TextView tv) {
        final ArrayList<String> list=new ArrayList<String>();
        ViewTreeObserver vo=tv.getViewTreeObserver();
        vo.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Layout layout = tv.getLayout();
                int lines = layout.getLineCount();
                String text=layout.getText().toString();
                for(int i=0;i<lines;i++){
                    int start=layout.getLineStart(i);
                    int end=layout.getLineEnd(i);
                    String temp=text.substring(start,end);
                    list.add(temp);
                }
            }
        });

        return list;
    }
}
