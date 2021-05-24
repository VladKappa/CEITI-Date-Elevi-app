package kek.kappa.ceitidate;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONException;

public class MainActivity extends AppCompatActivity {

    static Context context;
    static ProgressBar ProgressBar;
    static EditText idnp_input;
    static Elev elev;
    static Activity act;
    static TextView label;
    static Button btn;

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

        label = findViewById(R.id.idnp_label);
        btn = findViewById(R.id.idnp_btn);
    }

    public void showToast(String message){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public void changeViewVisibility(boolean hide)
    {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
        if(hide)
        {
            ProgressBar.setVisibility(View.VISIBLE);
            ProgressBar.bringToFront();


            label.setVisibility(View.GONE);
            btn.setVisibility(View.GONE);
            idnp_input.setVisibility(View.GONE);
            return;
        }

        ProgressBar.setVisibility(View.GONE);
        label.setVisibility(View.VISIBLE);
        btn.setVisibility(View.VISIBLE);
        idnp_input.setVisibility(View.VISIBLE);

            }
        });
    }

    public void sendIDNP(View view) {
        elev = new Elev(idnp_input.getText().toString());

        elev.processIDNP();

        // Aratam widget-ul cu progresul
        changeViewVisibility(true);

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