package com.speedata.wifidome.adapter;

import android.support.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.speedata.wifidome.R;
import com.speedata.wifidome.bean.WifiBean;

import java.util.List;

/**
 * Created by 张明_ on 2018/7/10.
 * Email 741183142@qq.com
 */

public class MainActAdapter extends BaseQuickAdapter<WifiBean, BaseViewHolder> {
    public MainActAdapter(int layoutResId, @Nullable List<WifiBean> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, WifiBean item) {
        helper.setText(R.id.tv_yuanMAC, item.getYuanMAC());
        helper.setText(R.id.tv_muMAC, item.getMuMAC());
        helper.setText(R.id.tv_zhenZhu, item.getZhenZhu());
        helper.setText(R.id.tv_zhenZi, item.getZhenZi());
        helper.setText(R.id.tv_xinDao, item.getXinDao());
        helper.setText(R.id.tv_xinHao, item.getXinHao());
    }
}
