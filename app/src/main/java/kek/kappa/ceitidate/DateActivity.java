package kek.kappa.ceitidate;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONException;
import org.json.JSONObject;

public class DateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    static TextView home_text;
    static NavigationView navigationView;
    static Toolbar toolbar;
    static DrawerLayout drawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_date);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.



        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        home_text = findViewById(R.id.text_main);

        JSONObject elev = MainActivity.elev.getJSON();
        System.out.println(elev.names().length());
        try {
            for (int i = 0; i < elev.names().length(); i++) {
                navigationView.getMenu().add(elev.names().getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            toolbar.setTitle(elev.names().getString(0));
            navigationView.setCheckedItem(0);
            navigationView.getMenu().getItem(0).setChecked(true);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        navigationView.setNavigationItemSelectedListener(this);
    }

    static final long THRESHOLD = 2000;
    long backLastPressed = 0;

    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - backLastPressed < THRESHOLD) {
            finish();
            // TODO: Register double-tapped BACK button, put your "exit" code here
            backLastPressed = 0;
            return;
        }
        backLastPressed = System.currentTimeMillis();
        // Otherwise, ignore this BACK press
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        int size = navigationView.getMenu().size();
        for (int i = 0; i < size; i++) {
            navigationView.getMenu().getItem(i).setChecked(false);
        }
        navigationView.setCheckedItem(menuItem.getItemId());
        menuItem.setChecked(true);

        System.out.println(menuItem.getTitle());
        toolbar.setTitle(menuItem.getTitle());
        drawer.close();

//        switch (menuItem.getItemId()) {
//            case R.id.nav_account:
//                // do you click actions for the first selection
//                break;
//            case R.id.nav_setting:
//                // do you click actions for the second selection
//                break;
//            case R.id.nav_logout:
//                // do you click actions for the third selection
//                break;
//        }

        return true;
    }
}