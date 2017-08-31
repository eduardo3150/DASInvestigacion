package com.chavez.eduardo.dasinvestigacion;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chavez.eduardo.dasinvestigacion.adapters.MainRecyclerViewAdapter;
import com.chavez.eduardo.dasinvestigacion.adapters.RecyclerViewItemListener;
import com.chavez.eduardo.dasinvestigacion.network.GameClient;
import com.chavez.eduardo.dasinvestigacion.network.GameObject;
import com.chavez.eduardo.dasinvestigacion.utils.PrefKey;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewItemListener {
    CoordinatorLayout coordinatorLayout;
    View bottomSheet;
    AppCompatButton bottomSheetButton;
    BottomSheetBehavior behavior;
    FloatingActionButton fab;
    List<GameObject> itemsFromWeb = new ArrayList<>();
    List<GameObject> localItems = new ArrayList<>();
    RecyclerView mainRecyclerView;
    private SwipeRefreshLayout refreshLayout;


    /**
     * Formulario
     **/
    private TextView toggleAdvanced;
    private EditText newGameTitle, newGameDescription, newGameReleaseDate, newGameDeveloper, newGameWebsite;
    private LinearLayout advancedContainer;
    private ViewGroup viewGroup;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        refreshLayout = (SwipeRefreshLayout) findViewById(R.id.refreshContainer);
        coordinatorLayout = (CoordinatorLayout) findViewById(R.id.coordinator);
        bottomSheet = coordinatorLayout.findViewById(R.id.bottom_sheet);
        behavior = BottomSheetBehavior.from(bottomSheet);
        mainRecyclerView = (RecyclerView) findViewById(R.id.mainRecyclerView);
        mainRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                networkRequest();
            }
        });

        refreshLayout.setRefreshing(true);
        networkRequest();

        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (behavior.getState() != BottomSheetBehavior.STATE_EXPANDED) {
                    behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                } else {
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);

                }
            }
        });
        setUpBottonSheet();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setUpBottonSheet() {
        viewGroup = (ViewGroup) bottomSheet.findViewById(R.id.formContainerGroup);
        toggleAdvanced = (TextView) bottomSheet.findViewById(R.id.openAdvanced);
        advancedContainer = (LinearLayout) bottomSheet.findViewById(R.id.advancedContainer);
        newGameTitle = (EditText) bottomSheet.findViewById(R.id.newGameName);
        newGameDescription = (EditText) bottomSheet.findViewById(R.id.newGameDescription);
        newGameDeveloper = (EditText) bottomSheet.findViewById(R.id.newGameDeveloper);
        newGameReleaseDate = (EditText) bottomSheet.findViewById(R.id.newReleaseDate);
        newGameWebsite = (EditText) bottomSheet.findViewById(R.id.newWebsite);
        bottomSheetButton = (AppCompatButton) bottomSheet.findViewById(R.id.addToList);
        bottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workAndAddNewItem();
                Snackbar.make(view, "Elemento agregado", Snackbar.LENGTH_SHORT).show();
            }
        });

        toggleAdvanced.setOnClickListener(new View.OnClickListener() {
            boolean visible;

            @Override
            public void onClick(View view) {
                visible = !visible;
                advancedContainer.setVisibility(visible ? View.VISIBLE : View.GONE);
            }
        });

    }

    private void workAndAddNewItem() {

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (behavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else {
            super.onBackPressed();
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            SharedPreferences sharedPreferences = getSharedPreferences(PrefKey.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PrefKey.FIRST_BOOT, true);
            editor.putBoolean(PrefKey.LOGGED_IN, false);
            editor.apply();
            Snackbar.make(getCurrentFocus(), "Intro activada", Snackbar.LENGTH_SHORT).show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            Snackbar.make(getCurrentFocus(), "Perfil", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_gallery) {
            Snackbar.make(getCurrentFocus(), "Favoritos", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_slideshow) {
            Snackbar.make(getCurrentFocus(), "Comentarios", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_share) {
            Snackbar.make(getCurrentFocus(), "Compartir app", Snackbar.LENGTH_SHORT).show();
        } else if (id == R.id.nav_send) {
            SharedPreferences sharedPreferences = getSharedPreferences(PrefKey.SHARED_PREF_NAME, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putBoolean(PrefKey.LOGGED_IN, false);
            editor.apply();
            startActivity(new Intent(MainActivity.this, LoginActivity.class));
            MainActivity.this.finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void networkRequest() {
        itemsFromWeb.clear();
        GameClient client = new Retrofit.Builder()
                .baseUrl(GameClient.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build().create(GameClient.class);

        final Call<List<GameObject>> request = client.getGames();

        request.enqueue(new Callback<List<GameObject>>() {
            @Override
            public void onResponse(Call<List<GameObject>> call, Response<List<GameObject>> response) {
                if (response.isSuccessful()) {
                    itemsFromWeb = response.body();
                    generateRecyclerView(itemsFromWeb);
                }
            }

            @Override
            public void onFailure(Call<List<GameObject>> call, Throwable t) {

            }
        });

    }

    private void generateRecyclerView(List<GameObject> gameObjects) {
        List<GameObject> recyclerFiller = new ArrayList<>();
        recyclerFiller.clear();
        recyclerFiller.addAll(gameObjects);
        mainRecyclerView.setAdapter(new MainRecyclerViewAdapter(recyclerFiller, this, this));
        refreshLayout.setRefreshing(false);
    }

    @Override
    public void onReciclerViewItemClick(int pos, GameObject object, ImageView sharedImage) {


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("object", object);
            intent.putExtra("transition", ViewCompat.getTransitionName(sharedImage));
            ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation(MainActivity.this,
                    sharedImage,
                    ViewCompat.getTransitionName(sharedImage));
            startActivity(intent, options.toBundle());
        } else {
            Intent intent = new Intent(MainActivity.this, DetailActivity.class);
            intent.putExtra("object", object);
            startActivity(intent);
        }
    }


}
