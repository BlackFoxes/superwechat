package cn.ucai.fulicenter.activity;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.TextView;

import cn.ucai.fulicenter.R;

public class FuliCenterMainActivity extends Activity {
    TextView mTvCartHint;
    RadioButton mRadioNewGoods;
    RadioButton mRadioBoutique;
    RadioButton mRadioCategory;
    RadioButton mRadioCart;
    RadioButton mRadioPersionalCenter;
    RadioButton[] mRadios = new RadioButton[5];
    private int index;
    private int currenTabIndex;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fuli_center_main);
        initView();
    }

    private void initView() {
        mTvCartHint = (TextView) findViewById(R.id.tvCartHint);
        mRadioBoutique = (RadioButton) findViewById(R.id.layout_boutique);
        mRadioCart = (RadioButton) findViewById(R.id.layout_cart);
        mRadioCategory = (RadioButton) findViewById(R.id.layout_category);
        mRadioPersionalCenter = (RadioButton) findViewById(R.id.layout_personal_center);
        mRadioNewGoods = (RadioButton) findViewById(R.id.layout_new_good);
        mRadios[0] = mRadioNewGoods;
        mRadios[1] = mRadioBoutique;
        mRadios[2] = mRadioCart;
        mRadios[3] = mRadioCategory;
        mRadios[4] = mRadioPersionalCenter;


    }

    private void onCheckedChange(View view) {
        switch (view.getId()) {
            case R.id.layout_new_good:
                index = 0;
                break;
            case R.id.layout_boutique:
                index = 1;
                break;
            case R.id.layout_category:

                index = 2;
                break;
            case R.id.layout_cart:

                index = 3;
                break;
            case R.id.layout_personal_center:

                index = 4;
                break;
        }
        if (currenTabIndex!=index) {
            setRadioChecked(index);
            currenTabIndex = index;


        }
    }

    private void setRadioChecked(int index) {
        for (int i=0;i<mRadios.length;i++) {
            if (index == i) {
                mRadios[i].setSelected(true);

            } else {
                mRadios[i].setSelected(false);
            }

        }

    }
}
