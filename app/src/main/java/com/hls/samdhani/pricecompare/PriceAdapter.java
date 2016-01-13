package com.hls.samdhani.pricecompare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Samdhani on 1/11/2016.
 */
public class PriceAdapter extends ArrayAdapter<Product> {

    public PriceAdapter(Context context, int textViewResourceId) {
        super(context, textViewResourceId);
    }

    public PriceAdapter(Context context, int resource, List<Product> items) {
        super(context, resource, items);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View v = convertView;

        if (v == null) {
            LayoutInflater vi;
            vi = LayoutInflater.from(getContext());
            v = vi.inflate(R.layout.list_item_price, null);
        }

        Product p = getItem(position);

        if (p != null) {
            TextView title = (TextView) v.findViewById(R.id.list_item_title_textview);
            TextView vendor = (TextView) v.findViewById(R.id.list_item_vendor_textview);
            TextView stock = (TextView) v.findViewById(R.id.list_item_stock_textview);
            TextView price = (TextView) v.findViewById(R.id.list_item_price_textview);
            ImageView logo = (ImageView)v.findViewById(R.id.list_item_icon);



            if(p.PRODUCT_VENDOR.contains("Flipkart"))
                logo.setImageResource(R.drawable.ico_flipkart);
            else if(p.PRODUCT_VENDOR.contains("Snap"))
                logo.setImageResource(R.drawable.ico_snapdeal);
            else if(p.PRODUCT_VENDOR.contains("Amazon"))
                logo.setImageResource(R.drawable.ico_amazon);
            else if(p.PRODUCT_VENDOR.contains("Indiatimes"))
                logo.setImageResource(R.drawable.ico_indiatimes);
            else if(p.PRODUCT_VENDOR.contains("ManiacStore"))
                logo.setImageResource(R.drawable.ico_maniac);

            title.setText(p.PRODUCT_TITLE);
            vendor.setText(p.PRODUCT_VENDOR);
            stock.setText(p.PRODUCT_STOCK);
            price.setText("Rs. "+p.PRODUCT_PRICE);
        }

        return v;
    }

}
