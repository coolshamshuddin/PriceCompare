package com.hls.samdhani.pricecompare;

/**
 * Created by Samdhani on 1/9/2016.
 */
public class Product {

    Product(String PRODUCT_PRICE,
            String PRODUCT_BRAND,
            String PRODUCT_CATEGORY,
            String PRODUCT_TITLE,
            String PRODUCT_STOCK,
            String PRODUCT_VENDOR,
            String PRODUCT_URL){

        this.PRODUCT_PRICE = PRODUCT_PRICE;
        this.PRODUCT_BRAND = PRODUCT_BRAND;
        this.PRODUCT_CATEGORY = PRODUCT_CATEGORY;
        this.PRODUCT_TITLE = PRODUCT_TITLE;
        this.PRODUCT_STOCK = PRODUCT_STOCK;
        this.PRODUCT_VENDOR = PRODUCT_VENDOR;
        this.PRODUCT_URL = PRODUCT_URL;
    }

    public String PRODUCT_PRICE = "NA";
    public String PRODUCT_BRAND = "NA";
    public String PRODUCT_CATEGORY = "NA";
    public String PRODUCT_TITLE = "NA";
    public String PRODUCT_STOCK = "NA";
    public String PRODUCT_VENDOR = "NA";
    public String PRODUCT_URL = "NA";

}
