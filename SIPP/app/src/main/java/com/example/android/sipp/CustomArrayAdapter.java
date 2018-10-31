package com.example.android.sipp;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class CustomArrayAdapter extends ArrayAdapter<EachPerson> {

    public CustomArrayAdapter(Activity context, int resource, ArrayList<EachPerson> eachPerson){

        super(context,resource,eachPerson);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View cardView = convertView;    //view that is inflating the layout
        if(cardView == null){
            cardView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item, parent, false);
        }

        EachPerson mEachPerson = getItem(position);
        ImageView mPersonImg = cardView.findViewById(R.id.img_person_cardview);
        TextView mName = cardView.findViewById(R.id.txt_name);
        mName.setText(mEachPerson.getName());

        TextView mPersonVotes = cardView.findViewById(R.id.txt_votes);
        mPersonVotes.setText(""+mEachPerson.getVotes());

        switch (mEachPerson.getProfileImageUrl()){
            case "default":
                Glide.with(cardView.getContext()).load(R.mipmap.ic_launcher_round).into(mPersonImg);
            default:
                Glide.clear(mPersonImg);
                Glide.with(cardView.getContext()).load(mEachPerson.getProfileImageUrl()).into(mPersonImg);
                break;
        }
        return cardView;
    }
}

