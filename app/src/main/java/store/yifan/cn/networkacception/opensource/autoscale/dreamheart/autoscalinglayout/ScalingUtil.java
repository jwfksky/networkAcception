package store.yifan.cn.networkacception.opensource.autoscale.dreamheart.autoscalinglayout;

import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;

/**
 * 缩放处理类
 */
public class ScalingUtil {

    /**
     * 缩放View和它的子View
     * @param view      根View
     * @param factor    缩放比例
     */
    public static void scaleViewAndChildren(View view, float factor, int level) {
        try{
            // 查看是否有 isAutoScaleEnable 方法
            Method method = view.getClass().getMethod("isAutoScaleEnable");
            // 如果isAutoScaleEnable关闭，则不缩放
            if(!(Boolean)method.invoke(view))
                return;
            // 存在isAutoScaleEnable 方法，说明View属于AutoScalingLayout
            // AutoScalingLayout互相嵌套，子layout不在这里处理缩放
            if (level > 0)
                return;
        }catch (Exception e){
        }

        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();

        // 如果宽高是具体数值，则进行缩放。(MATCH_PARENT、WRAP_CONTENT 等都是负数)
        if(layoutParams.width > 0) {
            layoutParams.width *= factor;
        }
        if(layoutParams.height > 0) {
            layoutParams.height *= factor;
        }

        // 缩放margin
        if(layoutParams instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams marginParams = (ViewGroup.MarginLayoutParams)layoutParams;
            marginParams.leftMargin *= factor;
            marginParams.topMargin *= factor;
            marginParams.rightMargin *= factor;
            marginParams.bottomMargin *= factor;
        }
        view.setLayoutParams(layoutParams);

        // EditText 有特殊的padding，不处理
        if(!(view instanceof EditText)) {
            // 缩放padding
            view.setPadding(
                    (int)(view.getPaddingLeft() * factor),
                    (int)(view.getPaddingTop() * factor),
                    (int)(view.getPaddingRight() * factor),
                    (int)(view.getPaddingBottom() * factor)
            );
        }

        // 缩放文字
        if(view instanceof TextView) {
            scaleTextSize((TextView) view, factor, layoutParams);
        }

        // 如果是ViewGroup，继续缩放它的子View
        if(view instanceof ViewGroup) {
            ViewGroup vg = (ViewGroup)view;
            for(int i = 0; i < vg.getChildCount(); i++) {
                scaleViewAndChildren(vg.getChildAt(i), factor, level + 1);
            }
        }
    }

    // 缩放文字
    public static void scaleTextSize(TextView tv, float factor, ViewGroup.LayoutParams layoutParams) {
        tv.setTextSize(TypedValue.COMPLEX_UNIT_PX, tv.getTextSize() * factor);
    }
}
