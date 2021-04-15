package kek.kappa.ceitidate;

import android.content.Context;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c=this.getApplicationContext();
        pb = findViewById(R.id.progressBar);
        idnp_input = findViewById(R.id.idnp_input);
    }

    public void sendIDNP(View view) {
        Elev e = new Elev(idnp_input.getText().toString());
        try {
            e.getDate();
            pb.setVisibility(View.VISIBLE);
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        /*        startActivity(new Intent(MainActivity.this,DateActivity.class));
        finish();*/
    }

}