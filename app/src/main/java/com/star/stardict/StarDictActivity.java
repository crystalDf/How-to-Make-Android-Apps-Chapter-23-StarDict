package com.star.stardict;

import android.support.v4.app.Fragment;

public class StarDictActivity extends SingleFragmentActivity {

    @Override
    protected Fragment createFragment() {
        return StarDictFragment.newInstance();
    }

}
