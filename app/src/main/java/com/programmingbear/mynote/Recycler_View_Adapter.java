package com.programmingbear.mynote;

import android.content.Context;
import android.database.Cursor;
import android.media.Image;
import android.provider.ContactsContract;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.programmingbear.mynote.note;
import java.util.Collections;
import java.util.List;

/**
 * Created by satish on 17/11/2016.
 */
public class Recycler_View_Adapter extends RecyclerView.Adapter<Recycler_View_Adapter.View_Holder> {

    List<note> list=Collections.emptyList();
    Context context;
    ClickListener clickListener=null;
    MyNotes mynotes=new MyNotes();
    NDb nd;


    public Recycler_View_Adapter(List<note> list, Context context) {
        this.list = list;
        this.context = context;
        nd=new NDb(context);

    }

    public class View_Holder extends RecyclerView.ViewHolder implements View.OnClickListener{

        CardView card_view;
        TextView txtremark;
        TextView txtdate;
        TextView txtnamerow;
        ImageView isStarred;

        public View_Holder(final View itemView) {
            super(itemView);
            card_view=(CardView)itemView.findViewById(R.id.cardview);
            txtremark=(TextView)itemView.findViewById(R.id.txtremark);
            //txtdate=(TextView)itemView.findViewById(R.id.txtdate);
            txtnamerow=(TextView)itemView.findViewById(R.id.txtnamerow);
            isStarred=(ImageView)itemView.findViewById(R.id.is_starred);

            isStarred.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view) {

                    if(isStarred.getTag().equals("R.drawable.ic_empty_star")){
                    isStarred.setImageResource(R.drawable.ic_filled_star);
                    }else{
                    isStarred.setImageResource(R.drawable.ic_empty_star);
                    }
                    if(clickListener!=null){
                        clickListener.imageClicked(txtnamerow.getText().toString(),isStarred.getTag().toString());
                    }
                }
            });

            itemView.setOnClickListener(this);

        }

        @Override
        public void onClick(View view) {

            if(clickListener!=null){
                clickListener.itemClicked(txtnamerow.getText().toString(),txtremark.getText().toString());
            }
        }
    }

    public void setClickListener(ClickListener clicklistener) {
        this.clickListener = clicklistener;
    }

    @Override
    public View_Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.row_layout,parent,false);
        View_Holder holder=new View_Holder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder( final View_Holder holder,final int position) {

        final int isStarred=list.get(position).getisStarred();
        holder.txtnamerow.setText(list.get(position).getName());
      //  holder.txtdate.setText(list.get(position).getDates());
        holder.txtremark.setText(list.get(position).getRemark());
        /*holder.isStarred.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int starred=list.get(position).getisStarred();

                if(starred==1) {
                    holder.isStarred.setImageResource(R.drawable.ic_empty_star);
                    nd.setImpBool(list.get(position).getName(),0);
                }else{
                    holder.isStarred.setImageResource(R.drawable.ic_filled_star);
                    nd.setImpBool(list.get(position).getName(),1);
                }

            }
        });*/

        if(isStarred==1) {
            holder.isStarred.setImageResource(R.drawable.ic_filled_star);
            holder.isStarred.setTag("R.drawable.ic_filled_star");
        }else{
            holder.isStarred.setImageResource(R.drawable.ic_empty_star);
            holder.isStarred.setTag("R.drawable.ic_empty_star");
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
    public void insert(int position,note note){
        list.add(position,note);
        notifyItemInserted(position);
    }
    public void remove(note note){
        int position = list.indexOf(note);
        list.remove(position);
        notifyItemRemoved(position);
    }
}


