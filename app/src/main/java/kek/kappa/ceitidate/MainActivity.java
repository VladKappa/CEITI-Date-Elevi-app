package kek.kappa.ceitidate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    static Context c;
    static ProgressBar pb;
    static TextView idnp_input;
    static Elev elev;
    static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c=this.getApplicationContext();
        pb = findViewById(R.id.progressBar);
        idnp_input = findViewById(R.id.idnp_input);
        act = this;
    }

    public void sendIDNP(View view) {
        elev = new Elev(idnp_input.getText().toString());
        try {
            elev.getDate();
            pb.setVisibility(View.VISIBLE);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
    }

    public static void init(){
        Intent intent = new Intent(c, DateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Idk man, stackoverflow says it's not a good idea but whatever
        c.startActivity(intent);
        act.finish();
    }

}