package kek.kappa.ceitidate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.MyViewHolder> {
    private JSONObject jsonobj = null;
    private JSONArray jsonarr = null;

    public DateAdapter(JSONObject obj) {
        this.jsonobj = obj;
        notifyDataSetChanged();
    }

    public DateAdapter(JSONArray obj) {
        this.jsonarr = obj;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DateAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View item = LayoutInflater.from(parent.getContext()).inflate(R.layout.data_card, parent, false);
        return new MyViewHolder(item);
    }

    @Override
    public void onBindViewHolder(@NonNull DateAdapter.MyViewHolder holder, int position) {
        try {
            if (jsonobj != null) {
                holder.textview.setText(jsonobj.names().getString(position));
                holder.textview.setText(holder.textview.getText() + "\n" + jsonobj.get(jsonobj.names().getString(position)).toString());
            } else if (jsonarr != null) {
                JSONObject jsonobj = jsonarr.getJSONObject(position);
                for (int i = 0; i < jsonobj.length(); i++) {
                    if(i==0)
                        holder.textview.setText(jsonobj.names().getString(i));
                    else
                        holder.textview.setText(holder.textview.getText() + "\n" + jsonobj.names().getString(i));
                    holder.textview.setText(holder.textview.getText() + "\n" + jsonobj.get(jsonobj.names().getString(i)).toString());
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        if (jsonarr != null)
            return jsonarr.length();
        if (jsonobj != null)
            return jsonobj.length();
        return 0;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView textview;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textview = itemView.findViewById(R.id.title);
        }
    }
}
