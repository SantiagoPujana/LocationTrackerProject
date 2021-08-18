package com.locationtracker.redzones;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.locationtracker.Language;
import com.locationtracker.R;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter<ListAdapter.ViewHolder> {

    private final List<ListElement> itemList;
    private final LayoutInflater inflater;
    private final Context context;

    final ListAdapter.OnItemClickListener listener;

    public interface OnItemClickListener{

        void onItemClick(ListElement item, String id);
    }

    public ListAdapter(List<ListElement> itemList, Context context, ListAdapter.OnItemClickListener listener){

        this.inflater = LayoutInflater.from(context);
        this.itemList = itemList;
        this.context = context;
        this.listener = listener;
    }

    @Override
    public int getItemCount(){ return itemList.size(); }

    @NonNull
    @Override
    public ListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType){

        @SuppressLint("InflateParams") View view = inflater.inflate(R.layout.list_element, null);

        return new ViewHolder(view, context);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.setData(itemList.get(position));
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        private final TextView name;
        private final TextView longitude;
        private final TextView latitude;
        private final TextView radius;
        private final Language language;
        private final Button delete;
        private final ImageButton edit;

        ViewHolder(View itemView, Context context){
            super(itemView);

            name = itemView.findViewById(R.id.redZoneName);
            longitude = itemView.findViewById(R.id.longitude);
            latitude = itemView.findViewById(R.id.latitude);
            radius = itemView.findViewById(R.id.radius);
            delete = itemView.findViewById(R.id.delete);
            edit = itemView.findViewById(R.id.edit);
            language = Language.getInstance(context);
        }

        @SuppressLint("SetTextI18n")
        public void setData(final ListElement listElement) {

            name.setText(listElement.getRedZoneName());
            longitude.setText(language.getLongitude()+listElement.getLongitude());
            latitude.setText(language.getLatitude()+listElement.getLatitude());
            radius.setText(language.getRadius()+listElement.getRadius()+" m");
            delete.setOnClickListener(v -> listener.onItemClick(listElement, "delete"));
            edit.setOnClickListener(v -> listener.onItemClick(listElement, "edit"));
        }
    }
}
