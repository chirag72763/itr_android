package com.codearlystudio.homedelivery.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.codearlystudio.homedelivery.MasterActivity;
import com.codearlystudio.homedelivery.R;

import static android.content.Context.INPUT_METHOD_SERVICE;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>{

    String[] SubjectValues;
    Context context;
    View view1;
    ViewHolder viewHolder1;

    public RecyclerViewAdapter(Context context1,String[] SubjectValues1){
        SubjectValues = SubjectValues1;
        context = context1;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        public TextView textView;
        public LinearLayout linearOptions;

        public ViewHolder(View v){
            super(v);
            textView = (TextView)v.findViewById(R.id.hint1);
            linearOptions =(LinearLayout)v.findViewById(R.id.options);
        }
    }

    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        view1 = LayoutInflater.from(context).inflate(R.layout.recyclerview_items,parent,false);
        viewHolder1 = new ViewHolder(view1);
        return viewHolder1;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, final int position){
        holder.textView.setText(SubjectValues[position]);
        holder.linearOptions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)context.getSystemService(INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                MasterActivity.etSearch.setText(SubjectValues[position]);
                MasterActivity.etSearch.setSelection(SubjectValues[position].length());
            }
        });
    }

    @Override
    public int getItemCount(){
        return SubjectValues.length;
    }
}