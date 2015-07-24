package store.yifan.cn.networkacception.ui.fragment;

import java.util.HashMap;

/**
 * Author: Jwf(feijia101@gmail.com) <br\>
 * Date: 2015-04-15 14:23<br\>
 * Version: 1.0<br\>
 * Desc:fragment的工厂类<br\>
 * Revise:<br\>
 */
public class FragmentFactory {

    public static final int TITLE_HOME = 0;
    public static final int TITLE_APP = 1;
    public static final int TITLE_GAME = 2;
    public static final int TITLE_SUBJECT = 3;
    public static final int TITLE_RECOMMEND = 4;
    public static final int TITLE_CATEGORY = 5;
    public static final int TITLE_HOT = 6;
    private static HashMap<Integer, BaseFragment> map = new HashMap<Integer, BaseFragment>();

    public static BaseFragment createFragment(int position) {
        BaseFragment fragment = map.get(position);
        if (fragment == null) {
            switch (position) {


            }
            map.put(position,fragment);
        }

        return fragment;

    }
}
