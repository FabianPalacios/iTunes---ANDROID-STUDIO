package com.fabianpalacios.itunesapi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.fabianpalacios.itunesapi.Service.model.Result;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.List;


public class CustomListAdapter extends BaseAdapter {

    private Context context = null;
    private List<Result> results = null;

    public CustomListAdapter(Context newContext, List<Result> newResults){
        context = newContext;
        results = newResults;
    }

    @Override
    public int getCount() {
        return results.size();
    }

    @Override
    public Object getItem(int i) {
        return results.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int pos, View convertView, ViewGroup parent) {
        final int ROW_RESOURCE = R.layout.layout_list_view_item;
        ViewHolder viewHolder = null;


        //ConvertView es null entonces no tiene elementos creados
        if (convertView == null){
            LayoutInflater layout = LayoutInflater.from(context);
            convertView = layout.inflate(ROW_RESOURCE, parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        }else{
            //En caso de que este creado se lo recupera
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Result result = results.get(pos);
        try {
            viewHolder.txtTrackName.setText(result.getTrackName());
            viewHolder.txtArtistName.setText(result.getArtistName());
            Picasso.get().load(result.getArtworkUrl100()).error(R.drawable.musica).into(viewHolder.imgPhoto);
            String destFilename = context.getCacheDir() + "/" + result.getTrackId() + ".m4a";
            if (new File(destFilename).exists()) {
                viewHolder.imgAction.setImageResource(R.drawable.play);
                result.setState(2);
            } else {
                viewHolder.imgAction.setImageResource(R.drawable.descarga);
                result.setState(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        return convertView;
    }

    public static class ViewHolder{
        ImageView imgPhoto = null;
        TextView txtTrackName = null;
        TextView txtArtistName = null;
        ImageView imgAction = null;

        public ViewHolder(View view){
            imgPhoto = view.findViewById(R.id.imgPhoto);
            txtTrackName = view.findViewById(R.id.txtTrackName);
            txtArtistName = view.findViewById(R.id.txtArtistName);
            imgAction = view.findViewById(R.id.imgAction);
        }

    }


}
