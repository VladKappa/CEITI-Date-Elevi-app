package kek.kappa.ceitidate;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    static Context c;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        c=this.getApplicationContext();
    }

    public static Context getContext(){
        return c;
    }

    public void sendIDNP(View view) {
        Elev e = new Elev("IDNP");
        try {
            e.getDate();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }
        /*        startActivity(new Intent(MainActivity.this,DateActivity.class));
        finish();*/
    }
}