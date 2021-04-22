package kek.kappa.ceitidate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    static Context context;
    static ProgressBar ProgressBar;
    static TextView idnp_input;
    static Elev elev;
    static Activity act;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        switch (getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK) {
            case Configuration.UI_MODE_NIGHT_YES:
                setTheme(R.style.AppThemeDark);
                break;
            case Configuration.UI_MODE_NIGHT_NO:
                setTheme(R.style.AppTheme);
                break;
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this.getApplicationContext();
        ProgressBar = findViewById(R.id.progressBar);
        idnp_input = findViewById(R.id.idnp_input);
        act = this;


    }

    public void sendIDNP(View view) {
        elev = new Elev(idnp_input.getText().toString());
        try {
            elev.processIDNP();
        } catch (JSONException jsonException) {
            jsonException.printStackTrace();
        }

        // Aratam widget-ul cu progresul
        ProgressBar.setVisibility(View.VISIBLE);
        ProgressBar.bringToFront();

        // Hai sa ascundem si celelalte elemente din spatele widget-ului cu progresul
        idnp_input.setVisibility(View.GONE);
        TextView label = findViewById(R.id.idnp_label);
        label.setVisibility(View.GONE);
        Button btn = findViewById(R.id.idnp_btn);
        btn.setVisibility(View.GONE);

        // Hide the keyboard
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

//    Se va executa atunci cand request-ul e finalizat
//    Va deschide Activitatea ce contine Drawer-ul.
    public static void initContent() {
        Intent intent = new Intent(context, DateActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // Idk man, stackoverflow says it's not a good idea but whatever
        context.startActivity(intent);
        act.finish();
    }

}