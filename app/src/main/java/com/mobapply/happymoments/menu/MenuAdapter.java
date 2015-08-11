package com.mobapply.happymoments.menu;

import android.app.Activity;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobapply.happymoments.R;

import java.util.Collections;
import java.util.List;

/**
 * Created by igor on 3/7/15.
 */
public class MenuAdapter extends BaseAdapter {

    private static final String TAG = MenuAdapter.class.getCanonicalName();
    private List<MenuItem> list = Collections.emptyList();
    private Activity context;
    private LayoutInflater inflater;

    private AdapterView.OnItemClickListener listener;

    public MenuAdapter(Activity context, List<MenuItem> list) {
        super();
        this.context = context;
        this.list = list;
        inflater= context.getLayoutInflater();
    }


    public void setListener(AdapterView.OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public MenuItem getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        View rowView = convertView;

        if (rowView == null) {
            rowView = inflater.inflate(R.layout.item_menu, null);
            ViewHolder viewHolder = new ViewHolder();
            viewHolder.text = (TextView) rowView.findViewById(R.id.text);
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.icon);
            rowView.setTag(viewHolder);
        }



        ViewHolder holder = (ViewHolder) rowView.getTag();
        MenuItem item = list.get(position);
        Resources resources=context.getResources();
        Drawable icon=null;
        switch (position){
            case 0:
                if (item.isSelected()){
                    icon=resources.getDrawable(R.drawable.ic_folder_turquoise_24dp);
                }else{
                    icon=resources.getDrawable(R.drawable.ic_folder_brown_24dp);
                }
                break;
            case 1:
                if (item.isSelected()){
                    icon=resources.getDrawable(R.drawable.ic_settings_turquoise_24dp);
                }else{
                    icon=resources.getDrawable(R.drawable.ic_settings_brown_24dp);
                }
                break;
            case 2:
                if (item.isSelected()){
                    icon=resources.getDrawable(R.drawable.ic_exit_turquoise_24dp);
                }else{
                    icon=resources.getDrawable(R.drawable.ic_exit_brown_24dp);
                }
                break;
        }
        holder.icon.setImageDrawable(icon);

        holder.text.setText(context.getString(item.getTextId()));
        if(item.isSelected()){
            holder.text.setTextColor(context.getResources().getColor(R.color.color_selected_menu));
         }
        else{
            holder.text.setTextColor(context.getResources().getColor(R.color.color_text_settings));
        }


        return rowView;
    }

    class ViewHolder {
        public ImageView icon;
        public TextView text;
    }
}
