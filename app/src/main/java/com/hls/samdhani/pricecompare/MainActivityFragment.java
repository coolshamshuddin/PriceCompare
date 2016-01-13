package com.hls.samdhani.pricecompare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private ArrayAdapter<String> priceAdapter;

    public MainActivityFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.menu_frag, menu);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        Button compareButton = (Button)rootView.findViewById(R.id.compare_button);
        final EditText productText = (EditText)rootView.findViewById(R.id.input_text);


        compareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(productText.getText().toString().matches(""))
                    productText.setError("Enter the Text");
                else{
                    String productName = productText.getText().toString();
                    Intent startListActivity = new Intent(getActivity(), ListActivity.class);
                    startListActivity.putExtra(Intent.EXTRA_TEXT, productName);
                    startActivity(startListActivity);
                }
            }
        });

        return rootView;
    }

}
