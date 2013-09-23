package com.tzapps.tzpalette.ui;

import java.util.List;

import android.app.Activity;
import android.app.ListFragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.tzapps.tzpalette.R;
import com.tzapps.tzpalette.data.PaletteData;
import com.tzapps.tzpalette.data.PaletteDataSource;

public class PaletteListFragment extends ListFragment implements OnClickListener
{
    private static final String TAG = "PaletteListFragment";
    
    private PaletteDataSource mSource;
    ArrayAdapter<PaletteData> mAdapter;
    
    @Override
    public void onAttach(Activity activity)
    {
        super.onAttach(activity);
        
        if (mSource == null)
            mSource = new PaletteDataSource(activity);
        
        mSource.open();
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState)
    {
        super.onActivityCreated(savedInstanceState);
        
        List<PaletteData> items = mSource.getAllPaletteData();
        
        if (mAdapter == null)
            mAdapter = new ArrayAdapter<PaletteData>(getActivity(), 
                    android.R.layout.simple_list_item_1, items);
        
        setListAdapter(mAdapter);
    }
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "onCreateView()");
        
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.palette_list_view, container, false);
        
        final Button addBtn = (Button)view.findViewById(R.id.add);
        addBtn.setOnClickListener(this);
        
        final Button deleteBtn = (Button)view.findViewById(R.id.delete);
        deleteBtn.setOnClickListener(this);
        
        return view;
    }
     
    @Override
    public void onListItemClick(ListView l, View v, int position, long id)
    {
        @SuppressWarnings("unchecked")
        ArrayAdapter<PaletteData> adapter = (ArrayAdapter<PaletteData>) getListAdapter();
        PaletteData data = adapter.getItem(position);
        
        Log.i(TAG, "palette data " + data.getId() + " clicked");
        
        //mSource.delete(data);
        //adapter.remove(data);
        //adapter.notifyDataSetChanged();
    }
    
    @Override
    public void onResume()
    {
        mSource.open();
        super.onResume();
    }
    
    @Override
    public void onPause()
    {
        mSource.close();
        super.onPause();
    }
    
    private PaletteData getMocedUpPaletteData()
    {
        PaletteData data = new PaletteData();
        data.setId(1000);
        data.setTitle("test");
        
        return data;
    }

    @Override
    public void onClick(View v)
    {
        PaletteData data = getMocedUpPaletteData();
        
        switch (v.getId())
        {
            case R.id.add:
                Log.d(TAG, "add a new palette data");
                mSource.save(data);
                mAdapter.add(data);
                break;
                
            case R.id.delete:
                Log.d(TAG, "delete a palette data");
                if (mAdapter.getCount() > 0)
                {
                    data = mAdapter.getItem(0);
                    mSource.delete(data);
                    mAdapter.remove(data);
                }
                break;
        }
        
        mAdapter.notifyDataSetChanged();
    }    
}

