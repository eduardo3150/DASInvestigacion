package com.chavez.eduardo.dasinvestigacion;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.chavez.eduardo.dasinvestigacion.network.DeveloperObject;
import com.chavez.eduardo.dasinvestigacion.network.GameClient;
import com.chavez.eduardo.dasinvestigacion.network.GameObject;
import com.chavez.eduardo.dasinvestigacion.utils.NetworkUtils;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class EditActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private EditText newGameTitle, newGameDescription, newGameReleaseDate, newGameWebsite, newGameCategory;
    private EditText newPlatform;
    AppCompatButton bottomSheetButton, deleteButton;
    private GameObject gameObject;
    private int current_id;
    String newTitleGame, newDescriptionGame, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame;
    boolean work;

    List<DeveloperObject> developerObjects = new ArrayList<>();
    private Spinner developersSpinner;
    private ArrayAdapter arrayAdapter;

    private GameClient client = new Retrofit.Builder()
            .baseUrl(NetworkUtils.BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build().create(GameClient.class);
    private int developer_id = 0;
    private boolean firstAppear = true;
    private int currentPosition;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onBackPressed();
                }
            });
        }

        if (getIntent().getExtras() != null) {
            gameObject = (GameObject) getIntent().getSerializableExtra("object");
        }

        current_id = gameObject.getId();
        newGameTitle = (EditText) findViewById(R.id.newGameName);
        newGameDescription = (EditText) findViewById(R.id.newGameDescription);
        newGameReleaseDate = (EditText) findViewById(R.id.newReleaseDate);
        newGameWebsite = (EditText) findViewById(R.id.newWebsite);
        newGameCategory = (EditText) findViewById(R.id.newCategoria);
        newPlatform = (EditText) findViewById(R.id.newPlatform);
        developersSpinner = (Spinner) findViewById(R.id.developerList);

        setSpinner();

        newGameTitle.setText(gameObject.getGame_name());
        newGameDescription.setText(gameObject.getGame_description());
        newGameReleaseDate.setText(gameObject.getRelease_date());
        newGameWebsite.setText(gameObject.getWebsite());
        newGameCategory.setText(gameObject.getCategory());
        newPlatform.setText(gameObject.getPlatform());

        newTitleGame = gameObject.getGame_name();
        newDescriptionGame = gameObject.getGame_description();
        newReleaseDateGame = gameObject.getRelease_date();
        newWebsiteGame = gameObject.getWebsite();
        newCategoryGame = gameObject.getCategory();
        newPlatformGame = gameObject.getPlatform();


        bottomSheetButton = (AppCompatButton) findViewById(R.id.updateGame);
        deleteButton = (AppCompatButton) findViewById(R.id.deleteItem);
        bottomSheetButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                workAndUpdateItem();

            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                buildDialog();
            }
        });
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

    private void createSpinner(List<DeveloperObject> developerObjects) {
        ArrayList<String> developers = new ArrayList<>();

        for (int i = 0; i < developerObjects.size() ; i++) {
            developers.add(developerObjects.get(i).getGame_developer());

            if (developerObjects.get(i).getGame_developer().equals(gameObject.getGame_developer())){
                currentPosition = i;
            }
        }
        developersSpinner.setOnItemSelectedListener(this);
        arrayAdapter = new ArrayAdapter(this, R.layout.spinner_item, developers);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        developersSpinner.setAdapter(arrayAdapter);
    }

    private void buildDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar elemento")
                .setMessage("¿Desea eliminar: " + gameObject.getGame_name() + "?")
                .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        promptDeletion(current_id);
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .show();
    }


    private void promptDeletion(int id) {
        Call<ResponseBody> call = client.deleteGame(String.valueOf(id));
        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditActivity.this, "Elemento eliminado con exito", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(EditActivity.this, MainActivity.class));
                    EditActivity.this.finish();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {

            }
        });
    }

    private void workAndUpdateItem() {
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
            postUpdatedGame(newTitleGame, newDescriptionGame, developer_id, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame);
            Log.d("HELLO", "HELLO");
        }

    }

    private void postUpdatedGame(String newTitleGame, String newDescriptionGame, int newDeveloperGame, String newReleaseDateGame, String newWebsiteGame, String newPlatformGame, String newCategoryGame) {
        Call<GameObject> call = client.updateGame(String.valueOf(current_id), newTitleGame, newDescriptionGame, newDeveloperGame, newReleaseDateGame, newWebsiteGame, newPlatformGame, newCategoryGame, gameObject.getCover_image(), gameObject.getThumb_image(), gameObject.getGame_image());
        call.enqueue(new Callback<GameObject>() {
            @Override
            public void onResponse(Call<GameObject> call, Response<GameObject> response) {
                if (response.isSuccessful()) {
                    Log.d("Exito", response.body().toString());
                    EditActivity.this.finish();
                    Toast.makeText(EditActivity.this, "Elemento actualizado", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<GameObject> call, Throwable t) {
                Log.d("Error", t.getLocalizedMessage());
            }
        });

    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (firstAppear) {
            developersSpinner.setSelection(currentPosition);
            developer_id = currentPosition;
            firstAppear = false;
        }
        if (adapterView.getItemAtPosition(i).equals(developerObjects.get(i).getGame_developer())) {
            developer_id = developerObjects.get(i).getId();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
