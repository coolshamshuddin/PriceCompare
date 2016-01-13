package com.hls.samdhani.pricecompare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * A placeholder fragment containing a simple view.
 */

public class ListActivityFragment extends Fragment {

    private PriceAdapter priceAdapter;
    ProgressBar spinner;
    ListView listView;
    public static String productName;

    public ListActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }



    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_list,menu);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_list, container, false);
        setHasOptionsMenu(true);
        spinner = (ProgressBar)rootView.findViewById(R.id.progressBar1);
        Intent intent = getActivity().getIntent();
        if(intent!= null && intent.hasExtra(Intent.EXTRA_TEXT)){
            productName = intent.getStringExtra(Intent.EXTRA_TEXT);
            productName.replace(" ","+");
            FetchPricesTask fetchPricesTask = new FetchPricesTask();
            fetchPricesTask.execute(productName);
        }
        listView = (ListView)rootView.findViewById(R.id.listview_price);

        spinner.setVisibility(View.VISIBLE);
        listView.setVisibility(View.INVISIBLE);

        List<Product> productList = new ArrayList<Product>();

        priceAdapter = new PriceAdapter(
                getActivity(),
                R.layout.list_item_price,
                productList);

        listView.setAdapter(priceAdapter);


        return rootView;
    }

    class FetchPricesTask extends AsyncTask<String, Void, List<Product>> {

        private List<Product> productList;

        private final String LOG_TAG = FetchPricesTask.class.getSimpleName();
        @Override
        protected List<Product> doInBackground(String... params) {
            // These two need to be declared outside the try/catch
            // so that they can be closed in the finally block.
            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            // Will contain the raw JSON response as a string.
            String pricesJsonStr = null;
            final String key = getString(R.string.api_key);
            String product = params[0];

            try {
                final String PRICES_BASE_URL =
                        "http://api.dataweave.in/v1/price_intelligence/findProduct/?";
                final String QUERY_KEY = "api_key";
                final String QUERY_PRODUCT = "product";

                Uri builtUri = Uri.parse(PRICES_BASE_URL).buildUpon().
                        appendQueryParameter(QUERY_KEY,key).appendQueryParameter(QUERY_PRODUCT, product).build();
                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                // Read the input stream into a String
                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    // Nothing to do.
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
                    return null;
                }
                pricesJsonStr = buffer.toString();
                Log.e(LOG_TAG, pricesJsonStr);
                try {
                    return getPriceDataFromJson(pricesJsonStr);
                    //returns productList
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            } catch (IOException e) {
                Log.e(LOG_TAG, "Error ", e);
                return null;
            } finally{
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream", e);
                    }
                }
            }
            return null;
        }

        public List<Product> getPriceDataFromJson(String pricesJsonStr)
                throws JSONException{
            final String OWN_STATUS = "status_text";
            final String OWN_COUNT = "count";
            final String OWN_DATA = "data";
            final String OWN_BRAND = "brand";
            final String OWN_PRICE = "available_price";
            final String OWN_CATEGORY = "category";
            final String OWN_TITLE = "title";
            final String OWN_STOCK = "stock";
            final String OWN_VENDOR = "source";
            final String OWN_URL = "url";

            JSONObject pricesJson = new JSONObject(pricesJsonStr);
            String status = pricesJson.getString(OWN_STATUS);
            int count = pricesJson.getInt(OWN_COUNT);

            if (status.contains("Success")&&count>0) {
                JSONArray pricesArray = pricesJson.getJSONArray(OWN_DATA);
                productList = new ArrayList<Product>();
                productList.clear();
                for(int i=0; i<pricesArray.length();i++){
                    JSONObject jsonProduct = pricesArray.getJSONObject(i);
                    if(jsonProduct.getString(OWN_VENDOR).contains("Flipkart")||
                            jsonProduct.getString(OWN_VENDOR).contains("Amazon")||
                            jsonProduct.getString(OWN_VENDOR).contains("IndiatimesShopping")||
                            jsonProduct.getString(OWN_VENDOR).contains("SnapDeal")||
                            jsonProduct.getString(OWN_VENDOR).contains("ManiacStore")){

                        String Product_Price = jsonProduct.has(OWN_PRICE)?jsonProduct.getString(OWN_PRICE).toString():"NA";
                        String Product_Brand = jsonProduct.has(OWN_BRAND)?jsonProduct.getString(OWN_BRAND).toString():"NA";
                        String Product_Category = jsonProduct.has(OWN_CATEGORY)?jsonProduct.getString(OWN_CATEGORY).toString():"NA";
                        String Product_Title = jsonProduct.has(OWN_TITLE)?jsonProduct.getString(OWN_TITLE).toString():"NA";
                        String Product_Stock = jsonProduct.has(OWN_STOCK)?jsonProduct.getString(OWN_STOCK).toString():"NA";
                        String Product_Vendor = jsonProduct.has(OWN_VENDOR)?jsonProduct.getString(OWN_VENDOR).toString():"NA";
                        String Product_URL = jsonProduct.has(OWN_URL)?jsonProduct.getString(OWN_URL).toString():"NA";

                        Product product = new Product(Product_Price, Product_Brand,
                                Product_Category,Product_Title,
                                Product_Stock,Product_Vendor,
                                Product_URL);
                        productList.add(product);
                    }
                }
            }
            return productList;
            //Flipkart, Amazon, IndiatimesShopping, SnapDeal, ManiacStore

        }

        @Override
        protected void onPostExecute(final List<Product> list) {
            super.onPostExecute(list);


            if(list!=null){
                priceAdapter.clear();
                //add filters
                final List<Product> finalList = new ArrayList<>(filter(list));

                priceAdapter.addAll(finalList);
                spinner.setVisibility(View.INVISIBLE);
                listView.setVisibility(View.VISIBLE);
                listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Intent loadURL = new Intent(getActivity(),DetailActivity.class);
                        String link = finalList.get(position).PRODUCT_URL.replace("\\","");
                        Log.e("Link", link);
                        loadURL.putExtra(Intent.EXTRA_TEXT, link);
                        startActivity(loadURL);
                    }
                });

                listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        Toast.makeText(getActivity(),list.get(position).PRODUCT_TITLE,Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }
            else {
                priceAdapter.clear();
                ImageView imageView = (ImageView)getView().findViewById(R.id.imageView);
                imageView.setImageResource(R.drawable.ico_oops);
                spinner.setVisibility(View.INVISIBLE);

            }
        }

        private List<Product> filter(List<Product> list){

            List<Product> list1 = new ArrayList<Product>();
            List<Product> filteredList = new ArrayList<Product>();

            list1.clear();
            filteredList.clear();

            SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(getActivity());
            //Stock Filter

            String[] def = getResources().getStringArray(R.array.pref_website_defaultValues);
            Set<String> defaultValues = new HashSet<String>(Arrays.asList(def));

            Set<String> checkedValues = preferences.getStringSet(getString(R.string.pref_website_key),defaultValues);
            Set<String> checkedCaps = new HashSet<String>();
            checkedCaps.clear();

            for(String s: checkedValues)
               checkedCaps.add(s.toUpperCase());

            //Filters
            //Website Filter
            for(Product p : list){

                for(String s: checkedCaps){

                    if(p.PRODUCT_VENDOR.toUpperCase().contains(s)){
                        list1.add(p);
                        break;
                    }
                }

            }

            //Stock Filter

            if(!preferences.getBoolean(getString(R.string.pref_stock_key),true)){
                for(int i=0; i<list1.size(); i++){
                    Product product = list1.get(i);
                    if(product.PRODUCT_STOCK.contains("In"))
                        filteredList.add(product);
                    else {

                    }
                }
            }else {
                filteredList = list1;
            }

            //Sort
            String sortType = preferences.getString(getString(R.string.pref_sort_key),getString(R.string.pref_sort_rating));
            List<Product> sortedList = new ArrayList<Product>();
            sortedList.clear();
            if(sortType.matches("highest_rated")){
                sortedList = filteredList;
            }else if(sortType.matches("lower_price")){
                sortedList = rsort(filteredList);
            }else if(sortType.matches("higher_price")){
                sortedList = sort(filteredList);
            }
            return sortedList;
        }


        private List<Product> sort(List<Product> a) {
            int N = a.size();
            for (int i = 1; i < N; i++)
                for (int j = i; j > 0; j--)
                    if (Integer.parseInt(a.get(j-1).PRODUCT_PRICE) <Integer.parseInt(a.get(j).PRODUCT_PRICE))
                    {
                        Product s = a.get(j);
                        a.set(j,a.get(j-1));
                        a.set(j-1,s);
                    }

                    else break;
            return a;
        }
        private List<Product> rsort(List<Product> a) {
            int N = a.size();
            for (int i = 1; i < N; i++)
                for (int j = i; j > 0; j--)
                    if (Integer.parseInt(a.get(j-1).PRODUCT_PRICE) > Integer.parseInt(a.get(j).PRODUCT_PRICE))
                    {
                        Product s = a.get(j);
                        a.set(j,a.get(j-1));
                        a.set(j-1,s);
                    }

                    else break;
            return a;
        }
    }
}

