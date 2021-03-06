package com.sidzi.circleofmusic.adapters;


import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;
import com.sidzi.circleofmusic.R;
import com.sidzi.circleofmusic.entities.Potm;
import com.sidzi.circleofmusic.services.MusicPlayerService;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

import static com.sidzi.circleofmusic.config.com_url;

public class PotmAdapter extends RecyclerView.Adapter<PotmAdapter.ViewHolder> {
    private ArrayList<Potm> potms = new ArrayList<>();
    private Context mContext;

    public PotmAdapter(Context mContext) {
        super();
        this.mContext = mContext;
        RequestQueue requestQueue = Volley.newRequestQueue(mContext);
        JsonArrayRequest trackRequest = new JsonArrayRequest(Request.Method.GET, com_url + "getPOTMs", null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    for (int i = 0; i < response.length(); i++) {
                        potms.add(new Potm(response.getJSONObject(i)));
                    }
                    notifyDataSetChanged();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
//                Couldn't fetch data
            }
        });
        requestQueue.add(trackRequest);
    }

    @Override
    public PotmAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row_potm, parent, false);
        return new PotmAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(PotmAdapter.ViewHolder holder, int position) {
        try {
            holder.tvPotmMonth.setText(new DateFormatSymbols().getMonths()[potms.get(position).getMonth() - 1]);
        } catch (ArrayIndexOutOfBoundsException ignore) {
        }
        holder.tvPotmTitle.setText(potms.get(position).getTitle());
        holder.tvPotmDescription.setText(potms.get(position).getDescription());
        holder.itemView.setTag(potms.get(position).getPath());
        holder.itemView.setTag(R.id.tag_track_path, potms.get(position).getPath());
        holder.itemView.setTag(R.id.tag_track_name, potms.get(position).getTitle());
        holder.itemView.setTag(R.id.tag_track_artist, potms.get(position).getDescription());
    }

    @Override
    public int getItemCount() {
        return potms.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private TextView tvPotmMonth;
        private TextView tvPotmTitle;
        private TextView tvPotmDescription;

        ViewHolder(View itemView) {
            super(itemView);
            tvPotmTitle = (TextView) itemView.findViewById(R.id.tvPotmTitle);
            tvPotmDescription = (TextView) itemView.findViewById(R.id.tvPotmDescription);
            tvPotmMonth = (TextView) itemView.findViewById(R.id.tvPotmMonth);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(final View v) {
            Intent intent = new Intent(mContext, MusicPlayerService.class);
            ServiceConnection mMusicServiceConnection = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
                    MusicPlayerService.MusicBinder musicBinder = (MusicPlayerService.MusicBinder) iBinder;
                    musicBinder.getService().play(v.getTag(R.id.tag_track_path).toString(), v.getTag(R.id.tag_track_artist).toString(), v.getTag(R.id.tag_track_name).toString());
                    mContext.unbindService(this);
                }

                @Override
                public void onServiceDisconnected(ComponentName componentName) {

                }
            };
            mContext.bindService(intent, mMusicServiceConnection, Context.BIND_AUTO_CREATE);
        }
    }
}
