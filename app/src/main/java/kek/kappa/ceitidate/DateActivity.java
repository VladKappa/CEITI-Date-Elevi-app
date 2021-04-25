package kek.kappa.ceitidate;

import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import ru.dimorinny.floatingtextbutton.FloatingTextButton;

public class DateActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private AppBarConfiguration mAppBarConfiguration;

    static NavigationView navigationView;
    static Toolbar toolbar;
    static DrawerLayout drawer;
    static JSONObject elev;
    static RecyclerView rec;
    static ArrayList<Integer> addedViews;

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
        setContentView(R.layout.activity_date);
        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_main)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);

        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        rec = findViewById(R.id.recyclerview);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(getApplicationContext());
        rec.setLayoutManager(manager);


        elev = MainActivity.elev.getJSON();


        try {
            for (int i = 0; i < elev.names().length(); i++) {
                navigationView.getMenu().add(elev.names().getString(i));
            }
            toolbar.setTitle(elev.names().getString(0));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        navigationView.getMenu().add(Menu.NONE, 69, Menu.NONE, "Ieșire");

        setActivityData(navigationView.getMenu().getItem(0), null);
        navigationView.setCheckedItem(0);
        navigationView.getMenu().getItem(0).setChecked(true);

        navigationView.setNavigationItemSelectedListener(this);

        addedViews = new ArrayList<Integer>();
    }

    static final long THRESHOLD = 2000;
    long backLastPressed = 0;

    @Override
    public void onBackPressed() {
        Toast.makeText(getBaseContext(), "Mai apasa odata BACK pentru a iesi!", Toast.LENGTH_SHORT).show();
        if (System.currentTimeMillis() - backLastPressed < THRESHOLD) {
            finish();
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

    // Verifica daca JSONObject-ul dat ca paramentru are ca value
    // un JSONArray, ceea ce inseamna ca sunt submeniuri
    private boolean hasSubmenus(JSONObject obj) {
        try {
            if (obj.getJSONArray(obj.names().getString(0)) instanceof JSONArray) {
                return true;
            }
        } catch (Exception e) { }
        return false;
    }

    public void setActivityData(MenuItem itm, String selectedsubmenu) {
        ConstraintLayout constraintLayout = findViewById(R.id.content_layout);


        if (addedViews != null && addedViews.size() > 0)
            for (int i = 0; i < addedViews.size(); i++) {
                constraintLayout.removeView(findViewById(addedViews.get(i)));
                addedViews.remove(i);
                i--;
            }

        try {

            Object obj = elev.get(itm.getTitle().toString());
            DateAdapter dateAdapter;
            boolean hasSubmenus;
            try {
                hasSubmenus = hasSubmenus(new JSONObject(elev.getJSONObject(itm.getTitle().toString()).toString()));
            } catch (JSONException ex) { // idk lol exception go brr
                hasSubmenus = false;
            }
            if (hasSubmenus) {
                int len = elev.getJSONObject(itm.getTitle().toString()).length();
                FloatingTextButton[] butoane = new FloatingTextButton[len]; // Array de butoane weee~~~~~


                ConstraintSet constraintSet = new ConstraintSet();


                for (int i = 0; i < len; i++) {
                    ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(ConstraintLayout.LayoutParams.WRAP_CONTENT, ConstraintLayout.LayoutParams.WRAP_CONTENT);


                    butoane[i] = new FloatingTextButton(this, null);

                    butoane[i].setId(i + 420); // ma asigur ca o sa aiba un id unic, hz lol

                    String value = elev.getJSONObject(itm.getTitle().toString()).names().getString(i);
                    if (selectedsubmenu == null)
                        selectedsubmenu = value;
                    butoane[i].setTitle(value);

                    butoane[i].setTitleColor(Color.argb(150,255, 255, 255));
                    butoane[i].setBackgroundColor(Color.argb(150,100, 100, 255));

                    if (butoane[i].getTitle() == selectedsubmenu) {
                        butoane[i].setTitleColor(Color.argb(150,0, 0, 0));
                        butoane[i].setBackgroundColor(Color.argb(150,255, 255, 255));
                    }

                    butoane[i].setForegroundGravity(Gravity.CENTER); // This line does nothing but I'll leave it here just for lulz

                    constraintLayout.addView(butoane[i]);


                    constraintSet.clone(constraintLayout);
                    constraintSet.connect(butoane[i].getId(), ConstraintSet.START, R.id.nav_host_fragment, ConstraintSet.START);
                    constraintSet.connect(butoane[i].getId(), ConstraintSet.END, R.id.nav_host_fragment, ConstraintSet.END);
                    constraintSet.connect(butoane[i].getId(), ConstraintSet.BOTTOM, R.id.nav_host_fragment, ConstraintSet.BOTTOM);
                    if (i > 0) {
                        constraintSet.connect(butoane[i].getId(), ConstraintSet.BOTTOM, butoane[i - 1].getId(), ConstraintSet.TOP);

                        layoutParams.setMargins(0, 0, 0, 20 * i);
                        butoane[i].setLayoutParams(layoutParams);

                    }
                    constraintSet.setHorizontalBias(butoane[i].getId(), (float) 0.95);
                    constraintSet.setVerticalBias(butoane[i].getId(), (float) 0.98);

                    constraintSet.applyTo(constraintLayout);

                    addedViews.add(butoane[i].getId());

                    butoane[i].setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            setActivityData(itm, value);
                        }
                    });
                }
            }


            if (obj instanceof JSONObject) {
                if (selectedsubmenu != null)
                    dateAdapter = new DateAdapter(elev.getJSONObject(itm.getTitle().toString()).getJSONArray(selectedsubmenu));
                else
                    dateAdapter = new DateAdapter(new JSONObject(elev.getJSONObject(itm.getTitle().toString()).toString()));

            } else {
                dateAdapter = new DateAdapter(new JSONArray(elev.getJSONArray(itm.getTitle().toString()).toString()));
            }

            dateAdapter.notifyDataSetChanged();
            rec.setAdapter(dateAdapter);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onNavigationItemSelected(MenuItem menuItem) {
        // Daca este selectat butonul magic (Iesire), atunci finisam activitatea.
        if (menuItem.getItemId() == 69) {
            finish();
            return false;
        }

        // Parcurgem si deselectam toate item-urile din navigationview menu
        for (int i = 0; i < navigationView.getMenu().size(); i++)
            navigationView.getMenu().getItem(i).setChecked(false);

        // Setam ca marcat menuitem-ul selectat
        navigationView.setCheckedItem(menuItem.getItemId());
        menuItem.setChecked(true);

        // Setam datele in RecyclerView pentru itemul selectat

        setActivityData(menuItem, null);
        // Setam titlul in appbar
        toolbar.setTitle(menuItem.getTitle());

        // Inchidem "saltariu"
        drawer.close();

        return true;
    }
}