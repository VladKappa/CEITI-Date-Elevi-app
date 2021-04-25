package kek.kappa.ceitidate;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
                holder.CardTextView.append(jsonobj.names().getString(position));
                holder.CardTextView.append("\n" + jsonobj.get(jsonobj.names().getString(position)).toString());
            } else if (jsonarr != null) {
                JSONObject jsonobj = jsonarr.getJSONObject(position);
                for (int i = 0; i < jsonobj.length(); i++) {

                    // Daca este primul element(primul titlu), nu inseram un newline la inceput
                    if (i == 0)
                        holder.CardTextView.setText(jsonobj.names().getString(i));
                    else
                        holder.CardTextView.append("\n" + jsonobj.names().getString(i));

                    Object check = jsonobj.get(jsonobj.names().getString(i));
                    if (check instanceof JSONArray) {
                        // In caz ca avem de aface cu un JSONArray ca value, atunci il parcurgem (Note)
                        JSONArray Values = jsonobj.getJSONArray(jsonobj.names().getString(i));

                        Object isabsente = Values.get(i);
                        if(isabsente instanceof JSONObject)
                        {
                            for (int j=0;j<Values.length();j++) {
                                JSONObject Absente = Values.getJSONObject(j);
                                holder.CardTextView.append("\n" + Absente.names().getString(0));
                                holder.CardTextView.append("\n" + Absente.get(Absente.names().getString(0)).toString());
                            }

                        }
                        else {
                            holder.CardTextView.append("\n");
                            for (int j = 0; j < Values.length(); j++) {
                                holder.CardTextView.append(Values.getString(j));
                                if (j != Values.length() - 1)
                                    holder.CardTextView.append(", ");
                            }

                        }
                        continue;
                    }

                    // Daca nu este JSONArray, atunci il afisam asa.
                    holder.CardTextView.append("\n" + jsonobj.get(jsonobj.names().getString(i)).toString());
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
        private TextView CardTextView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            CardTextView = itemView.findViewById(R.id.CardTextView);
        }
    }
}
