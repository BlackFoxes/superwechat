package cn.ucai.fulicenter.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import cn.ucai.fulicenter.I;
import cn.ucai.fulicenter.R;
import cn.ucai.fulicenter.bean.GoodDetailsBean;
import cn.ucai.fulicenter.bean.NewGoodBean;

/**
 * Created by sks on 2016/6/22.
 */
public class GoodAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    Context mContext;
    GoodDetailsBean good;
    ArrayList<NewGoodBean> mGoodList;
    private String footerText;
    private boolean isMore;
    FooterViewHolder footerHolder;
    GoodItemViewHolder goodHolder;

    public GoodAdapter(Context mContext, ArrayList<NewGoodBean> mGoodList) {
        this.mContext = mContext;
        this.mGoodList = mGoodList;
    }

    public String getFooterText() {
        return footerText;
    }

    public void setFooterText(String footerText) {
        this.footerText = footerText;
    }

    public boolean isMore() {
        return isMore;
    }

    public void setMore(boolean more) {
        isMore = more;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater=LayoutInflater.from(mContext);
        ViewHolder holder = null;
        switch (viewType) {
            case I.TYPE_ITEM:
                holder = new GoodItemViewHolder(inflater.inflate(R.layout.item_new_good, parent, false));
                break;
            case I.TYPE_FOOTER:
                holder = new FooterViewHolder(inflater.inflate(R.layout.item_footer, parent, false));
                break;

        }
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        if (holder instanceof FooterViewHolder) {
            footerHolder = (FooterViewHolder) holder;
            footerHolder.tvFooter.setText(footerText);
            footerHolder.tvFooter.setVisibility(View.VISIBLE);

        }
        if (holder instanceof GoodItemViewHolder) {
            goodHolder = (GoodItemViewHolder) holder;
            goodHolder.tvGoodName.setText(good.getGoodsName());
            goodHolder.tvGoodPrice.setText(good.getCurrencyPrice());

        }


    }

    @Override
    public int getItemCount() {
        return mGoodList==null?1:mGoodList.size()+1;
    }

    @Override
    public int getItemViewType(int position) {
        if (position == getItemCount() - 1) {
            return I.TYPE_FOOTER;


        } else {
            return I.TYPE_ITEM;

        }

    }

    class GoodItemViewHolder extends ViewHolder {
        LinearLayout LayoutGood;
        NetworkImageView nivThumb;
        TextView tvGoodName;
        TextView tvGoodPrice;

        public GoodItemViewHolder(View itemView) {
            super(itemView);
            LayoutGood = (LinearLayout) itemView.findViewById(R.id.layout_good);
            nivThumb = (NetworkImageView) itemView.findViewById(R.id.niv_good_thumb);
            tvGoodName = (TextView) itemView.findViewById(R.id.tv_good_name);
            tvGoodPrice = (TextView) itemView.findViewById(R.id.tv_good_price);
        }
    }
}
