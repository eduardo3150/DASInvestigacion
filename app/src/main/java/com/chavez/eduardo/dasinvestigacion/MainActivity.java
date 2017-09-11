package com.chavez.eduardo.dasinvestigacion;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.chavez.eduardo.dasinvestigacion.adapters.MainRecyclerViewAdapter;
import com.chavez.eduardo.dasinvestigacion.adapters.RecyclerViewItemListener;
import com.chavez.eduardo.dasinvestigacion.network.DeveloperObject;
import com.chavez.eduardo.dasinvestigacion.network.GameClient;
import com.chavez.eduardo.dasinvestigacion.network.GameObject;
import com.chavez.eduardo.dasinvestigacion.utils.NetworkUtils;
import com.chavez.eduardo.dasinvestigacion.utils.PrefKey;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.engine.impl.PicassoEngine;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, RecyclerViewItemListener, AdapterView.OnItemSelectedListener {
    private static final int REQUEST_CODE_CHOOSE = 432;
    CoordinatorLayout coordinatorLayout;
    View bottomSheet;
    AppCompatButton bottomSheetButton;
    BottomSheetBehavior behavior;
    FloatingActionButton fab;
    List<GameObject> itemsFromWeb = new ArrayList<>();
    List<DeveloperObject> developerObjects = new ArrayList<>();
    RecyclerView mainRecyclerView;
    private SwipeRefreshLayout refreshLayout;


    /**
     * Formulario
     **/
    private TextView toggleAdvanced;
    private EditText newGameTitle, newGameDescription, newGameReleaseDate, newGameWebsite, newGameCategory;
    private String newTitleGame, newDescriptionGame, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame;
    private boolean work;
    private EditText newPlatform;
    private LinearLayout advancedContainer;
    private ViewGroup viewGroup;
    private Spinner developersSpinner;
    private ArrayAdapter arrayAdapter;


    private GameClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GameClient.class);
    private int developer_id = 0;
    private AppCompatButton pictureFromGallery;
    private File file;
    private ImageView thumbnail;

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
        setSpinner();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

    }

    private void setSpinner() {
        Call<List<DeveloperObject>> call = client.getDevelopers();
        call.enqueue(new Callback<List<DeveloperObject>>() {
            @Override
            public void onResponse(Call<List<DeveloperObject>> call, Response<List<DeveloperObject>> response) {
                developerObjects = response.body();
                createSpinner(developerObjects);
            }

            @Override
            public void onFailure(Call<List<DeveloperObject>> call, Throwable t) {

            }
        });
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
        newGameReleaseDate = (EditText) bottomSheet.findViewById(R.id.newReleaseDate);
        newGameWebsite = (EditText) bottomSheet.findViewById(R.id.newWebsite);
        newGameCategory = (EditText) bottomSheet.findViewById(R.id.newCategoria);
        newPlatform = (EditText) bottomSheet.findViewById(R.id.newPlatform);
        developersSpinner = (Spinner) bottomSheet.findViewById(R.id.developerList);
        bottomSheetButton = (AppCompatButton) bottomSheet.findViewById(R.id.addToList);
        pictureFromGallery = (AppCompatButton) bottomSheet.findViewById(R.id.addPicture);
        thumbnail = (ImageView) bottomSheet.findViewById(R.id.thumbnailRecibido);
        bottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workAndAddNewItem();

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

        pictureFromGallery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getPicture();
            }
        });
    }


    private void workAndAddNewItem() {
        newTitleGame = newGameTitle.getText().toString();
        newDescriptionGame = newGameDescription.getText().toString();
        newReleaseDateGame = newGameReleaseDate.getText().toString();
        newWebsiteGame = newGameWebsite.getText().toString();
        newCategoryGame = newGameCategory.getText().toString();
        newPlatformGame = newPlatform.getText().toString();

        newGameTitle.setError(null);
        newGameDescription.setError(null);

        if (newTitleGame.isEmpty()) {
            newGameTitle.setError(getString(R.string.error_field_required));
            work = false;
        } else if (newDescriptionGame.isEmpty()) {
            newGameDescription.setError(getString(R.string.error_field_required));
            work = false;

        } else {
            work = true;
        }

        if (newReleaseDateGame.isEmpty()) {
            newReleaseDateGame = "Desconocido";
        }

        if (newWebsiteGame.isEmpty()) {
            newWebsiteGame = "Desconocido";
        }

        if (newCategoryGame.isEmpty()) {
            newCategoryGame = "Desconocido";
        }

        if (newPlatformGame.isEmpty()) {
            newPlatformGame = "Desconocida";
        }

        if (work) {
            if (file != null) {

                postNewGameWithPicture(newTitleGame, newDescriptionGame, developer_id, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame);
            } else {

                postNewGame(newTitleGame, newDescriptionGame, developer_id, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame);
            }
        }

    }

    private void postNewGameWithPicture(String newTitleGame, String newDescriptionGame, int developer_id, String newReleaseDateGame, String newWebsiteGame, String newPlatformGame, String newCategoryGame) {
        RequestBody requestBody = null;
        requestBody = RequestBody.create(MediaType.parse(getContentResolver().getType(mSelected.get(0))), file);

        RequestBody game_title = RequestBody.create(null, newTitleGame);
        RequestBody game_description = RequestBody.create(null, newDescriptionGame);
        RequestBody game_developer = RequestBody.create(null, String.valueOf(developer_id));
        RequestBody game_release = RequestBody.create(null, newReleaseDateGame);
        RequestBody game_website = RequestBody.create(null, newWebsiteGame);
        RequestBody game_platform = RequestBody.create(null, newPlatformGame);
        RequestBody game_category = RequestBody.create(null, newCategoryGame);

        MultipartBody.Part cover_image = MultipartBody.Part.createFormData("cover_image", file.getName(), requestBody);
        MultipartBody.Part thumb_image = MultipartBody.Part.createFormData("thumb_image", file.getName(), requestBody);
        MultipartBody.Part game_image = MultipartBody.Part.createFormData("game_image", file.getName(), requestBody);

        Call<ResponseBody> call = client.addGame(game_title,
                game_description,
                game_developer,
                game_release,
                game_website,
                game_platform,
                game_category,
                cover_image,
                thumb_image,
                game_image);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("ok", response.body().toString());
                    Toast.makeText(MainActivity.this, "Elemento agregado con exito", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(true);
                    networkRequest();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    clearForm();

                }


            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                //Log.d("error", t.getLocalizedMessage());

            }
        });
    }

    private void clearForm() {
        newGameTitle.setText(null);
        newGameDescription.setText(null);
        newGameReleaseDate.setText(null);
        newGameWebsite.setText(null);
        newGameCategory.setText(null);
        newTitleGame = null;
        newDescriptionGame = null;
        newReleaseDateGame = null;
        newWebsiteGame = null;
        newPlatformGame = null;
        newCategoryGame = null;
        work = false;
        file = null;
        thumbnail.setVisibility(View.GONE);
    }

    private void postNewGame(String newTitleGame, String newDescriptionGame, int newDeveloperGame, String newReleaseDateGame, String newWebsiteGame, String newPlatformGame, String newCategoryGame) {
        Call<ResponseBody> call = client.addGame(newTitleGame, newDescriptionGame, newDeveloperGame, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame);

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {
                    Log.d("Success", " " + response.toString());
                    Toast.makeText(MainActivity.this, "Elemento agregado con exito", Toast.LENGTH_SHORT).show();
                    refreshLayout.setRefreshing(true);
                    networkRequest();
                    behavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
                    clearForm();
                    bottomSheetButton.setEnabled(true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                bottomSheetButton.setEnabled(true);
            }
        });
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
            startActivity(new Intent(MainActivity.this, StatisticsActivity.class));
        } else if (id == R.id.nav_slideshow) {
            startActivity(new Intent(MainActivity.this, Developers.class));
        } else if (id == R.id.nav_share) {
            Snackbar.make(getCurrentFocus(), "Compartir", Snackbar.LENGTH_SHORT).show();
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

    private void createSpinner(List<DeveloperObject> developerObjects) {
        ArrayList<String> developers = new ArrayList<>();
        for (DeveloperObject object : developerObjects) {
            developers.add(object.getGame_developer());
        }
        developersSpinner.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, developers);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        developersSpinner.setAdapter(arrayAdapter);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (adapterView.getItemAtPosition(i).equals(developerObjects.get(i).getGame_developer())) {
            developer_id = developerObjects.get(i).getId();
        }


    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }


    private void getPicture() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                // Should we show an explanation?
                if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    // Show an explanation to the user *asynchronously* -- don't block
                    // this thread waiting for the user's response! After the user
                    // sees the explanation, try again to request the permission.
                } else {
                    // No explanation needed, we can request the permission.
                    ActivityCompat.requestPermissions(this,
                            new
                                    String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            1);
                }
            } else {
                Matisse.from(MainActivity.this)
                        .choose(MimeType.allOf())
                        .countable(true)
                        .maxSelectable(9)
                        .thumbnailScale(0.85f)
                        .imageEngine(new PicassoEngine())
                        .forResult(REQUEST_CODE_CHOOSE);
            }

        } else {
            Matisse.from(MainActivity.this)
                    .choose(MimeType.allOf())
                    .countable(true)
                    .maxSelectable(9)
                    .thumbnailScale(0.85f)
                    .imageEngine(new PicassoEngine())
                    .forResult(REQUEST_CODE_CHOOSE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] ==
                        PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permisos otorgados para acceder a fotos", Toast.LENGTH_SHORT);
                } else {
                    // permission denied,Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permiso denegado para acceder a fotos", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    List<Uri> mSelected;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_CHOOSE && resultCode == RESULT_OK) {
            mSelected = Matisse.obtainResult(data);
            Log.d("Matisse", "mSelected: " + mSelected);
            String path = getRealPathFromUri(this, mSelected.get(0));
            thumbnail.setVisibility(View.VISIBLE);
            Glide.with(this).load(mSelected.get(0)).into(thumbnail);
            file = new File(path);
            Log.d("Path", path);
        }
    }


    public static String getRealPathFromUri(Context context, Uri contentUri) {
        Cursor cursor = null;
        try {
            String[] proj = {MediaStore.Images.Media.DATA};
            cursor = context.getContentResolver().query(contentUri, proj, null,
                    null, null);
            int column_index =
                    cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
    }

}
